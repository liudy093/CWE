**调度器控制器**

新核心系统的入口，实现向调度器分配工作流；根据系统负载压力，管理调度器生命周期。

[![Build Status](https://drone.cloudcontrolsystems.cn/api/badges/CloudTeam/Controller/status.svg)](https://drone.cloudcontrolsystems.cn/CloudTeam/Controller)

[TOC]

# 配置使用 RUST crate.io 的清华镜像

在国内访问 crate.io 比较慢，推荐使用清华的镜像

克隆仓库后，仓库目录下的 `cargo_crates.config` 文件拷贝为 `$HOME/.cargo/config` 即可，具体可以参考[清华镜像网站的说明](https://lug.ustc.edu.cn/wiki/mirrors/help/rust-crates)

# 关于 grpc_proto 子模块

项目所有proto存入独立库：grpc_proto

本模块将grpc_proto作为子模块引入到grpc_proto目录，根据需要手工与仓库同步。

## 初始化
克隆本仓库后，为了同步子模块，需要在本仓库克隆后的根目录下执行: `git submodule init` 和 `git submodule update`

## 修改子模块内容

修改grpc_proto目录下的任何内容，需要提交到grpc_proto仓库。

## 与本模块相关的proto文件

### 本模块定义的proto文件

| 文件名                     | service             |   rpc   |  message |
| :------------------------- | :------------------ | :--- | -------------------------- |
| scheduler_controller.proto | SchedulerController |KeepAlive| KeepAliveRequest, KeepAliveReply |

# 关于 gRPC
## 向外提供 gRPC 服务(Server)

编译阶段，编译器会根据 build.rs 文件描述，读取 grpc_proto 目录下指定的 proto 文件，并生成对应的 rust 源码存入 src/grpc 目录下。如有需要可以修改 build.rs 控制生成过程。

### 实现 service

本模块实现 gRPC Service 相关代码存放在 sc_grpc.rs 文件里(mod sc_grpc)。

sc_grpc.rs 文件里定义了SchedulerControllerGRPC，并实现了(impl)编译器从scheduler_controller.proto自动生成的 trait SchedulerController

请根据需要，为不同功能的 gRPC Service 实现代码设立相应(独立)mod，并在 sc_grpc.rs 文件里调用相应mod里的函数，完成具体功能。

## 调用外部 gRPC 服务(Client)

# 关于 Prometheus 指标导出
## 模块内的功能性 struct
1. 定义想要收集的指标。根据需要从 Counter、Gauge、Histogram 三个指标中选择要记录的类型。
1. 在struct初始化的时候，初始化指标。
1. 实现(impl) 在 metric_endpoint.rs 文件里的 PrometheusRegistry 特质(trait)。目的为了注册指标。
1. 在 main.rs 文件的 main 函数里，调用相关模块的 register_metric 函数，注册指标

## prometheus 配置
```yaml
- job_name: 'controller'
  scrape_interval: 2s
  static_configs:
    - targets:
      - 172.28.0.14:30162
```
`注意` targets 需要填入控制器的地址

# Git Commit Log 说明
参考 [Commit message 和 Change log 编写指南](http://www.ruanyifeng.com/blog/2016/01/commit_message_change_log.html)

用于 Commit 的 `type` 只允许使用下面7个标识：
> - feat：新功能（feature）
  - fix：修补bug
  - docs：文档（documentation）
  - style： 格式（不影响代码运行的变动）
  - refactor：重构（即不是新增功能，也不是修改bug的代码变动）
  - test：增加测试
  - chore：构建过程或辅助工具的变动

# 制作Docker镜像
## BIT Harbor
使用 `deploy.bit.harbor.sh` 制作镜像并推送到[harbor仓库](https://harbor.cloudcontrolsystems.cn/ccs/)
## 鲁南大数据中心
使用 `deploy.xxl.harbor.sh`制作镜像并推送到[harbor仓库](http://192.168.6.90/workflow/)

`注意：` 192.168.6.90 仓库没有配置 ssl，需要把 192.168.6.90 地址所有访问此仓库计算机的 docker 可信地址列表里


# 支持的环境变量

| 名称                  | 类型   | 默认值    | 说明               |
| --------------------- | ------ | --------- | ------------------ |
| REDIS_HOST            | String | 127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002 | redis 集群所有master主机的ip地址。多个地址之间使用','隔开，最后结尾不要有',' |
|ALLOWED_RA_COMMUNICATION_NUM| u32 | 8 | 允许的与资源分配器通信失败的最大次数。超过此数，管理器会删除对应的资源分配器，并在终端给出警告 |
| SCHEDULER_IMAGE       | String | harbor.cloudcontrolsystems.cn/workflow/scheduler:latest | 调度器镜像 |
|SCHEDULER_CPU_CORE_COUNT| u32 | 2 | 调度器pod的需求核心数 |
|SCHEDULER_MEM_SIZE| u64 | 1024 | 调度器pod的需求内存（单位：MB） |
|SCHEDULER_BALANCE_ALGORITHM| String | NONE  | 调度器平衡算法。压力评价器在新增调度器时使用的算法，目前支持: NONE, PID, INCREASE(在None的基础上只增不减调度器，即关闭缩减调度器功能) |
|SCHEDULER_BALANCE_STRATEGY(1*)| String | NONE | 调度器平衡策略。压力评价器在启停调度器时使用的策略，目前支持：NONE(默认)，RADICAL |
|SCHEDULER_START_TIMEOUT_CYCLE| u32 | 3 | 调度器启动等待超时周期（单位：个周期，每个周期2秒）。设为0，表示不设置等待队列 |
|MAX_SCHEDULER| U32 | -1 | 允许开启调度器数量。小于零表示不做限制。 |
| LOG_LEVEL             | String | DEBUG      | 日志输出等级，支持: ERROR, WARN, INFO, DEBUG  |
| KEEP_ALIVE_CYCLE | u32 | 3 | 保活信号超时周期（每个周期1秒）。在此周期内没有收到调度器的保活信号认为调度器已经死亡 |
|SECS_PER_CYCLE|  u32  | 2 | 每个周期是几秒 |
| KP                    | f64    | 50        | 比例               |
| KI                    | f64    | 2.0       | 积分时间           |
| KD                    | f64    | 0.2       | 微分时间           |
| SC_W                  | f64    | 0.8(约5次平均) | 计算平均每个调度器能承载的工作流数量时指数加权移动平均算法使用的权重系数 |
| BALANCE_METHOD | String | normal | 平衡方法。abs' 为绝对平均，不管调度器的压力和容量，每次每个调度器只分配一个工作流。'normal' 正常算法，考虑调度器的压力和容量 |

1* SCHEDULER_BALANCE_STRATEGY 压力评价器在启停调度器时使用的策略。目前支持：
   1. NONE: 不设定策略。当前工作流队列里等待的工作流数量满足一个调度器的批量调度数量时，才启动调度器
   1. RADICAL: 激进策略。只要有一个工作流没有可分配调度器就新启动调度器

# 自动化部署
## 预备
1. 建立编译虚机，操作系统为 ubuntu 20.04，命名为 builder，将 ip 地址填入 publish/invetory 文件里的 builder 项目
1. 在publish目录下执行: `ansible-playbook -i inventory init.ubuntu.ansible.yml` 初始化虚机

## 提交编译
在 publish 目录下执行: 
```bash
   ansible-playbook -i inventory -e "TARGET_TO=xxl" publish.script.ansible.yml
```
其中，可以通过 TARGET_TO 设置编译后的镜像执行的目的k8s集群。TARGET_TO 支持 bit, xxl

# 与调度器之间的数据约定
## Redis中的A表
key: xxxx-A 的形式
value: 压缩后的原始工作流
redis数据类型: string
## Redis中的B表
key: xxxxx-B 的形式
value: 
     key: workflowState 或 task-name
     value: 状态（UNEXECUTE、）
redis数据类型: hash table

## Redis中 custom id 与 workflow id之间的映射关系
key: custome id
value: workflow id
redis数据类型: string