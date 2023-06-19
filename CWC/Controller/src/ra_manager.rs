extern crate nanoid;
use nanoid::nanoid;
use prometheus::{IntCounter, IntGauge, Opts, Registry};
use std::sync::Arc;
use std::{collections::HashMap, time::Duration};
use tokio::{sync::RwLock, time::sleep};

use crate::{
    config::Configure,
    grpc::resource_allocator::{
        resource_request_service_client::ResourceRequestServiceClient, CreateSchedulerPodRequest,
        GetSchedulerNumRequest, UpdateSchedulerAliveStatusRequest,
    },
    keep_alive::unix_timestamp,
    metric_endpoint::PrometheusRegistry,
};

#[derive(Debug)]
pub struct SchedulerWaitTimer {
    counter: u32,
}

#[derive(Debug)]
struct RARegisterTableItem {
    /// 集群id
    cluster_id: String,

    /// ipv4地址
    ipv4: String,

    scheduler_count: u32,

    waiting_startup_timeout_queue: Vec<SchedulerWaitTimer>,

    /// 调度器调用失败计数
    error_count: u32,
}

type ClusterID = String;
type RARegisterTable = HashMap<ClusterID, RARegisterTableItem>;
pub struct StateChanged(pub String, pub bool); //sch_id, state
pub type StateChangedMap = HashMap<ClusterID, Vec<StateChanged>>;

#[derive(Debug)]
pub struct RAManager {
    /// Prometheus 指标：启动调度器计数
    start_scheduler_counter: IntCounter,

    /// 资源分配器注册表
    ra_register_table: RwLock<RARegisterTable>,

    /// 全局配置
    global_conf: Arc<Configure>,

    /// 集群数量
    cluster_count: IntGauge,

    /// Prometheus 指标: pressure_evaluator 工作次数计数
    ra_manager_work_count: IntCounter,

    /// 启动id，每次启动 ra_manager 的时候重新生成一个新的启动序列号
    boot_id: String,
}

impl RAManager {
    pub fn new(global_conf: Arc<Configure>) -> RAManager {
        RAManager {
            start_scheduler_counter: IntCounter::with_opts(Opts::new(
                "start_scheduler_counter",
                "start scheduler counter by pressure-evaluator",
            ))
            .unwrap(),

            ra_register_table: RwLock::new(RARegisterTable::new()),
            global_conf,
            cluster_count: IntGauge::with_opts(Opts::new("cluster_count", "已注册集群数量"))
                .unwrap(),
            ra_manager_work_count: IntCounter::with_opts(Opts::new(
                "ra_manager_work_count",
                "ra manager work count",
            ))
            .unwrap(),
            boot_id: nanoid!(),
        }
    }

    pub async fn register_resource_allocator(&self, cluster_id: &str, ipv4: &str) -> String {
        let mut ra_reg_table = self.ra_register_table.write().await;
        if !ra_reg_table.contains_key(cluster_id) {
            ra_reg_table.insert(
                cluster_id.to_owned(),
                RARegisterTableItem {
                    cluster_id: cluster_id.to_owned(),
                    ipv4: ipv4.to_owned(),
                    scheduler_count: 0,
                    waiting_startup_timeout_queue: Vec::new(),
                    error_count: 0,
                },
            );
            self.cluster_count.inc();
        }

        debug!(
            "[资源分配器管理器] 注册资源分配器: {}-{}，返回启动序列号：{}",
            cluster_id, ipv4, self.boot_id
        );
        self.boot_id.clone()
    }

