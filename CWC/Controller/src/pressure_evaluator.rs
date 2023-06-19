use crate::ra_manager::RAManager;

use super::config::Configure;
use super::keep_alive::unix_timestamp;
use super::metric_endpoint::PrometheusRegistry;
use super::pre_selector::SchedulerPreSelectedTable;
use super::tracker::SchedulerRegisterTable;
use flume::Receiver;
use prometheus::{Gauge, GaugeVec, IntCounter, IntGauge, Opts, Registry};
use std::collections::HashSet;
use std::sync::Arc;
use tokio::sync::RwLock;
use tokio::time;

// 系统采样周期
const SAMPLE_INTERVAL: u64 = 2;

pub struct PressureEvaluator {
    // 调度器注册表的一个ARC引用（来自 Tracker 模块）
    scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,

    /// 调度器预选表的一个ARC引用（来自 pre_selector）
    pre_selected_table: Arc<RwLock<SchedulerPreSelectedTable>>,

    /// 调度器黑名单的一个ARC引用（来自 main 模块）
    scheduler_blacklist: Arc<RwLock<HashSet<String>>>,

    /// 工作流队列内多少工作流待发送（缓存工作流数量），来自 scheduler_distributor 模块，本模块内：只读
    wf_count_in_queue: IntGauge,

    // Prometheus 指标：当前系统内可用负载容量
    valid_capacity: Gauge,

    /// Prometheus 指标：1分钟，5分钟，15分钟负载
    pressure_gauge: Gauge,

    /// Prometheus 指标：PID 调整调度器数量的增量
    pe_pid_delta: Gauge,

    pe_pid_inc: GaugeVec,

    /// Prometheus 指标: pressure_evaluator 工作次数计数
    pressure_evaluator_work_count: IntCounter,

    /// 上次增量
    inc_1: f64,

    /// 上上次增量
    inc_2: f64,

    /// 平均每个调度器可承载的工作流数量，来自 tracker 模块，本模块内：只读
    wf_count_per_scheduler: IntGauge,

    /// 全局配置
    global_conf: Arc<Configure>,

    /// 需要更新的镜像名称队列
    image_queue_receiver: Receiver<String>,

    /// 资源分配器管理器（来自 ra_manager 模块）
    ra_manager: Arc<RAManager>,
}

impl PressureEvaluator {
    pub fn new(
        scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,
        pre_selected_table: Arc<RwLock<SchedulerPreSelectedTable>>,
        scheduler_blacklist: Arc<RwLock<HashSet<String>>>,
        wf_count_in_queue: IntGauge,
        wf_count_per_scheduler: IntGauge,
        global_conf: Arc<Configure>,
        image_queue_receiver: Receiver<String>,
        ra_manager: Arc<RAManager>,
    ) -> PressureEvaluator {
        PressureEvaluator {
            scheduler_register_table,
            pre_selected_table,
            scheduler_blacklist,
            wf_count_in_queue,
            wf_count_per_scheduler,

            valid_capacity: Gauge::with_opts(Opts::new("valid_capacity", "valid capacity"))
                .unwrap(),

            pressure_gauge: Gauge::with_opts(Opts::new("pressure_gauge", "pressure gauge"))
                .unwrap(),

            pe_pid_delta: Gauge::with_opts(Opts::new(
                "pe_pid_delta",
                "Presssure Evaluator PID delta",
            ))
            .unwrap(),

            pe_pid_inc: GaugeVec::new(
                Opts::new("pe_pid_inc", "Presssure Evaluator PID Error"),
                &["k"],
            )
            .unwrap(),

            pressure_evaluator_work_count: IntCounter::with_opts(Opts::new(
                "pressure_evaluator_work_count",
                "pressure evaluator work count",
            ))
            .unwrap(),

            inc_1: 0.0,
            inc_2: 0.0,
            global_conf,
            image_queue_receiver,
            ra_manager,
        }
    }

