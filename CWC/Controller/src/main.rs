extern crate prometheus;
#[macro_use]
extern crate log;

mod config;
mod data_structs;
mod grpc;
mod keep_alive;
mod metric_endpoint;
mod pre_selector;
mod pressure_evaluator;
mod ra_manager;
mod sc_grpc;
mod scheduler_distributor;
mod tracker;
mod utils;
mod visual_apis;

use config::Configure;
use flume::unbounded;
use grpc::scheduler_controller::scheduler_controller_server::SchedulerControllerServer;
use keep_alive::KeepAliveServer;
use metric_endpoint::PrometheusRegistry;
use pre_selector::SchedulerPreSelector;
use pressure_evaluator::PressureEvaluator;
use prometheus::{IntGauge, Opts, Registry};
use ra_manager::RAManager;
use scheduler_distributor::SchedulerDistributor;
use std::collections::HashSet;
use std::sync::Arc;
use tokio::sync::RwLock;
use tonic::transport::Server;
use tracker::Tracker;
use visual_apis::VisualWorkflow;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // 从环境变量里读取的全局配置信息
    let config = Arc::new(Configure::new());

    // 初始化日志记录
    fern::Dispatch::new()
        .format(|out, message, record| {
            out.finish(format_args!(
                "{}[{}][{}] {}",
                chrono::Local::now().format("[%Y-%m-%d][%H:%M:%S]"),
                record.target(),
                record.level(),
                message
            ))
        })
        .level(match &config.log_level[..] {
            "ERROR" => log::LevelFilter::Error,
            "WARN" => log::LevelFilter::Warn,
            "DEBUG" => log::LevelFilter::Debug,
            _ => log::LevelFilter::Info,
        })
        .level_for("hyper", log::LevelFilter::Info)
        .level_for("h2", log::LevelFilter::Info)
        .level_for("tower_buffer", log::LevelFilter::Info)
        .chain(std::io::stdout())
        .apply()?;

    let r = Registry::new();

    let wf_count_in_queue = IntGauge::with_opts(Opts::new(
        "wf_count_in_queue",
        "wf_count_in_queue workflow queue length",
    ))
    .unwrap();
    r.register(Box::new(wf_count_in_queue.clone())).unwrap();

    // 调度器黑名单
    // 由压力评价器写入，调度器状态收集器(keep_alive)读取
    // 不能放在压力评价器里构建，必须放在 main 里构建，否则会有循环引用的问题
    let scheduler_blacklist = Arc::new(RwLock::new(HashSet::new()));

    // 构建·保活器gRPC服务点(Endpoint)
    let mut kas = KeepAliveServer::new(scheduler_blacklist.clone(), config.clone());
    kas.register_metric(&r);

    // 构建·资源分配器管理器·
    let ra_mngr = Arc::new(RAManager::new(config.clone()));
    ra_mngr.register_metric(&r);

    // 构建工作流队列
    // 工作流队列设定为 mpsc 模型
    // 在主函数里构建 mpsc 的channel：
    //     1. 连接 channel 的 sender(tx), receiver(rx) 需要被分别处理
    //     2. receiver(rx) 分配到 “工作流分配器”
    //     3. sender(tx) 的 clone 分配到 “调度器分配器，调度器状态跟踪器”
    let (tx_wf, rx_wf) = unbounded::<sc_grpc::WorkflowBinaryArray>();

    // 构建·调度器状态跟踪器·工作线程
    let trckr = Tracker::new(
        kas.get_state_tracking_table(),
        tx_wf.clone(),
        wf_count_in_queue.clone(),
        config.clone(),
        ra_mngr.clone(),
        scheduler_blacklist.clone(),
    );
    trckr.register_metric(&r);
    kas.set_scheduler_register_table(trckr.get_scheduler_register_table());

    // 构建·调度器预选器·工作线程
    let psltr = SchedulerPreSelector::new(trckr.get_scheduler_register_table());
    psltr.register_metric(&r);

    // 构建·调度器分配器·工作线程
    let mut sd = SchedulerDistributor::new(
        psltr.get_preselected_table(),
        trckr.get_scheduler_register_table(),
        rx_wf,
        tx_wf.clone(),
        wf_count_in_queue.clone(),
        config.clone(),
    );
    sd.register_metric(&r);

    let visual_workflow =
        VisualWorkflow::new(trckr.get_scheduler_register_table().clone(), config.clone());

    // 需要更新的镜像队列
    // 收到harbor由webhook发送的事件后,把image name放入队列
    // 压力评价器从队列中读入image name,如果是调度器的镜像,则关闭旧调度器,开启新调度器
    let (tx, rx) = unbounded::<String>();

    // 构建&启动·压力评价器·工作线程
    let mut pe = PressureEvaluator::new(
        trckr.get_scheduler_register_table(),
        psltr.get_preselected_table(),
        scheduler_blacklist,
        wf_count_in_queue.clone(),
        trckr.get_wf_count_per_scheduler(),
        config.clone(),
        rx,
        ra_mngr.clone(),
    );
    pe.register_metric(&r);
    tokio::spawn(async move {
        pe.run().await;
    });

    // 构建·gRPC Server·
    let sc_grpc = sc_grpc::SchedulerControllerGRPC::new(
        kas,
        tx_wf.clone(),
        wf_count_in_queue.clone(),
        visual_workflow,
        ra_mngr.clone(),
    );
    sc_grpc.register_metric(&r);

    // 启动·调度器分配器·工作线程
    tokio::spawn(async move {
        sd.run().await;
    });

    // 启动·调度器状态跟踪器·工作线程
    tokio::spawn(async move {
        trckr.run().await;
    });

    // 启动·调度器预选器·工作线程
    tokio::spawn(async move {
        psltr.run().await;
    });

    // 启动·资源分配器管理器·工作线程
    tokio::spawn(async move {
        ra_mngr.run().await;
    });

    // 启动·gRPC Server·
    tokio::spawn(async move {
        info!("启动 gRPC");
        metric_endpoint::run(r, tx).await;
    });

    let addr = r#"0.0.0.0:6060"#.parse().unwrap();
    Server::builder()
        .add_service(SchedulerControllerServer::new(sc_grpc))
        .serve(addr)
        .await?;

    Ok(())
}