    pub async fn enlarge_scheduler_set(&self, enlarge_num: u32) {
        // 使用最简单的负载均衡算法
        // 平均分到各资源分配器
        let mut ra_register_table = self.ra_register_table.write().await;
        let ra_count = ra_register_table.len();
        if ra_count == 0 {
            info!("[资源分配器管理器] 没有注册的资源分配器，跳过本次扩展调度器数量操作");
            return;
        }
        let sch_num_per_ra = (enlarge_num as f64 / ra_count as f64).ceil() as u32;
        debug!(
            "[资源分配器管理器] 共 {} 个集群 / 每个集群需要启动 {} 个调度器",
            ra_count, sch_num_per_ra
        );
        for (cluster_id, ra) in ra_register_table.iter_mut() {
            let ra_address = format!("http://{}", ra.ipv4);
            if let Some(allow_scheduler_start_num) = self
                .get_allow_scheduler_start_number(cluster_id, &ra_address)
                .await
            {
                let need_start_num = sch_num_per_ra.min(allow_scheduler_start_num);
                if need_start_num > 0 {
                    let successed_count = self
                        .start_scheduler(
                            cluster_id,
                            &ra_address,
                            need_start_num,
                            &mut ra.waiting_startup_timeout_queue,
                        )
                        .await;
                    ra.error_count += need_start_num - successed_count;
                }
            } else {
                warn!(
                    "[资源分配器管理器] 向集群 {} 的资源分配器 {} 查询可启动调度器数量失败，本轮跳过在此资源分配器启动调度器",
                    cluster_id, ra_address
                );
                ra.error_count += 1;
            }
        }
    }

    /// 返回值为成功启动的调度器个数
    async fn start_scheduler(
        &self,
        cluster_id: &str,
        ra_address: &str,
        num: u32,
        waiting_startup_timeout_queue: &mut Vec<SchedulerWaitTimer>,
    ) -> u32 {
        let mut successed_count = 0u32;
        debug!(
            "准备向集群 {} 的资源分配器({})发送启动 {} 个调度器的请求",
            cluster_id, ra_address, num
        );
        if let Ok(mut client) = ResourceRequestServiceClient::connect(ra_address.to_owned()).await {
            for idx in 0..num {
                let request = tonic::Request::new(CreateSchedulerPodRequest {
                    image: self.global_conf.scheduler_image.clone(),
                    cpu: (self.global_conf.scheduler_cpu_core_count * 1000) as i64,
                    mem: self.global_conf.scheduler_mem_size as i64,
                    env: Vec::new(),
                });
                match client.create_scheduler_pod(request).await {
                    Ok(response) => {
                        if response.get_ref().result >= 1 {
                            debug!(
                                    "[资源分配器管理器] 成功向资源分配器请求创建调度器 1 次/已建立 {} 个/本轮共需 {} 次",
                                    idx + 1,
                                    num
                                );
                            self.start_scheduler_counter.inc();

                            // 向资源调度器请求启动调度器成功后，设置启动超时计时器
                            // 启动超时计时器内的调度器在超时前，控制器认为调度器已经启动，不再请求资源分配器启动调度器
                            waiting_startup_timeout_queue.push(SchedulerWaitTimer {
                                counter: self.global_conf.scheduler_start_timeout_cycle,
                            });
                            waiting_startup_timeout_queue.sort_unstable_by_key(|val| val.counter);
                            debug!(
                                "[资源分配器管理器] 启动的调度器放入超时列表中, 将在 {} 周期后超时。集群 {} 超时列表中有 {} 个调度器",
                                self.global_conf.scheduler_start_timeout_cycle, cluster_id, waiting_startup_timeout_queue.len()
                            );
                            successed_count += 1;
                        } else {
                            error!(
                                "[资源分配器管理器] 创建调度器失败，资源分配器返回错误码：{}",
                                response.get_ref().err_no
                            );
                        }
                    }
                    Err(err) => {
                        error!(
                            "[资源分配器管理器] 与资源分配器 {} gRPC 通信错误，创建调度器失败 {:?}",
                            ra_address, err
                        );
                        // 与资源分配器通信的client貌似不行了，继续循环无意义，跳出循环
                        break;
                    }
                }
            }
        } else {
            error!(
                "[资源分配器管理器] 准备启动调度器时，无法连接到集群{}的资源分配器({})",
                cluster_id, ra_address
            );
        }
        successed_count
    }