    async fn ensure_scheduler_number_threshold(&self, delta_count: u32) -> u32 {
        // 如果限制调度器最大数量(max_scheduler > 0)，则遵照限制
        debug!(
            "[压力评价器] 调度器数量限制为 {} 个",
            self.global_conf.max_scheduler
        );
        if self.global_conf.max_scheduler > 0 {
            let cur_scheduler_num = self.scheduler_num().await;
            let max_scheduler_num = self.global_conf.max_scheduler as usize;
            if cur_scheduler_num < max_scheduler_num {
                let r = (max_scheduler_num - cur_scheduler_num).min(delta_count as usize) as u32;
                info!(
                    "[压力评价器] 调度器启动数量被限制不超过 {} 个，本次启动 {} 个",
                    self.global_conf.max_scheduler, r
                );
                r
            } else {
                info!("[压力评价器] 调度器启动数量超过限制，不再启动新调度器");
                0
            }
        } else {
            delta_count
        }
    }

    /// 完成增删指定数量的调度器
    async fn rescale_scheduler_set(&mut self, delta_count: i32) {
        info!("[压力评价器] 需要启停 {} 个调度器", delta_count);

        if delta_count > 0 {
            let delta_num = self
                .ensure_scheduler_number_threshold(delta_count as u32)
                .await;
            if delta_num > 0 {
                self.ra_manager.enlarge_scheduler_set(delta_num).await;
            }
        } else if delta_count < 0 && self.global_conf.scheduler_balance_algorithm != "INCREASE" {
            // 需要关闭一些调度的情况：从调度器预选表中选择一些压力比较小的调度器，关闭它们
            // 通过把调度器加入·调度器黑名单·的办法关闭调度器
            // 调度器黑名单在 keep_alive 模块内处理
            let prsltbl = self.pre_selected_table.read().await;
            for low_press_sch in prsltbl.iter().rev().take(delta_count.abs() as usize) {
                let mut sch_blklst = self.scheduler_blacklist.write().await;
                sch_blklst.insert(low_press_sch.sid.clone());
            }
        }
    }

    /// 当前系统内调度器总数
    async fn scheduler_num(&self) -> usize {
        let srt = self.scheduler_register_table.read().await;
        srt.len() + self.ra_manager.get_waiting_startup_sch_number().await
    }

    // 当前系统可用容量
    async fn valid_capacity(&self) -> u32 {
        let srt = self.scheduler_register_table.read().await;
        let capacity: Vec<u32> = srt
            .iter()
            .map(|(_, tt)| ((100 - tt.pressure) as f64 / 100.0 * tt.capacity as f64).round() as u32)
            .collect();
        let mut capacity_count = capacity.iter().sum::<u32>();

        // 可用容量算上正在启动但还没有完全启动（没收到保活信号）的调度器容量
        let waiting_startup_sch_number =
            self.ra_manager.get_waiting_startup_sch_number().await as u32;
        capacity_count += waiting_startup_sch_number * self.wf_count_per_scheduler.get() as u32;
        debug!(
            "[压力评价器] 当前系统内等待启动调度器 {} 个，调度器可用承载 {} 个工作流",
            waiting_startup_sch_number, capacity_count
        );
        self.valid_capacity.set(capacity_count as f64);
        capacity_count
    }

    // 当前系统内有多少工作流超过了当前系统最大负载
    // 如果结果大于0，需要新增调度器
    async fn capacity_overflow_count(&self) -> i32 {
        self.wf_count_in_queue.get() as i32 - self.valid_capacity().await as i32
    }

    async fn pid(&mut self, kp: f64, ti: f64, td: f64, t: f64) -> i32 {
        // 如果当前系统可以承载，不需要进行pid调控
        let co_count = self.capacity_overflow_count().await;
        if co_count <= 0 {
            return 0;
        }

        let expect_scheduler_num = co_count as f64 / self.wf_count_per_scheduler.get() as f64;
        let inc_0: f64 = expect_scheduler_num - self.scheduler_num().await as f64;

        // 记录真实计算的 inc 值
        self.pe_pid_inc
            .get_metric_with_label_values(&["0"])
            .unwrap()
            .set(inc_0);
        self.pe_pid_inc
            .get_metric_with_label_values(&["1"])
            .unwrap()
            .set(self.inc_1);
        self.pe_pid_inc
            .get_metric_with_label_values(&["2"])
            .unwrap()
            .set(self.inc_2);

        // TODO 暂时不考虑调度器缩容问题，增量不能小于0
        let inc_0 = if inc_0 < 0.0 { 0.0 } else { inc_0 };

        let delta_u: f64 = kp * (1.0 + t / ti + td / t) * inc_0
            - kp * (1.0 + 2.0 * td / t) * self.inc_1
            + kp * td / t * self.inc_2;
        self.pe_pid_delta.set(delta_u);

        self.inc_2 = self.inc_1;
        self.inc_1 = inc_0;

        delta_u.round() as i32
    }

