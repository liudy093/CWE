use crate::ra_manager::{RAManager, StateChanged, StateChangedMap};

use super::config::Configure;
use super::keep_alive::{unix_timestamp, SchedulerStateTrackingTable};
use super::metric_endpoint::PrometheusRegistry;
use super::sc_grpc::WorkflowBinaryArray;
use flume::Sender;
use prometheus::{Gauge, IntCounter, IntGauge, Opts, Registry};
use redis::{cluster::ClusterClient, Commands, RedisResult};
use std::collections::{HashMap, HashSet};
use std::sync::Arc;
use tokio::sync::{Mutex, RwLock};
use tokio::time::{sleep, Duration};

type SchedulerID = String;
pub type SchedulerRegisterTable = HashMap<SchedulerID, SchedulerRegisterItem>;

#[derive(Debug)]
pub enum SchedulerState {
    StableWaiting, // 0 刚诞生状态，需要等待稳定（至少3个周期的保活信号）
    Running,       // 1
    TimeWaiting,   //2 等待关闭
}

//调度器注册表
#[derive(Debug)]
pub struct SchedulerRegisterItem {
    // 调度器自测压力值（0~100，整数）
    pub pressure: u32,
    // 调度器承载力（还可以承载/接收多少工作流，单位：个，整数）
    pub capacity: u32,
    // ipv4地址
    pub ipv4: std::string::String,
    /// 调度器状态
    pub state: SchedulerState,
    /// 集群id
    pub cluster_id: String,
    /// 准备告知调度器的发送保活信号的时延（秒）
    pub keep_alive_delay: u32,
}

pub struct Tracker {
    /// 状态跟踪表的一个ARC引用（来自KeepAlive GRPC接收模块）
    state_tracking_table: Arc<Mutex<SchedulerStateTrackingTable>>,

    /// 调度器注册表
    scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,

    /// 工作流队列内多少工作流待发送（缓存工作流数量），本模块内：写
    wf_count_in_queue: IntGauge,

    /// Prometheus 指标：当前系统内调度器数量
    scheduler_num: Gauge,

    /// Prometheus 指标：诞生的调度器计数
    born_counter: IntCounter,

    /// Prometheus 指标：死亡调度器计数
    dead_counter: IntCounter,

    /// Prometheus 指标：Tracker 工作次数计数
    tracker_work_count: IntCounter,

    /// Prometheus 指标：平均每个调度器可承载的工作流数量
    wf_count_per_scheduler: IntGauge,

    /// 工作流队列 sender
    workflow_queue_sender: Sender<WorkflowBinaryArray>,

    /// 资源分配器管理器的一个引用(来自 ra_manager 模块)
    ra_manager: Arc<RAManager>,

    /// 全局配置
    global_conf: Arc<Configure>,

    /// 调度器黑名单的一个ARC引用（来自 main 模块）
    scheduler_blacklist: Arc<RwLock<HashSet<String>>>,
}

impl Tracker {
    pub fn new(
        state_tracking_table: Arc<Mutex<SchedulerStateTrackingTable>>,
        sender: Sender<WorkflowBinaryArray>,
        wf_count_in_queue: IntGauge,
        global_conf: Arc<Configure>,
        ra_manager: Arc<RAManager>,
        scheduler_blacklist: Arc<RwLock<HashSet<String>>>,
    ) -> Tracker {
        let trckr = Tracker {
            state_tracking_table,
            scheduler_register_table: Arc::new(RwLock::new(HashMap::new())),
            wf_count_in_queue: wf_count_in_queue,

            scheduler_num: Gauge::with_opts(Opts::new("scheduler_num", "Scheduler Numbers"))
                .unwrap(),

            born_counter: IntCounter::with_opts(Opts::new(
                "born_counter",
                //给Prometheus看的，目的是可观可测
                "born counter",
                //给开发者看的
            ))
            .unwrap(),

            dead_counter: IntCounter::with_opts(Opts::new(
                "dead_counter",
                //给Prometheus看的，目的是可观可测
                "dead counter",
                //给开发者看的
            ))
            .unwrap(),

            tracker_work_count: IntCounter::with_opts(Opts::new(
                "tracker_work_count",
                //给Prometheus看的，目的是可观可测
                "tracker work counter",
                //给开发者看的
            ))
            .unwrap(),

            wf_count_per_scheduler: IntGauge::with_opts(Opts::new(
                "wf_count_per_scheduler",
                "allowed workflows per scheduler",
            ))
            .unwrap(),

            workflow_queue_sender: sender,
            ra_manager,
            global_conf,
            scheduler_blacklist,
        };
        trckr.wf_count_per_scheduler.set(100);
        trckr
    }

