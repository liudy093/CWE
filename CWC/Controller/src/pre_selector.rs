use super::keep_alive::unix_timestamp;
use super::metric_endpoint::PrometheusRegistry;
use super::tracker::SchedulerRegisterTable;
use prometheus::{IntCounter, Opts, Registry};
use std::collections::HashMap;
use std::sync::Arc;
use tokio::sync::RwLock;
use tokio::time::{sleep, Duration};

// 调度器预选表
pub type SchedulerPreSelectedTable = Vec<SchedulerPreSelectedItem>;

#[derive(Debug)]
/// 调度器预选表项
pub struct SchedulerPreSelectedItem {
    // 调度器id
    pub sid: std::string::String,

    // ipv4地址
    pub ipv4: std::string::String,

    /// 调度器的真实承载力
    pub valid_cap: u32,

    /// 集群id
    pub cluster_id: String,
}

#[derive(Debug)]
pub struct SchedulerPreSelector {
    /// 调度器预选表
    preselected_table: Arc<RwLock<SchedulerPreSelectedTable>>,

    /// 调度器注册表的ARC引用（来自 Tracker）
    scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,

    /// Prometheus 指标: pre_selector 工作次数计数
    pre_selector_work_count: IntCounter,
}

impl SchedulerPreSelector {
    pub fn new(
        scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,
    ) -> SchedulerPreSelector {
        SchedulerPreSelector {
            preselected_table: Arc::new(RwLock::new(Vec::new())),

            scheduler_register_table,

            pre_selector_work_count: IntCounter::with_opts(Opts::new(
                "pre_selector_work_count",
                "pre_selector work count",
            ))
            .unwrap(),
        }
    }

    async fn order_scheduler(&self) {
        // 读取状态表信息
        let sch_register_table = self.scheduler_register_table.read().await;

        // 将调度器注册表内的所有调度器按照集群分开，形成分集群的调度器预选表
        let mut max_queue_len = 0u32;
        let mut _pre_selected_table_by_cluster: HashMap<String, Vec<SchedulerPreSelectedItem>> =
            HashMap::new();
        for (sch_id, sch) in sch_register_table.iter() {
            let valid_cap =
                ((100.0f64 - sch.pressure as f64) / 100.0f64 * sch.capacity as f64).round() as u32;
            if valid_cap > 0 {
                _pre_selected_table_by_cluster
                    .entry(sch.cluster_id.clone())
                    .or_insert_with(|| Vec::new())
                    .push(SchedulerPreSelectedItem {
                        sid: sch_id.to_string(),
                        ipv4: sch.ipv4.clone(),
                        valid_cap,
                        cluster_id: sch.cluster_id.clone(),
                    });

                let v = _pre_selected_table_by_cluster.get(&sch.cluster_id).unwrap();
                max_queue_len = max_queue_len.max(v.len() as u32);
            }
        }
        drop(sch_register_table); // 开锁调度器注册表

        for (_, sch_ps_items) in _pre_selected_table_by_cluster.iter_mut() {
            // 每个集群内的所有预选调度器，按照真实承载力从大到小排序
            sch_ps_items.sort_unstable_by_key(|v| v.valid_cap);
            sch_ps_items.reverse();
        }

        // 把根据集群分割的调度器预选表合并成一个调度器预选表
        // 使用zip模式合并各个集群内的调度器预选表
        let mut _pre_selected_table: Vec<SchedulerPreSelectedItem> = Vec::new();
        for _ in 0..max_queue_len {
            for (_, sch_ps_items) in _pre_selected_table_by_cluster.iter_mut() {
                if let Some(itm) = sch_ps_items.pop() {
                    _pre_selected_table.push(itm);
                }
            }
        }

        // 替换已经存在的调度器预选表
        let mut preselected_table = self.preselected_table.write().await;
        preselected_table.clear();
        preselected_table.append(&mut _pre_selected_table);
    }

    pub async fn run(&self) {
        loop {
            self.pre_selector_work_count.inc();
            self.order_scheduler().await;

            let tp = unix_timestamp(); //前一次时间
            sleep(Duration::new(2, 0)).await;
            let tc = unix_timestamp(); //当前时间
            if tc - tp > 4 {
                warn!("[调度器预选器] 等待时延超过正常情况2倍");
            }
        }
    }

    pub fn get_preselected_table(&self) -> Arc<RwLock<SchedulerPreSelectedTable>> {
        self.preselected_table.clone()
    }
}

impl PrometheusRegistry for SchedulerPreSelector {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.pre_selector_work_count.clone()))
            .unwrap();
    }
}

#[cfg(test)]
mod tests {
    use super::super::tracker::SchedulerRegisterItem;
    use super::*;

    async fn __make_scheduler_preselector() -> SchedulerPreSelector {
        let srt: Arc<RwLock<SchedulerRegisterTable>> =
            Arc::new(RwLock::new(SchedulerRegisterTable::new()));
        {
            let mut srt_w = srt.write().await;
            for i in 1..=10_000 {
                srt_w.insert(
                    i.to_string(),
                    SchedulerRegisterItem {
                        pressure: i / 100,
                        capacity: i,
                        ipv4: String::from("192.168.1.101"),
                        state: crate::tracker::SchedulerState::Running,
                        cluster_id: String::from("123"),
                        keep_alive_delay: 1,
                    },
                );
            }
        }

        SchedulerPreSelector::new(srt)
    }

    #[tokio::test]
    async fn test_gen_pre_selected_table() {
        let sps = __make_scheduler_preselector().await;
        sps.order_scheduler().await;
        let st = sps.preselected_table.read().await;
        assert!(st.first().unwrap().valid_cap == 2550);
        assert!(st.last().unwrap().valid_cap == 1);
    }
}
