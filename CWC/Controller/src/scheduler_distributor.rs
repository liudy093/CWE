use crate::config::Configure;

use super::grpc::scheduler::{scheduler_client::SchedulerClient, WorkflowTransferRequest};
use super::keep_alive::unix_timestamp;
use super::metric_endpoint::PrometheusRegistry;
use super::pre_selector::SchedulerPreSelectedTable;
use super::sc_grpc::WorkflowBinaryArray;
use super::tracker::SchedulerRegisterTable;
use flume::{Receiver, Sender};
use prometheus::{IntCounter, IntGauge, Opts, Registry};
use std::sync::Arc;
use tokio::sync::{Mutex, RwLock};
use tokio::time;

pub struct SchedulerDistributor {
    /// 调度器预选表的一个ARC引用（来自 pre_selector）
    pre_selected_table: Arc<RwLock<SchedulerPreSelectedTable>>,

    /// 调度器注册表的一个ARC引用（来自 tracker）
    scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,

    /// 工作流队列 mpsc 接收端
    //FIXME: 如何去掉 mutex ?
    wf_queue_receiver: Mutex<Receiver<WorkflowBinaryArray>>,

    /// 工作流队列 mpsc 发送端
    wf_queue_sender: Sender<WorkflowBinaryArray>,

    /// 工作流队列内多少工作流待发送（缓存工作流数量），本模块内：读、写
    wf_count_in_queue: IntGauge,

    /// Prometheus 指标：发送失败的工作流计数
    failed_counter: IntCounter,

    /// Prometheus 指标：已发送的工作流的计数
    sended_workflow_counter: IntCounter,

    /// Prometheus 指标：正在发送的工作流的数量
    sending_workflow_counter: IntGauge,

    /// Prometheus: 调度器分配器 工作次数计数
    scheduler_distributor_work_count: IntCounter,

    /// 全局配置
    global_conf: Arc<Configure>,
}

impl SchedulerDistributor {
    pub fn new(
        pre_selected_table: Arc<RwLock<SchedulerPreSelectedTable>>,
        scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,
        wf_queue_receiver: Receiver<WorkflowBinaryArray>,
        wf_queue_sender: Sender<WorkflowBinaryArray>,
        wf_count_in_queue: IntGauge,
        global_conf: Arc<Configure>,
    ) -> SchedulerDistributor {
        SchedulerDistributor {
            pre_selected_table,

            scheduler_register_table,

            wf_queue_receiver: Mutex::new(wf_queue_receiver),
            wf_queue_sender,

            wf_count_in_queue,

            failed_counter: IntCounter::with_opts(Opts::new("failed_counter", "failed counter"))
                .unwrap(),

            sended_workflow_counter: IntCounter::with_opts(Opts::new(
                "sended_workflow_counter",
                "sended workflow number",
            ))
            .unwrap(),

            sending_workflow_counter: IntGauge::with_opts(Opts::new(
                "sending_workflow_counter",
                "正在发送的工作流数量",
            ))
            .unwrap(),

            scheduler_distributor_work_count: IntCounter::with_opts(Opts::new(
                "scheduler_distributor_work_count",
                "scheduler distributor work count",
            ))
            .unwrap(),

            global_conf: global_conf,
        }
    }

    /// 从队列中取指定数量的工作流
    async fn fetch_workflows(&self, num: u32) -> Vec<WorkflowBinaryArray> {
        let mut workflows: Vec<WorkflowBinaryArray> = Vec::new();

        for _i in 0..num {
            if let Ok(wf) = self.wf_queue_receiver.lock().await.try_recv() {
                workflows.push(wf)
            }
        }
        if workflows.len() > 0 {
            self.wf_count_in_queue.sub(workflows.len() as i64);
            info!(
                "[调度器分配器] 从·工作流·队列中取出 {} 个工作流（希望取出 {}）",
                workflows.len(),
                num
            );
        }

        workflows
    }