    /// 调整平均每个调度器能承载工作流数量
    /// 使用 指数加权移动平均 方法
    fn caculate_wf_per_scheduler(&self, v_t: u32) {
        let v_t_0 = self.wf_count_per_scheduler.get() as f64;
        let v_t_1 = self.global_conf.wf_per_scheduler_caculate_weight * v_t_0
            + (1f64 - self.global_conf.wf_per_scheduler_caculate_weight) * (v_t as f64);
        self.wf_count_per_scheduler.set(v_t_1 as i64);
    }

    /// 读取调度器状态表，判断该调度器是否是新诞生
    /// 如果新诞生，将其插入调度器注册表中
    /// 如果已存在，则进行生、死诊断
    async fn scheduler_idcheck(&self) -> StateChangedMap {
        let mut state_changed_schedulers: StateChangedMap = HashMap::new();

        //遍历状态跟踪表，找到所有死亡的调度器，id放入数组中，以便删除
        let mut deaded_sch_ids = Vec::new();
        let mut sch_trck_table = self.state_tracking_table.lock().await;
        let mut sch_register_table = self.scheduler_register_table.write().await;
        debug!(
            "[状态跟踪器] 开始检测调度器生死状态，当前`调度器跟踪表`内包含{}个调度器条目，`调度器注册表`内包含{}个调度器",
            sch_trck_table.keys().len(),
            sch_register_table.keys().len()
        );
        for (sch_id, val) in sch_trck_table.iter() {
            if let Some(tracking_item) = val.last() {
                //检测死亡状态的调度器，将其从调度器注册表删除
                //并将工作流状态数据库中，取出分配给死亡调度器的工作流，重新送入“DAG”队列
                let tl = tracking_item.timestamp; //状态表的时间戳
                let ts = unix_timestamp(); //当前时间
                debug!(
                    "[状态跟踪器] 检测调度器 [{}] 生死状态，时间戳: {}, 当前时间: {}, offset: {}",
                    sch_id,
                    tl,
                    ts,
                    ts - tl
                );
                if (ts - tl)
                    > (self.global_conf.keep_alive_cycle * self.global_conf.secs_per_cycle) as u64
                {
                    info!(
                        "[状态跟踪器] 调度器 [{}] 死亡，从调度器注册表中删除",
                        sch_id
                    );
                    deaded_sch_ids.push(sch_id.to_owned());
                    self.dead_counter.inc();

                    // 调度器死亡后，需要告知·资源分配器管理器·
                    self.ra_manager
                        .notify_scheuler_deaded(&tracking_item.cluster_id, sch_id)
                        .await;

                    // 判定调度器死亡后，需要加入黑名单，防止意外复活
                    {
                        let mut sch_blk_lst = self.scheduler_blacklist.write().await;
                        sch_blk_lst.insert(sch_id.clone());
                    }

                    // 根据“资源分配器模块”的需求，当检测到调度器死亡后，
                    // 通知“资源分配器模块”，调度器状态改变
                    // 此处收集所有死亡的调度器，并设定其存活状态为 false
                    state_changed_schedulers
                        .entry(tracking_item.cluster_id.clone())
                        .or_insert_with(|| Vec::new())
                        .push(StateChanged(sch_id.clone(), false));

                    match fetch_workflow_from_redis(sch_id, &self.global_conf.redis_host).await {
                        Ok(wkflows) => {
                            info!(
                                "已从Redis中取出分配给死亡调度器 [{}] 的 {} 个工作流，并送入缓存队列",
                                sch_id,
                                wkflows.len()
                            );
                            let wf_count = wkflows.len();
                            self.wf_count_in_queue.add(wf_count as i64);
                            for wf in wkflows {
                                self.workflow_queue_sender.send(wf).unwrap();
                            }
                        }
                        Err(e) => {
                            error!(
                                "[状态跟踪器] 从redis里获取调度器 [{}] 的工作流失败: {:?}",
                                sch_id, e
                            );
                        }
                    }
                }
                //检测到调度器状态表里生存的调度器，更新调度器注册表
                else {
                    let sch_state = SchedulerState::Running;
                    //检测到新诞生的调度器，打入调度器注册表
                    if !sch_register_table.contains_key(sch_id) {
                        self.born_counter.inc();
                        debug!("[状态跟踪器] 检测到新调度器 [{}]，存入调度器注册表", sch_id);
                        // TODO 新诞生的调度器需要经过稳定期后，再进入运行状态
                        // sch_state = ...

                        // 调度器诞生后，调整平均每个调度器能承载工作流数量
                        // 使用 指数加权移动平均 方法
                        self.caculate_wf_per_scheduler(tracking_item.capacity);

                        // 调度器诞生后，需要告知·资源分配器管理器·
                        self.ra_manager
                            .notify_scheduler_started(&tracking_item.cluster_id, sch_id)
                            .await;

                        // 根据“资源分配器模块”的需求，当检测到调度器诞生后，
                        // 通知“资源分配器模块”，调度器状态改变
                        // 此处收集所有新诞生的调度器，并设定其存活状态为 true
                        state_changed_schedulers
                            .entry(tracking_item.cluster_id.clone())
                            .or_insert_with(|| Vec::new())
                            .push(StateChanged(sch_id.clone(), true));
                    } else {
                        info!(
                            "[状态跟踪器] 根据调度器 [{}] 的序列号为 {} 保活信号，更新调度器注册表",
                            sch_id, tracking_item.serial_number
                        );
                    }

                    let sr = SchedulerRegisterItem {
                        pressure: tracking_item.pressure,
                        capacity: tracking_item.capacity,
                        ipv4: tracking_item.ipv4.clone(),
                        state: sch_state,
                        cluster_id: tracking_item.cluster_id.clone(),
                        // 根据调度器的sch id，把调度器分配器到 keep_alive_cycle 个周期内
                        // keep_alive_delay 的最小值是1, sch_id_to_hash_bucket最小值是0
                        keep_alive_delay: sch_id_to_hash_bucket(
                            sch_id,
                            self.global_conf.keep_alive_cycle * self.global_conf.secs_per_cycle,
                        )
                        .max(1u32),
                    };
                    debug!(
                        "[状态跟踪器] 调度器 [{}] 本次告知当前压力为 {}, 可用容量为 {}",
                        sch_id, tracking_item.pressure, tracking_item.capacity
                    );
                    sch_register_table.insert(sch_id.to_string(), sr);
                }
            } else {
                warn!(
                    "[状态跟踪器] 从状态跟踪表中取调度器 {} 的最后一次保活信号失败！",
                    sch_id
                )
            }
        }

        // 从调度器注册表和状态跟踪表中删除所有死亡的调度器
        for sch_id in deaded_sch_ids.iter() {
            sch_register_table.remove(sch_id);
            sch_trck_table.remove(sch_id);
        }

        self.scheduler_num.set(sch_register_table.len() as f64);

        state_changed_schedulers
    }