    async fn get_allow_scheduler_start_number(
        &self,
        cluster_id: &str,
        ra_address: &str,
    ) -> Option<u32> {
        if let Ok(mut client) = ResourceRequestServiceClient::connect(ra_address.to_owned()).await {
            match client
                .get_allocatable_scheduler_num(GetSchedulerNumRequest {
                    cpu: (self.global_conf.scheduler_cpu_core_count * 1000) as i64,
                    mem: self.global_conf.scheduler_mem_size as i64,
                })
                .await
            {
                Ok(response) => {
                    let scheduler_pod_num = response.get_ref().scheduler_pod_num as u32;
                    info!(
                        "[资源分配器管理器] 经查询，集群 {} 的资源分配器 {} 允许启动 {} 个调度器",
                        cluster_id, ra_address, scheduler_pod_num
                    );
                    Some(scheduler_pod_num)
                }
                Err(err) => {
                    error!(
                            "[资源分配器管理器] 与资源分配器 {} gRPC 通信错误，获取最大调度器启动数量失败 {:?}",
                            ra_address, err
                        );
                    None
                }
            }
        } else {
            error!(
                "[资源分配器管理器] 准备获取最大调度器启动数量时，无法连接到资源分配器({})",
                ra_address
            );
            None
        }
    }

    pub async fn get_waiting_startup_sch_number(&self) -> usize {
        let ra_register_table = self.ra_register_table.read().await;
        let mut count = 0usize;
        for (_, ra) in ra_register_table.iter() {
            count += ra.waiting_startup_timeout_queue.len();
        }
        count
    }

    pub async fn notify_scheduler_started(&self, cluster_id: &str, sch_id: &str) {
        let mut ra_register_table = self.ra_register_table.write().await;
        // 减少一个（最小的那个）启动超时计时器
        // 计时器队列保证由小到大排序(在 start_scheduler 函数里)
        if let Some(ra_item) = ra_register_table.get_mut(cluster_id) {
            if ra_item.waiting_startup_timeout_queue.len() > 0 {
                ra_item.waiting_startup_timeout_queue.remove(0);
            }
            ra_item.scheduler_count += 1;
        } else {
            error!(
                "[资源分配器管理器] 告知({})调度器完成启动，但此调度器所在集群id({})不在资源分配器注册表里！",
                sch_id, cluster_id
            );
        }
    }

    pub async fn notify_scheuler_deaded(&self, cluster_id: &str, sch_id: &str) {
        let mut ra_register_table = self.ra_register_table.write().await;
        if let Some(ra_item) = ra_register_table.get_mut(cluster_id) {
            ra_item.scheduler_count -= 1;
        } else {
            error!(
                "[资源分配器管理器] 告知({})调度器死亡，但此调度器所在集群id({})不在资源分配器注册表里！",
                sch_id, cluster_id
            );
        }
    }

    /// 根据“资源分配器模块”的需求，当检测到调度器死亡后，
    /// 通知“资源分配器模块”，调度器状态改变
    pub async fn send_change_event_to_ra(&self, s_changed: StateChangedMap) {
        if s_changed.len() == 0 {
            return;
        }
        for (cluster_id, state) in s_changed.iter() {
            let ra_register_table = self.ra_register_table.read().await;
            if let Some(ra) = ra_register_table.get(cluster_id) {
                let ra_address = format!("http://{}", ra.ipv4);
                drop(ra_register_table);
                if let Ok(mut client) =
                    ResourceRequestServiceClient::connect(ra_address.clone()).await
                {
                    for sch_stat in state.iter() {
                        let request = tonic::Request::new(UpdateSchedulerAliveStatusRequest {
                            scheduler_id: sch_stat.0.clone(),
                            alive_status: sch_stat.1,
                        });
                        match client.update_scheduler_alive_status(request).await {
                            Ok(response) => {
                                if response.get_ref().result < 1 {
                                    error!(
                                            "[资源分配器管理器] 向资源分配器发送调度器状态失败，资源分配器返回错误码：{}",
                                            response.get_ref().err_no
                                        );
                                }
                            }
                            Err(err) => {
                                error!(
                                        "[资源分配器管理器] 与资源分配器 {} gRPC 通信错误，向资源分配器发送调度器状态失败 {:?}",
                                        ra_address, err
                                    );
                                // 与资源分配器通信的client貌似不行了，继续循环无意义，跳出循环
                                break;
                            }
                        }
                    }
                } else {
                    error!(
                        "[资源分配器管理器] 向资源分配器({})发送调度器变更信息，但无法连接到资源分配器",
                        ra_address
                    );
                }
            } else {
                warn!("[资源分配器管理器] 向资源分配器发送变更信息时，无法从资源分配器注册表中读取cluster id为{}的资源分配器！",cluster_id);
            }
        }
    }

