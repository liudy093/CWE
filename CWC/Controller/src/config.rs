use std::env;
#[derive(Debug)]
pub struct Configure {
    /// Redis 地址
    pub redis_host: Vec<String>,

    /// 允许的与资源分配器通信失败的最大次数。超过此数，管理器会删除对应的资源分配器，并在终端给出警告
    pub allowed_ra_communication_num: u32,

    /// 调度器镜像
    pub scheduler_image: String,

    /// 调度器POD的cpu数量（单位：核心数）
    pub scheduler_cpu_core_count: u32,

    /// 调度器POD的mem数量（单位：MB）
    pub scheduler_mem_size: u64,

    /// 调度器平衡算法
    /// 压力评价器在新增调度器时使用的算法，目前支持: NONE, PID, INCREASE
    pub scheduler_balance_algorithm: String,

    /// 调度器平衡策略
    /// 压力评价器在启停调度器时使用的策略，目前支持：NONE(默认), RADICAL
    pub scheduler_balance_strategy: String,

    /// 调度器启动等待超时周期（单位：个周期）
    /// 设为0，表示不设置等待队列
    pub scheduler_start_timeout_cycle: u32,

    /// 调度器最大数量
    /// 默认值 -1，表示不限制。大于0表示设定最大值
    pub max_scheduler: i32,

    /// 日志输出等级
    pub log_level: String,

    /// 保活信号周期。在此周期内没有检测到保活信号，认为调度器已经死亡
    pub keep_alive_cycle: u32,

    /// 每个周期多少秒
    pub secs_per_cycle: u32,

    /// PID 参数：Kp 比例
    pub pid_kp: f64,

    /// PID 参数：积分时间
    pub pid_ti: f64,

    /// PID 参数：微分时间
    pub pid_td: f64,

    /// 计算平均每个调度器能承载的工作流数量时指数加权移动平均算法使用的权重系数
    pub wf_per_scheduler_caculate_weight: f64,

    /// 平衡方法
    /// 'abs' 为绝对平均，不管调度器的压力和容量，每次每个调度器只分配一个工作流
    /// 'normal' 正常算法，考虑调度器的压力和容量
    pub balance_method: String,
}

impl Configure {
    pub fn new() -> Configure {
        Configure {
            redis_host: env::var("REDIS_HOST").map_or(
                vec![
                    "redis://127.0.0.1:7000/".to_owned(),
                    "redis://127.0.0.1:7001/".to_owned(),
                    "redis://127.0.0.1:7002/".to_owned(),
                ],
                |val| {
                    val.split(',')
                        .map(|s| format!("redis://{}/", s.trim()))
                        .collect()
                },
            ),
            allowed_ra_communication_num: env::var("ALLOWED_RA_COMMUNICATION_NUM")
                .map_or(8, |val| val.parse::<u32>().unwrap()),
            scheduler_image: env::var("SCHEDULER_IMAGE")
                .unwrap_or("harbor.cloudcontrolsystems.cn/workflow/scheduler:latest".to_string()),
            scheduler_cpu_core_count: env::var("SCHEDULER_CPU_CORE_COUNT")
                .map_or(2, |val| val.parse::<u32>().unwrap()),
            scheduler_mem_size: env::var("SCHEDULER_MEM_SIZE")
                .map_or(1024, |val| val.parse::<u64>().unwrap()),
            scheduler_balance_algorithm: env::var("SCHEDULER_BALANCE_ALGORITHM")
                .unwrap_or("NONE".to_string()),
            scheduler_balance_strategy: env::var("SCHEDULER_BALANCE_STRATEGY")
                .unwrap_or("NONE".to_string()),
            scheduler_start_timeout_cycle: env::var("SCHEDULER_START_TIMEOUT_CYCLE")
                .map_or(3, |val| val.parse::<u32>().unwrap()),
            max_scheduler: env::var("MAX_SCHEDULER").map_or(-1, |val| val.parse::<i32>().unwrap()),
            log_level: env::var("LOG_LEVEL").unwrap_or("DEBUG".to_string()),
            keep_alive_cycle: env::var("KEEP_ALIVE_CYCLE")
                .map_or(3, |val| val.parse::<u32>().unwrap()),
            secs_per_cycle: env::var("SECS_PER_CYCLE").map_or(2, |val| val.parse::<u32>().unwrap()),
            pid_kp: env::var("KP").map_or(50.0, |val| val.parse::<f64>().unwrap()),
            pid_ti: env::var("TI").map_or(2.0, |val| val.parse::<f64>().unwrap()),
            pid_td: env::var("TD").map_or(0.2, |val| val.parse::<f64>().unwrap()),
            wf_per_scheduler_caculate_weight: env::var("SC_W")
                .map_or(0.8, |val| val.parse::<f64>().unwrap()),
            balance_method: env::var("BALANCE_METHOD").unwrap_or("normal".to_string()),
        }
    }
}

impl Default for Configure {
    fn default() -> Configure {
        Configure {
            redis_host: vec![
                "redis://127.0.0.1:7000/".to_owned(),
                "redis://127.0.0.1:7001/".to_owned(),
                "redis://127.0.0.1:7002/".to_owned(),
            ],
            allowed_ra_communication_num: 8,
            scheduler_image: "None".to_string(),
            scheduler_cpu_core_count: 2,
            scheduler_mem_size: 1024,
            scheduler_balance_algorithm: "NONE".to_owned(),
            scheduler_balance_strategy: "NONE".to_owned(),
            scheduler_start_timeout_cycle: 3,
            max_scheduler: -1,
            keep_alive_cycle: 3,
            log_level: "DEBUG".to_owned(),
            secs_per_cycle: 2,
            pid_kp: 50.0,
            pid_td: 2.0,
            pid_ti: 0.2,
            wf_per_scheduler_caculate_weight: 0.8,
            balance_method: "normal".to_owned(),
        }
    }
}