    pub async fn run(&self) {
        loop {
            self.tracker_work_count.inc();
            self.ra_manager
                .send_change_event_to_ra(self.scheduler_idcheck().await)
                .await;

            let tp = unix_timestamp(); //前一次时间
            sleep(Duration::new(2, 0)).await;
            let tc = unix_timestamp(); //当前时间
            if tc - tp > 4 {
                warn!("[状态跟踪器] 等待时延超过正常情况2倍");
            }
        }
    }

    pub fn get_scheduler_register_table(&self) -> Arc<RwLock<SchedulerRegisterTable>> {
        self.scheduler_register_table.clone()
    }

    pub fn get_wf_count_per_scheduler(&self) -> IntGauge {
        self.wf_count_per_scheduler.clone()
    }
}

impl PrometheusRegistry for Tracker {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.scheduler_num.clone())).unwrap();
        r.register(Box::new(self.dead_counter.clone())).unwrap();
        r.register(Box::new(self.born_counter.clone())).unwrap();
        r.register(Box::new(self.tracker_work_count.clone()))
            .unwrap();
        r.register(Box::new(self.wf_count_per_scheduler.clone()))
            .unwrap();
    }
}

async fn fetch_workflow_from_redis(
    sch_id: &str,
    redis_hosts: &Vec<String>,
) -> RedisResult<Vec<Vec<u8>>> {
    let mut all_wf: Vec<Vec<u8>> = Vec::new();
    let redis_client = ClusterClient::open(redis_hosts.to_owned())?;
    let mut con = redis_client.get_connection()?;
    let all_keys: Vec<String> = con.keys(format!("{}-*-A", sch_id))?;
    for k in all_keys.iter() {
        let x: Vec<u8> = con.get(k)?;
        all_wf.push(x);

        // 取出的工作流要从 redis 里删除
        con.del(k)?;
    }

    debug!(
        "[状态跟踪器] 从Redis里获取 {} 条 [{}-*-A] 模式的记录",
        all_wf.len(),
        sch_id
    );

    Ok(all_wf)
}