    /// 通过 grpc 向调度器发送工作流
    fn send_workflow_to_scheduler(
        &self,
        cluster_id: &str,
        sch_id: &str,
        scheduler_host: &str,
        workflows: Vec<WorkflowBinaryArray>,
    ) {
        let wf_queue_sender = self.wf_queue_sender.clone();
        let wf_count_in_queue = self.wf_count_in_queue.clone();
        let sch_address = format!("http://{}", scheduler_host);
        let cluster_id = cluster_id.to_owned();
        let sch_id = sch_id.to_string();
        let sended_workflow_counter = self.sended_workflow_counter.clone();
        let sending_workflow_counter = self.sending_workflow_counter.clone();
        let failed_counter = self.failed_counter.clone();

        // 启动 Future 处理发送任务
        tokio::spawn(async move {
            let total_sended_num = workflows.len() as u32;
            debug!(
                "[调度器分配器] 准备向集群 {} 的调度器 [{}] 发送 {} 个工作流",
                cluster_id, sch_id, total_sended_num
            );
            sending_workflow_counter.add(total_sended_num as i64);

            if let Ok(mut client) = SchedulerClient::connect(sch_address.clone()).await {
                let request = tonic::Request::new(WorkflowTransferRequest {
                    sid: sch_id.clone(),
                    // 需要复制出一份，发送过程中出现问题了，可以再送回队列
                    // FIXME 如何去掉复制操作？
                    workflow: workflows.clone(),
                });
                match client.workflow_transfer(request).await {
                    Ok(response) => {
                        if response.into_inner().accept > 0 {
                            sended_workflow_counter.inc_by(total_sended_num as u64);
                            sending_workflow_counter.sub(total_sended_num as i64);
                            debug!(
                                "[调度器分配器] 已向集群 {} 的调度器 [{}]({}) 发送 {} 个工作流",
                                cluster_id, sch_id, sch_address, total_sended_num
                            );
                            return;
                        } else {
                            warn!(
                                "[调度器分配器] 集群 {} 的调度器 [{}]({}) 未能接受工作流(accept<=0)，将重新分配工作流！",
                                cluster_id, sch_id, sch_address
                            );
                        }
                    }
                    Err(err) => {
                        error!(
                            "[调度器分配器] 向集群 {} 的调度器 [{}]({}) 发送工作流失败 {:?}！",
                            cluster_id, sch_id, sch_address, err
                        );
                    }
                }
            } else {
                error!(
                    "[调度器分配器] 准备向集群 {} 的调度器 [{}]({}) 发送工作流，但连接失败！",
                    cluster_id, sch_id, sch_address
                );
            }

            sending_workflow_counter.sub(total_sended_num as i64);

            // 无法连接调度器，所有工作流重新入队
            failed_counter.inc_by(total_sended_num as u64);
            warn!(
                "[调度器分配器] {} 个工作流发送失败，重新放入队列！",
                total_sended_num
            );
            let wf_count = workflows.len() as i64;
            wf_count_in_queue.add(wf_count);
            for wf in workflows.into_iter() {
                // 如果无法重新入队，说明发送一端已终止，这种情况下一定是程序出问题了，这里直接杀死程序就可以
                wf_queue_sender.send(wf).unwrap();
            }
        });
    }

    async fn distribute(&self) -> bool {
        // 如果已经没有了排队工作流，不再做任何操作
        if self.wf_count_in_queue.get() == 0 {
            return false;
        }

        // 从预选表中获取所有调度器
        let mut alived_sch_count = 0u32;
        let mut pst = self.pre_selected_table.write().await;
        for pst_sch_info in pst.iter_mut() {
            // 需要先验证调度器是否存活
            // 跳过不存活的调度器
            let mut srt = self.scheduler_register_table.write().await;
            if let Some(sch_info) = srt.get_mut(&pst_sch_info.sid) {
                let cap = if self.global_conf.balance_method == "abs" {
                    debug!("[调度器分配器] abs 模式下只取一个工作流，注意性能问题");
                    1
                } else {
                    pst_sch_info.valid_cap
                };

                // 验证调度器依然存活的同时，需要同时验证调度器承载力
                // 因为上一次迭代过程中，调度器分配器会修改注册表内调度器的承载力
                if cap > 0 {
                    // fetch_workflows 函数可能会阻塞
                    // 调用前需要保证没有锁住任何数据结构，否则可能会导致死锁
                    let workflows = self.fetch_workflows(cap).await;
                    let fetched_workflow_count = workflows.len() as u32;

                    // 无论是否发送成功，都需要核减调度器注册表中调度器承载力
                    // 因为状态跟踪器更新注册表的时延很长，如果不在此处核减，可能导致超过调度器的承载力，调度器压力过大
                    // 如果因调度器分配器没有发送成功，导致异常核减问题，可由状态跟踪器根据保活信号自动纠正
                    pst_sch_info.valid_cap -= if fetched_workflow_count > 0 {
                        self.send_workflow_to_scheduler(
                            &pst_sch_info.cluster_id,
                            &pst_sch_info.sid,
                            &pst_sch_info.ipv4,
                            workflows,
                        );
                        fetched_workflow_count
                    } else {
                        warn!("[调度器分配器] 本次未取到工作流");
                        0
                    };

                    if self.global_conf.balance_method == "abs" {
                        // 如果abs模式，让容量直接归零，强制进入下一轮
                        debug!(
                            "[调度器分配器] 调度器内还剩 {} 可接收容量，但在 abs 模式下强制归零",
                            pst_sch_info.valid_cap
                        );
                        pst_sch_info.valid_cap = 0;
                    }

                    // 如果当前调度器还有容纳空间，可以继续循环
                    if pst_sch_info.valid_cap > 0 {
                        alived_sch_count += 1;
                    }

                    sch_info.capacity = pst_sch_info.valid_cap;
                }
            }
        }

        alived_sch_count > 0
    }