    async fn naive(&self) -> i32 {
        // 如果当前系统可以承载，不需要进行pid调控
        let co_count = self.capacity_overflow_count().await;
        debug!("[压力评价器] naive 算法中，容量溢出值为 {}", co_count);
        if co_count <= 0 {
            return 0;
        }
        let expect_scheduler_num = co_count as f64 / self.wf_count_per_scheduler.get() as f64;

        if self.global_conf.scheduler_balance_strategy == "RADICAL" {
            // 激进策略下，如果当前系统中没有调度器，至少启动1个
            expect_scheduler_num.ceil() as i32
        } else {
            expect_scheduler_num.floor() as i32
        }
    }

    async fn pressure_checking(&self) -> f64 {
        // 读取调度器注册表
        // 统计所有调度器的压力
        // 调度器总压力超过70%，新增调度器
        let srt = self.scheduler_register_table.read().await;

        let pressures: Vec<u32> = srt.iter().map(|(_, tt)| tt.pressure).collect();

        // 没有调度器的时候，压力值为100
        let pressures =
            100.0_f64.min(pressures.iter().sum::<u32>() as f64 / pressures.len() as f64);

        self.pressure_gauge.set(pressures);
        pressures
    }

    async fn check_scheduler_upgrade(&mut self) {
        if let Ok(image_name) = self.image_queue_receiver.try_recv() {
            if image_name == self.global_conf.scheduler_image {
                info!("调度器镜像需要更新,重启所有`已经启动`的调度器");
                let mut srt = self.scheduler_register_table.write().await;
                for (sch_id, _) in srt.iter() {
                    let mut sch_blklst = self.scheduler_blacklist.write().await;
                    sch_blklst.insert(sch_id.clone());
                }
                // 关闭调度器后需要清空调度器注册表
                srt.clear();
            }
        }
    }

    pub async fn run(&mut self) {
        loop {
            self.pressure_evaluator_work_count.inc();
            self.pressure_checking().await;
            let delta = match &self.global_conf.scheduler_balance_algorithm[..] {
                "PID" => {
                    self.pid(
                        self.global_conf.pid_kp,
                        self.global_conf.pid_td,
                        self.global_conf.pid_ti,
                        SAMPLE_INTERVAL as f64,
                    )
                    .await
                }
                _ => self.naive().await, // NONE，和其他未知算法全认为是navie算法
            };
            self.rescale_scheduler_set(delta).await;
            self.check_scheduler_upgrade().await;

            let tp = unix_timestamp(); //前一次时间
            time::sleep(time::Duration::new(SAMPLE_INTERVAL, 0)).await;
            let tc = unix_timestamp(); //当前时间
            if tc - tp > 4 {
                warn!("[压力评价器] 等待时延超过正常情况2倍");
            }
        }
    }
}