fn sch_id_to_hash_bucket(sch_id: &str, bucket_num: u32) -> u32 {
    sch_id
        .chars()
        .map(|c| {
            c.to_digit(10).expect(
                format!(
                    "[状态跟踪器] 调度器id转hash时发现一个无法转成数字的字符 {}",
                    c
                )
                .as_str(),
            )
        })
        .fold(0, |acc, num| num ^ acc)
        % bucket_num
}

//测试模块
#[cfg(test)]
mod tests {
    use super::super::data_structs::circular_queue::CircularQueue;
    use super::super::keep_alive::{unix_timestamp, SchedulerStateTrackingItem};
    use super::*;
    use flume::{unbounded, Receiver};
    use redis::{cluster::ClusterClient, Commands};
    use std::{
        collections::{HashMap, HashSet},
        vec,
    };

    fn __make_state_circular_queue(timestamp: u64) -> CircularQueue<SchedulerStateTrackingItem> {
        let mut cq = CircularQueue::with_capacity(20);
        for j in 1u32..=10u32 {
            cq.push(SchedulerStateTrackingItem {
                pressure: j * 10,
                capacity: 100,
                serial_number: j as u64,
                ipv4: String::from("192.168.1.101"),
                timestamp,
                cluster_id: String::from("123"),
            });
        }
        cq
    }

    fn __wirte_workflow_to_redis(
        sch_id: &str,
        wf_id: &str,
        redis_hosts: Vec<String>,
        workflow: Vec<u8>, // 工作流的内容设定为一个字节的数字
    ) -> RedisResult<()> {
        let redis_client = ClusterClient::open(redis_hosts)?;
        let mut con = redis_client.get_connection()?;
        let _: () = con.set(format!("{}-{}-A", sch_id, wf_id), workflow)?;
        Ok(())
    }

    fn __make_tracker() -> (Tracker, Receiver<WorkflowBinaryArray>) {
        // 状态跟踪表
        let sstt = Arc::new(Mutex::new(HashMap::new()));

        let (tx, rx) = unbounded::<WorkflowBinaryArray>();
        let config = Arc::new(Configure::default());
        let wf_count_in_queue = IntGauge::with_opts(Opts::new(
            "wf_count_in_queue",
            "wf_count_in_queue workflow queue length",
        ))
        .unwrap();
        let sch_blk_list = Arc::new(RwLock::new(HashSet::new()));
        let trckr = Tracker::new(
            sstt,
            tx,
            wf_count_in_queue,
            config.clone(),
            Arc::new(RAManager::new(config.clone())),
            sch_blk_list,
        );

        (trckr, rx)
    }

