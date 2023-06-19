use super::config::Configure;
use super::data_structs::circular_queue::CircularQueue;
use super::grpc::scheduler_controller::KeepAliveRequest;
use super::metric_endpoint::PrometheusRegistry;
use super::tracker::SchedulerRegisterTable;
use prometheus::{Counter, Opts, Registry};
use std::collections::HashMap;
use std::collections::HashSet;
use std::sync::Arc;
use std::time::{SystemTime, UNIX_EPOCH};
use tokio::sync::{Mutex, RwLock};

const DEFAULT_STATE_QUEUE_LENGTH: usize = 20;
pub type SchedulerStateTrackingTable = HashMap<String, CircularQueue<SchedulerStateTrackingItem>>;

#[derive(Debug)]
pub struct SchedulerStateTrackingItem {
    // 调度器自测压力值（0~100，整数）
    pub pressure: u32,
    // 调度器承载力（还可以承载/接收多少工作流，单位：个，整数）
    pub capacity: u32,
    // 保活序号（单调增，整数，从1开始）
    pub serial_number: u64,
    // ipv4地址
    pub ipv4: std::string::String,
    // 时间戳
    pub timestamp: u64,
    // 集群id
    pub cluster_id: String,
}

#[derive(Debug)]
pub struct KeepAliveServer {
    /// 调度器黑名单的一个ARC引用（来自 main 模块）
    scheduler_blacklist: Arc<RwLock<HashSet<String>>>,

    /// 调度器注册表的一个ARC引用（来自 tracker 模块）
    scheduler_register_table: Option<Arc<RwLock<SchedulerRegisterTable>>>,

    /// 调度器状态跟踪表
    state_tracking_table: Arc<Mutex<SchedulerStateTrackingTable>>,

    /// Prometheus 指标：收到的保活信号计数
    recv_keep_alive_signal_counter: Counter,

    /// 全局配置
    global_conf: Arc<Configure>,
}

impl KeepAliveServer {
    pub fn new(
        scheduler_blacklist: Arc<RwLock<HashSet<String>>>,
        conf: Arc<Configure>,
    ) -> KeepAliveServer {
        KeepAliveServer {
            scheduler_register_table: None,
            scheduler_blacklist,
            state_tracking_table: Arc::new(Mutex::new(HashMap::new())),
            recv_keep_alive_signal_counter: Counter::with_opts(Opts::new(
                "recved_keepalive_signal",
                "received keep alive signal counter",
            ))
            .unwrap(),

            global_conf: conf,
        }
    }

    pub fn set_scheduler_register_table(&mut self, srt: Arc<RwLock<SchedulerRegisterTable>>) {
        self.scheduler_register_table = Some(srt);
    }

    pub async fn process_keep_alive_signal(&self, request: KeepAliveRequest) -> i32 {
        self.recv_keep_alive_signal_counter.inc();

        // 收到保活信号，保存到跟踪表里
        // TODO: 需要检查序列号回退的现象
        debug!(
            "[保活信号接收器] 收到调度器 [{}] 发送的序号为 {} 的保活信号",
            request.sid.as_str(),
            request.serial_number
        );

        if self.scheduler_blacklist.read().await.contains(&request.sid) {
            // 如果调度器已经在黑名单中，不接受调度器的保活信号（随后调度器跟踪器会认为此调度器已经死亡）
            // 返回负值告知调度器自己关闭
            debug!(
                "[保活信号接收器] 调度器 [{}] 在黑名单中，不接受调度器序号为 {} 的保活信号",
                request.sid.as_str(),
                request.serial_number
            );
            -1
        } else {
            let mut stt = self.state_tracking_table.lock().await;
            debug!(
                "[保活信号接收器] 收到调度器 [{}] 的保活信号[pressure:{}, capacity:{}]，已放入状态跟踪表内",
                request.sid.as_str(), request.pressure, request.capacity
            );

            let mut keep_alive_delay = 1i32;
            if let Some(srt) = &self.scheduler_register_table {
                let scheduler_register_table = srt.read().await;
                keep_alive_delay = match scheduler_register_table.get(request.sid.as_str()) {
                    Some(sch) => sch.keep_alive_delay as i32,
                    None => 1i32,
                };
            }
            stt.entry(request.sid)
                .or_insert_with(|| CircularQueue::with_capacity(DEFAULT_STATE_QUEUE_LENGTH))
                .push(SchedulerStateTrackingItem {
                    pressure: request.pressure,
                    capacity: request.capacity,
                    serial_number: request.serial_number,
                    ipv4: request.ipv4,
                    timestamp: unix_timestamp(),
                    cluster_id: request.cluster_id,
                });
            keep_alive_delay
        }
    }

    pub fn get_state_tracking_table(&self) -> Arc<Mutex<SchedulerStateTrackingTable>> {
        self.state_tracking_table.clone()
    }
}

impl PrometheusRegistry for KeepAliveServer {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.recv_keep_alive_signal_counter.clone()))
            .unwrap();
    }
}

pub fn unix_timestamp() -> u64 {
    let start = SystemTime::now();
    let since_the_epoch = start.duration_since(UNIX_EPOCH).expect("异常：时间倒流！");
    since_the_epoch.as_secs()
}

#[cfg(test)]
mod tests {
    use super::*;

    fn __make_keep_alive_server() -> KeepAliveServer {
        let config = Arc::new(Configure::default());
        let sch_blk_list = Arc::new(RwLock::new(HashSet::new()));
        KeepAliveServer::new(sch_blk_list, config)
    }

    #[tokio::test]
    async fn test_process_keep_alive_signal() {
        let request = KeepAliveRequest {
            sid: String::from("123"),
            pressure: 20,
            capacity: 100,
            serial_number: 2,
            ipv4: String::from("192.168.1.101"),
            cluster_id: String::from("123"),
        };

        let kas = __make_keep_alive_server();

        kas.process_keep_alive_signal(request).await;

        let stt_c = kas.get_state_tracking_table();
        let stt = stt_c.lock().await;
        let v = stt.get("123").unwrap().last().unwrap();
        assert_eq!(v.pressure, 20);
        assert_eq!(v.ipv4, String::from("192.168.1.101"));
    }

    #[tokio::test]
    async fn test_read_tracking_vec() {
        let mut cq = CircularQueue::with_capacity(DEFAULT_STATE_QUEUE_LENGTH);
        cq.push(SchedulerStateTrackingItem {
            pressure: 20,
            capacity: 100,
            serial_number: 2,
            ipv4: String::from("192.168.1.101"),
            timestamp: unix_timestamp(),
            cluster_id: String::from("123"),
        });

        let kas = __make_keep_alive_server();

        {
            let stt_c = kas.get_state_tracking_table();
            let mut stt = stt_c.lock().await;
            stt.insert(String::from("123"), cq);
        }

        let stt_c = kas.get_state_tracking_table();
        let stt = stt_c.lock().await;
        let v = stt.get("123").unwrap().last().unwrap();
        assert_eq!(v.pressure, 20);
        assert_eq!(v.ipv4, String::from("192.168.1.101"));
    }
}