impl PrometheusRegistry for PressureEvaluator {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.valid_capacity.clone())).unwrap();
        r.register(Box::new(self.pressure_gauge.clone())).unwrap();
        r.register(Box::new(self.pe_pid_delta.clone())).unwrap();
        r.register(Box::new(self.pe_pid_inc.clone())).unwrap();
        r.register(Box::new(self.pressure_evaluator_work_count.clone()))
            .unwrap();
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    use super::super::pre_selector::SchedulerPreSelectedItem;
    use super::super::tracker::SchedulerRegisterItem;
    use flume::{unbounded, Sender};

    fn __make_default_config() -> Arc<Configure> {
        Arc::new(Configure::default())
    }

    async fn __mk_pressure_evaluator_instance(
        default_config: Arc<Configure>,
    ) -> (PressureEvaluator, Sender<String>) {
        let srt: Arc<RwLock<SchedulerRegisterTable>> =
            Arc::new(RwLock::new(SchedulerRegisterTable::new()));
        let psltbl: Arc<RwLock<SchedulerPreSelectedTable>> =
            Arc::new(RwLock::new(SchedulerPreSelectedTable::new()));
        let sch_blk_list = Arc::new(RwLock::new(HashSet::new()));

        // 填充调度器注册表(1w)
        {
            let mut srt_w = srt.write().await;
            for i in 1..=10000 {
                srt_w.insert(
                    i.to_string(),
                    SchedulerRegisterItem {
                        pressure: i,
                        capacity: i,
                        ipv4: String::from("192.168.1.101"),
                        state: crate::tracker::SchedulerState::Running,
                        cluster_id: String::from("123"),
                        keep_alive_delay: 1,
                    },
                );
            }
        }

        // 填充调度器预选表(100)
        {
            let mut psltbl_w = psltbl.write().await;
            for i in (1..=100).rev() {
                psltbl_w.push(SchedulerPreSelectedItem {
                    sid: i.to_string(),
                    ipv4: String::from("192.168.1.101"),
                    valid_cap: i,
                    cluster_id: String::from("123"),
                });
            }
        }

        let (tx, rx) = unbounded::<String>();

        (
            PressureEvaluator::new(
                srt,
                psltbl,
                sch_blk_list,
                IntGauge::with_opts(Opts::new(
                    "wf_count_in_queue",
                    "wf_count_in_queue workflow queue length",
                ))
                .unwrap(),
                IntGauge::with_opts(Opts::new(
                    "wf_count_per_scheduler",
                    "allowed workflows per scheduler",
                ))
                .unwrap(),
                default_config.clone(),
                rx,
                Arc::new(RAManager::new(default_config)),
            ),
            tx,
        )
    }

    #[tokio::test]
    async fn test_pressure_checking() {
        let (pe, _) = __mk_pressure_evaluator_instance(__make_default_config()).await;
        assert_eq!(100f64, pe.pressure_checking().await);
    }

    #[tokio::test]
    // 测试在没有调度器的情况下，压力值应该输出 100.0
    async fn test_pressure_checking_empty_scheduler() {
        let sstt: Arc<RwLock<SchedulerRegisterTable>> =
            Arc::new(RwLock::new(SchedulerRegisterTable::new()));
        let psltbl: Arc<RwLock<SchedulerPreSelectedTable>> =
            Arc::new(RwLock::new(SchedulerPreSelectedTable::new()));
        let sch_blk_list = Arc::new(RwLock::new(HashSet::new()));
        let (_, rx) = unbounded::<String>();

        let default_config = __make_default_config();
        let pe = PressureEvaluator::new(
            sstt,
            psltbl,
            sch_blk_list,
            IntGauge::with_opts(Opts::new(
                "wf_count_in_queue",
                "wf_count_in_queue workflow queue length",
            ))
            .unwrap(),
            IntGauge::with_opts(Opts::new(
                "wf_count_per_scheduler",
                "allowed workflows per scheduler",
            ))
            .unwrap(),
            default_config.clone(),
            rx,
            Arc::new(RAManager::new(default_config)),
        );
        assert_eq!(100.0_f64, pe.pressure_checking().await);
    }

    #[tokio::test]
    async fn test_ensure_scheduler_enought_minus() {
        let (mut pe, _) = __mk_pressure_evaluator_instance(__make_default_config()).await;
        pe.rescale_scheduler_set(-5).await;
        let sch_blk_lst = pe.scheduler_blacklist.read().await;
        for i in 1..=5 {
            assert!(sch_blk_lst.contains(&i.to_string()));
        }
    }

    /// 测试限制调度器最大数量功能 1（当前调度器数量不超过限制最大值）
    /// 预设 config.max_scheduler 为10010
    /// 测试1计划增加 6 个调度器，根据预设限制，可以增加 6 个调度器
    /// 测试2计划增加 10 个调度器，根据预设限制，可以增加 10 个调度器
    /// 测试3计划增加 20 个调度器，根据预设限制，只能增加 10 个调度器
    #[tokio::test]
    async fn test_max_scheduler_threshold_normal() {
        // 设定最大调度器数量
        let default_config = Arc::new(Configure {
            redis_host: vec!["redis://127.0.0.1/".to_string()],
            allowed_ra_communication_num: 8,
            scheduler_image: "None".to_string(),
            scheduler_cpu_core_count: 2,
            scheduler_mem_size: 1024,
            scheduler_balance_algorithm: "NONE".to_owned(),
            scheduler_balance_strategy: "NONE".to_owned(),
            scheduler_start_timeout_cycle: 3,
            max_scheduler: 10010,
            log_level: "INFO".to_owned(),
            keep_alive_cycle: 3,
            secs_per_cycle: 2,
            pid_kp: 50.0,
            pid_td: 2.0,
            pid_ti: 0.2,
            wf_per_scheduler_caculate_weight: 0.8,
            balance_method: "normal".to_owned(),
        });

        let (pe, _) = __mk_pressure_evaluator_instance(default_config).await;
        assert_eq!(6, pe.ensure_scheduler_number_threshold(6).await);
        assert_eq!(10, pe.ensure_scheduler_number_threshold(10).await);
        assert_eq!(10, pe.ensure_scheduler_number_threshold(20).await);
    }

    /// 测试限制调度器最大数量功能 2（当前调度器数量超过限制最大值）
    /// 预设 config.max_scheduler 为 900
    /// 无论要求增加多少调度器，均拒绝
    #[tokio::test]
    async fn test_max_scheduler_threshold_overflow() {
        // 设定最大调度器数量
        let default_config = Arc::new(Configure {
            redis_host: vec!["redis://127.0.0.1/".to_string()],
            allowed_ra_communication_num: 8,
            scheduler_image: "None".to_string(),
            scheduler_cpu_core_count: 2,
            scheduler_mem_size: 1024,
            scheduler_balance_algorithm: "NONE".to_owned(),
            scheduler_balance_strategy: "NONE".to_owned(),
            scheduler_start_timeout_cycle: 3,
            max_scheduler: 900,
            log_level: "INFO".to_owned(),
            keep_alive_cycle: 3,
            secs_per_cycle: 2,
            pid_kp: 50.0,
            pid_td: 2.0,
            pid_ti: 0.2,
            wf_per_scheduler_caculate_weight: 0.8,
            balance_method: "normal".to_owned(),
        });

        let (pe, _) = __mk_pressure_evaluator_instance(default_config).await;
        assert_eq!(0, pe.ensure_scheduler_number_threshold(6).await);
        assert_eq!(0, pe.ensure_scheduler_number_threshold(10).await);
        assert_eq!(0, pe.ensure_scheduler_number_threshold(20).await);
    }

    #[test]
    fn test_pid_calculate() {}

    #[tokio::test]
    async fn test_scheduler_updrade() {
        // 设定镜像地址
        let default_config = Arc::new(Configure {
            redis_host: vec!["redis://127.0.0.1/".to_string()],
            allowed_ra_communication_num: 8,
            scheduler_image: "192.168.1.11/workflow/scheduler:latest".to_string(),
            scheduler_cpu_core_count: 2,
            scheduler_mem_size: 1024,
            scheduler_balance_algorithm: "NONE".to_owned(),
            scheduler_balance_strategy: "NONE".to_owned(),
            scheduler_start_timeout_cycle: 3,
            max_scheduler: -1,
            log_level: "INFO".to_owned(),
            keep_alive_cycle: 3,
            secs_per_cycle: 2,
            pid_kp: 50.0,
            pid_td: 2.0,
            pid_ti: 0.2,
            wf_per_scheduler_caculate_weight: 0.8,
            balance_method: "normal".to_owned(),
        });
        let (mut pe, tx) = __mk_pressure_evaluator_instance(default_config).await;
        tx.send("192.168.1.11/workflow/scheduler:1.0".to_string())
            .unwrap();
        pe.check_scheduler_upgrade().await;
        assert_eq!(10000, pe.scheduler_register_table.read().await.len());

        tx.send("192.168.1.11/workflow/scheduler:latest".to_string())
            .unwrap();
        pe.check_scheduler_upgrade().await;
        assert_eq!(0, pe.scheduler_register_table.read().await.len());
    }
}
