#[derive(Clone, PartialEq, ::prost::Message)]
pub struct WorkflowTransferRequest {
    #[prost(string, tag = "1")]
    pub sid: ::prost::alloc::string::String,
    /// 工作流binary array文件
    #[prost(bytes = "vec", repeated, tag = "2")]
    pub workflow: ::prost::alloc::vec::Vec<::prost::alloc::vec::Vec<u8>>,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct WorkflowTransferReply {
    /// 返回标记符，表示收到文件
    #[prost(uint32, tag = "1")]
    pub accept: u32,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct TaskStateRequest {
    ///workflowXX, 查询时将直接其作为namespace
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
    #[prost(message, repeated, tag = "2")]
    pub node_state: ::prost::alloc::vec::Vec<task_state_request::NodesState>,
    ///调度器所在的IP
    #[prost(string, tag = "3")]
    pub ip: ::prost::alloc::string::String,
}
/// Nested message and enum types in `TaskStateRequest`.
pub mod task_state_request {
    #[derive(Clone, PartialEq, ::prost::Message)]
    pub struct NodesState {
        #[prost(uint32, tag = "1")]
        pub task_id: u32,
        ///例如task-12-XX-XX. X个数不确定
        #[prost(string, tag = "2")]
        pub task_pod_name: ::prost::alloc::string::String,
        #[prost(string, tag = "3")]
        pub task_state: ::prost::alloc::string::String,
    }
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct TaskStateReply {
    #[prost(uint32, tag = "1")]
    pub accept: u32,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DeleteWorkflowRequest {
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
    #[prost(string, tag = "2")]
    pub custom_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DeleteWorkflowReply {
    /// 返回标记符，1表示成功删除，0表示失败
    #[prost(uint32, tag = "1")]
    pub result: u32,
    /// 如果失败，填具体消息
    #[prost(string, tag = "2")]
    pub error_message: ::prost::alloc::string::String,
}
#[doc = r" Generated client implementations."]
pub mod scheduler_client {
    #![allow(unused_variables, dead_code, missing_docs)]
    use tonic::codegen::*;
    pub struct SchedulerClient<T> {
        inner: tonic::client::Grpc<T>,
    }
    impl SchedulerClient<tonic::transport::Channel> {
        #[doc = r" Attempt to create a new client by connecting to a given endpoint."]
        pub async fn connect<D>(dst: D) -> Result<Self, tonic::transport::Error>
        where
            D: std::convert::TryInto<tonic::transport::Endpoint>,
            D::Error: Into<StdError>,
        {
            let conn = tonic::transport::Endpoint::new(dst)?.connect().await?;
            Ok(Self::new(conn))
        }
    }
    impl<T> SchedulerClient<T>
    where
        T: tonic::client::GrpcService<tonic::body::BoxBody>,
        T::ResponseBody: Body + HttpBody + Send + 'static,
        T::Error: Into<StdError>,
        <T::ResponseBody as HttpBody>::Error: Into<StdError> + Send,
    {
        pub fn new(inner: T) -> Self {
            let inner = tonic::client::Grpc::new(inner);
            Self { inner }
        }
        pub fn with_interceptor(inner: T, interceptor: impl Into<tonic::Interceptor>) -> Self {
            let inner = tonic::client::Grpc::with_interceptor(inner, interceptor);
            Self { inner }
        }
        #[doc = " 调度器控制器向调度器发送工作流DAG文件"]
        pub async fn workflow_transfer(
            &mut self,
            request: impl tonic::IntoRequest<super::WorkflowTransferRequest>,
        ) -> Result<tonic::Response<super::WorkflowTransferReply>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path =
                http::uri::PathAndQuery::from_static("/scheduler.Scheduler/WorkflowTransfer");
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn input_task_state(
            &mut self,
            request: impl tonic::IntoRequest<super::TaskStateRequest>,
        ) -> Result<tonic::Response<super::TaskStateReply>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static("/scheduler.Scheduler/InputTaskState");
            self.inner.unary(request.into_request(), path, codec).await
        }
        #[doc = " 删除工作流"]
        pub async fn delete_workflow(
            &mut self,
            request: impl tonic::IntoRequest<super::DeleteWorkflowRequest>,
        ) -> Result<tonic::Response<super::DeleteWorkflowReply>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static("/scheduler.Scheduler/DeleteWorkflow");
            self.inner.unary(request.into_request(), path, codec).await
        }
    }
    impl<T: Clone> Clone for SchedulerClient<T> {
        fn clone(&self) -> Self {
            Self {
                inner: self.inner.clone(),
            }
        }
    }
    impl<T> std::fmt::Debug for SchedulerClient<T> {
        fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
            write!(f, "SchedulerClient {{ ... }}")
        }
    }
}