    pub async fn run(&mut self) {
        loop {
            // 分发工作流
            // 分发完一次后，
            // 如果有分发，不需要等待
            // 如果没有分发，表明当前系统中没有调度器，没必要再次分发任务，等待2秒再试
            self.scheduler_distributor_work_count.inc();
            if !self.distribute().await {
                info!("[调度器分配器] 没有可用的调度器或队列为空，将等待2秒后再次检测");

                let tp = unix_timestamp(); //前一次时间
                time::sleep(time::Duration::new(1, 0)).await;
                let tc = unix_timestamp(); //当前时间
                if tc - tp > 4 {
                    warn!("[调度器分配器] 等待时延超过正常情况2倍");
                }
            }
        }
    }
}

impl PrometheusRegistry for SchedulerDistributor {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.failed_counter.clone())).unwrap();
        r.register(Box::new(self.sended_workflow_counter.clone()))
            .unwrap();
        r.register(Box::new(self.sending_workflow_counter.clone()))
            .unwrap();
        r.register(Box::new(self.scheduler_distributor_work_count.clone()))
            .unwrap();
    }
}

#[cfg(test)]
mod tests {
    use super::super::pre_selector::{SchedulerPreSelectedItem, SchedulerPreSelectedTable};
    use super::super::tracker::{SchedulerRegisterItem, SchedulerRegisterTable};
    use super::*;
    use flume::unbounded;

    async fn __make_scheduler_distributor() -> SchedulerDistributor {
        let (tx, rx) = unbounded::<WorkflowBinaryArray>();
        let wf_count_in_queue = IntGauge::with_opts(Opts::new(
            "wf_count_in_queue",
            "wf_count_in_queue workflow queue length",
        ))
        .unwrap();
        let config = Arc::new(Configure::default());
        let sd = SchedulerDistributor::new(
            Arc::new(RwLock::new(SchedulerPreSelectedTable::new())),
            Arc::new(RwLock::new(SchedulerRegisterTable::new())),
            rx,
            tx,
            wf_count_in_queue,
            config,
        );

        // 预选表内，注册表内均有 3 个调度器：1，2，3
        {
            let mut spst = sd.pre_selected_table.write().await;
            for i in 1..=3 {
                spst.push(SchedulerPreSelectedItem {
                    sid: i.to_string(),
                    ipv4: String::from("192.168.1.101"),
                    valid_cap: i,
                    cluster_id: String::from("123"),
                });
            }
            let mut srt = sd.scheduler_register_table.write().await;
            for i in 1..=3 {
                srt.insert(
                    i.to_string(),
                    SchedulerRegisterItem {
                        pressure: 0,
                        capacity: i,
                        ipv4: String::from("192.168.1.101"),
                        state: crate::tracker::SchedulerState::Running,
                        cluster_id: String::from("123"),
                        keep_alive_delay: 1,
                    },
                );
            }
        }

        sd
    }

    #[tokio::test]
    // 测试在发送前，检测到有的调度器已经死亡（不在调度器注册表里）
    async fn test_some_scheduler_dead_before_send() {
        let sd = __make_scheduler_distributor().await;

        // 从注册表里去除 3 号调度器，模拟死亡
        {
            let mut srt = sd.scheduler_register_table.write().await;
            srt.remove("3");

            // 工作流队列内放入 6 个工作流（1+2+3）
            for i in 1..=6 {
                sd.wf_queue_sender.send(vec![i]).unwrap();
            }
            sd.wf_count_in_queue.add(6);
        }

        // 执行分配
        sd.distribute().await;

        // 因 3 号调度器已经死亡
        // 1，2 号调度器未能成功发送工作流，任务
        // 队列中应该还剩 6 个工作流（4，5，6，1，2，3）
        // 但使用的 tokio 的单线程测试模型，没有机会执行发送函数
        // 最终，队列里应该只有 3 个工作流（4，5，6）
        let except_wf_contents = vec![4, 5, 6];
        let mut recved_wf_contents: Vec<i32> = Vec::new();
        for _i in 1u32..=3u32 {
            let wf = sd.wf_queue_receiver.lock().await.try_recv().unwrap();
            recved_wf_contents.push(wf[0] as i32);
        }
        assert!(recved_wf_contents.len() == 3);
        assert_eq!(except_wf_contents, recved_wf_contents);

        // 无论是否发送成功，调度器注册表中所有控制器的承载力都应该已经清零
        for (_, sch_info) in sd.scheduler_register_table.read().await.iter() {
            assert_eq!(sch_info.capacity, 0);
        }
    }
}