    async fn __sync_tracking_state_to_register_table(
        tracker: &Tracker,
        which_schedulers: &[String],
    ) {
        let sch_state_trck_table = tracker.state_tracking_table.lock().await;
        let mut sch_register_table = tracker.scheduler_register_table.write().await;

        for sch_id in which_schedulers.iter() {
            let state_item = sch_state_trck_table.get(sch_id).unwrap();
            if let Some(tracking_item) = state_item.last() {
                let sri = SchedulerRegisterItem {
                    pressure: tracking_item.pressure,
                    capacity: tracking_item.capacity,
                    ipv4: tracking_item.ipv4.clone(),
                    state: SchedulerState::Running,
                    cluster_id: String::from("123"),
                    keep_alive_delay: 1,
                };
                sch_register_table.insert(sch_id.to_string(), sri);
            }
        }
    }

    #[tokio::test]
    // 测试发现新诞生调度器的能力
    async fn test_detect_born_scheduler() {
        let (trckr, _) = __make_tracker();

        // 状态跟踪表 存储 3 个保活信号
        // 第 3 个预设为新保活信号，预示着新调度器的诞生（上线）
        {
            let mut stt = trckr.state_tracking_table.lock().await;
            for i in 1u8..=3u8 {
                let cq = __make_state_circular_queue(unix_timestamp());
                stt.insert(i.to_string(), cq);
            }
        }

        // 向调度器注册表内写入 2 个调度器信息（id分别为 1，2）
        __sync_tracking_state_to_register_table(&trckr, &["1".to_string(), "2".to_string()]).await;

        // 检测调度器生死状态
        // 在调度器注册表中，应该出现 3 号调度器
        trckr.scheduler_idcheck().await;
        assert!(trckr
            .scheduler_register_table
            .read()
            .await
            .contains_key("3"));
    }

    #[tokio::test]
    // 测试发现调度器死亡的能力，及从 redis 读取分配给死亡调度器的工作流
    async fn test_fetch_workflow_after_scheduler_dead() {
        let (trckr, mut rx) = __make_tracker();

        // 状态跟踪表 存储 2 个正常和 1 个异常保活信号
        {
            let mut stt = trckr.state_tracking_table.lock().await;
            for i in 1..=2 {
                let cq = __make_state_circular_queue(unix_timestamp());
                stt.insert(i.to_string(), cq);
            }
            let cq = __make_state_circular_queue(unix_timestamp() - 3600);
            stt.insert(3.to_string(), cq);
        }

        // 向本地 redis 写入 3 号调度器的 10 个工作流
        for i in 1u8..=10u8 {
            __wirte_workflow_to_redis(
                3.to_string().as_str(),
                i.to_string().as_str(),
                vec!["redis://127.0.0.1:7000/".to_owned()],
                vec![i],
            )
            .unwrap();
        }

        // 向调度器注册表内写入，目前存在于调度器状态跟踪表内的所有调度器信息
        {
            let sch_state_trck_table = trckr.state_tracking_table.lock().await;
            let keys = sch_state_trck_table
                .keys()
                .cloned()
                .collect::<Vec<String>>();
            drop(sch_state_trck_table);
            __sync_tracking_state_to_register_table(&trckr, &keys).await;
        }

        // 检测调度器生死状态
        // 3 号调度器设定为死亡状态
        // 3号调度器应该已经从注册表中删除
        trckr.scheduler_idcheck().await; // 内部会对redis取出的工作流执行snappy压缩
        assert!(!trckr
            .scheduler_register_table
            .read()
            .await
            .contains_key("3"));
        // 检测到调度器死亡之后，应该可以从 rx 端读到全部归属 3 号调度器的 10 个工作流
        let mut all_wf_ids: HashSet<u8> = HashSet::new();
        for i in 1u8..=10u8 {
            all_wf_ids.insert(i);
        }
        for _i in 1u8..=10u8 {
            let wf: Vec<u8> = rx.recv_async().await.unwrap();
            all_wf_ids.remove(&wf[18]); // 取出的工作流为 snappy 压缩后的，第 18 位为真实数据
        }
        assert!(all_wf_ids.is_empty());
    }
}
