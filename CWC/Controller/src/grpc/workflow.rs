#[derive(Clone, PartialEq, ::prost::Message)]
pub struct WorkflowNode {
    /// 工作流节点名称
    #[prost(string, tag = "1")]
    pub name: ::prost::alloc::string::String,
    /// 依赖(确认)
    #[prost(string, repeated, tag = "2")]
    pub dependencies: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
    /// image
    #[prost(string, tag = "3")]
    pub template: ::prost::alloc::string::String,
    /// phase（状态）
    #[prost(string, tag = "4")]
    pub phase: ::prost::alloc::string::String,
    /// status
    #[prost(string, tag = "5")]
    pub node_info: ::prost::alloc::string::String,
    /// cpu(核数)
    #[prost(uint32, tag = "6")]
    pub cpu: u32,
    /// 内存(Bytes)
    #[prost(uint64, tag = "7")]
    pub mem: u64,
    /// 环境变量
    #[prost(map = "string, string", tag = "8")]
    pub env:
        ::std::collections::HashMap<::prost::alloc::string::String, ::prost::alloc::string::String>,
    /// 输入向量
    #[prost(string, repeated, tag = "9")]
    pub input_vector: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
    /// 输出向量
    #[prost(string, repeated, tag = "10")]
    pub output_vector: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct Workflow {
    /// 工作流名称
    #[prost(string, tag = "1")]
    pub workflow_name: ::prost::alloc::string::String,
    /// 工作流custom_id
    #[prost(string, tag = "2")]
    pub custom_id: ::prost::alloc::string::String,
    /// 工作流类型
    #[prost(string, tag = "3")]
    pub style: ::prost::alloc::string::String,
    /// 是否为定制工作流
    #[prost(bool, tag = "4")]
    pub customization: bool,
    /// 时间等级(A,B,C,D)
    #[prost(string, tag = "5")]
    pub time_grade: ::prost::alloc::string::String,
    /// 花费（资金）等级(A,B,C,D)
    #[prost(string, tag = "6")]
    pub cost_grade: ::prost::alloc::string::String,
    /// 拓扑结构
    #[prost(message, repeated, tag = "7")]
    pub topology: ::prost::alloc::vec::Vec<WorkflowNode>,
}