    async fn check_startup_timeout(&self) {
        let mut ra_register_table = self.ra_register_table.write().await;
        for (cluster_id, v) in ra_register_table.iter_mut() {
            for timer in v.waiting_startup_timeout_queue.iter_mut() {
                timer.counter -= 1;
            }
            v.waiting_startup_timeout_queue
                .retain(|val| val.counter > 0);
            debug!(
                "[资源分配器管理器] 集群 {} 内有 {} 个等待启动的调度器",
                cluster_id,
                v.waiting_startup_timeout_queue.len()
            );
        }
    }

    async fn check_ra_status(&self) {
        let mut ra_register_table = self.ra_register_table.write().await;
        ra_register_table.retain(|k, v| {
            if v.error_count >= self.global_conf.allowed_ra_communication_num {
                warn!(
                    "[资源分配器管理器] 与资源分配器(cluster id:{})通信失败次数({})超过阈值({})，将从资源分配器注册表中剔除！",
                    k, v.error_count, self.global_conf.allowed_ra_communication_num
                );
                self.cluster_count.dec();
                false
            } else {
                true
            }
        });
    }

    pub async fn run(&self) {
        loop {
            self.ra_manager_work_count.inc();
            self.check_startup_timeout().await;
            self.check_ra_status().await;

            let tp = unix_timestamp(); //前一次时间
            sleep(Duration::new(2, 0)).await;
            let tc = unix_timestamp(); //当前时间
            if tc - tp > 4 {
                warn!("[资源分配器管理器] 等待时延超过正常情况2倍");
            }
        }
    }
}

impl PrometheusRegistry for RAManager {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.start_scheduler_counter.clone()))
            .unwrap();
        r.register(Box::new(self.ra_manager_work_count.clone()))
            .unwrap();
        r.register(Box::new(self.cluster_count.clone())).unwrap();
    }
}

#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use crate::config::Configure;

    use super::{RAManager, SchedulerWaitTimer};

    fn __make_default_instance() -> RAManager {
        RAManager::new(Arc::new(Configure::default()))
    }

    #[tokio::test]
    async fn test_ra_register() {
        let ra_mngr = __make_default_instance();

        // 向资源分配器管理器注入 5000 个资源分配器
        for i in 0..5000u32 {
            ra_mngr
                .register_resource_allocator(&i.to_string(), "192.168.1.1:8080")
                .await;
        }

        let ra_table = ra_mngr.ra_register_table.read().await;

        // 验证数量
        assert_eq!(ra_table.len(), 5000);

        // 验证内容
        for i in 0..5000u32 {
            let r = ra_table.get(&i.to_string());
            assert!(r.is_some());
            assert_eq!(r.unwrap().ipv4, "192.168.1.1:8080");
        }
    }

    #[tokio::test]
    async fn test_check_startup_timeout() {
        let ra_mngr = __make_default_instance();

        ra_mngr
            .register_resource_allocator("1", "192.168.1.1:8080")
            .await;

        let mut ra_register_table = ra_mngr.ra_register_table.write().await;
        let ra = ra_register_table.get_mut("1").unwrap();
        ra.waiting_startup_timeout_queue
            .push(SchedulerWaitTimer { counter: 6 });
        ra.waiting_startup_timeout_queue
            .push(SchedulerWaitTimer { counter: 5 });
        ra.waiting_startup_timeout_queue
            .sort_unstable_by_key(|val| val.counter);
        drop(ra_register_table);

        ra_mngr.check_startup_timeout().await;

        let mut ra_register_table = ra_mngr.ra_register_table.write().await;
        let ra = ra_register_table.get_mut("1").unwrap();

        assert_eq!(ra.waiting_startup_timeout_queue[0].counter, 4);
        assert_eq!(ra.waiting_startup_timeout_queue[1].counter, 5);
    }
}
