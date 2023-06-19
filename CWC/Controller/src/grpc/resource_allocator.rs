///资源请求参数
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct ResourceRequest {
    ///调度器id
    #[prost(string, tag = "1")]
    pub scheduler_id: ::prost::alloc::string::String,
    ///时间戳
    #[prost(int64, tag = "2")]
    pub time_stamp: i64,
    ///该调度器当前批次ready_tasks资源请求量
    #[prost(message, optional, tag = "3")]
    pub current_request: ::core::option::Option<resource_request::ResourceDemand>,
    ///该调度器下一批次ready_tasks资源请求量
    #[prost(message, optional, tag = "4")]
    pub next_request: ::core::option::Option<resource_request::ResourceDemand>,
    ///当前该调度器所有待分配任务资源请求总量  
    #[prost(message, optional, tag = "5")]
    pub all_tasks_request: ::core::option::Option<resource_request::ResourceDemand>,
    ///time_grade暂时用不到。若customization=true且cost_grade=false，则请求的资源都满足；否则，按之前流程走
    ///
    ///该任务是否属于定制工作流
    #[prost(bool, tag = "6")]
    pub customization: bool,
    /// 是否存在时间等级
    #[prost(bool, tag = "7")]
    pub time_grade: bool,
    /// 是否花费（资金）等级
    #[prost(bool, tag = "8")]
    pub cost_grade: bool,
}
/// Nested message and enum types in `ResourceRequest`.
pub mod resource_request {
    #[derive(Clone, PartialEq, ::prost::Message)]
    pub struct ResourceDemand {
        ///基本单位millicore(1Core=1000millicore) [default = 50]
        #[prost(int64, tag = "1")]
        pub cpu: i64,
        ///基本单位Mi,1024*1024byte[default = 50]
        #[prost(int64, tag = "2")]
        pub mem: i64,
    }
}
///分配资源信息
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct ResourceAllocateInfo {
    #[prost(string, tag = "1")]
    pub scheduler_id: ::prost::alloc::string::String,
    #[prost(message, optional, tag = "2")]
    pub current_request: ::core::option::Option<resource_allocate_info::ResourceDemand>,
    #[prost(bool, tag = "3")]
    pub current_request_status: bool,
}
/// Nested message and enum types in `ResourceAllocateInfo`.
pub mod resource_allocate_info {
    #[derive(Clone, PartialEq, ::prost::Message)]
    pub struct ResourceDemand {
        ///基本单位millicore(1Core=1000millicore) [default = 50]
        #[prost(int64, tag = "1")]
        pub cpu: i64,
        ///基本单位Mi,1024*1024byte[default = 100]
        #[prost(int64, tag = "2")]
        pub mem: i64,
    }
}
/// 请求创建调度器POD
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct CreateSchedulerPodRequest {
    ///镜像
    #[prost(string, tag = "1")]
    pub image: ::prost::alloc::string::String,
    ///基本单位 millicore(1Core=1000millicore)
    #[prost(int64, tag = "2")]
    pub cpu: i64,
    ///基本单位 MiB
    #[prost(int64, tag = "3")]
    pub mem: i64,
    ///需要输入给 POD 的环境变量
    #[prost(string, repeated, tag = "4")]
    pub env: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct CreateSchedulerPodResponse {
    ///成功创建,根据当前剩余资源返回创建调度起pod的上限数 成功>=1，失败置为0）
    #[prost(int32, tag = "1")]
    pub result: i32,
    ///在失败状态下，可以设置状态码
    ///成功状态（result>=1），客户端不关系此字段，置为 0 即可
    #[prost(int32, tag = "2")]
    pub err_no: i32,
}
///请求创建任务POD
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct CreateTaskPodRequest {
    ///workflow的ID
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
    ///taskName
    #[prost(string, tag = "2")]
    pub task_name: ::prost::alloc::string::String,
    ///任务镜像
    #[prost(string, tag = "3")]
    pub image: ::prost::alloc::string::String,
    ///基本单位 millicore(1Core=1000millicore)
    #[prost(int64, tag = "4")]
    pub cpu: i64,
    ///基本单位 MiB
    #[prost(int64, tag = "5")]
    pub mem: i64,
    ///需要输入给 POD 的环境变量
    #[prost(map = "string, string", tag = "6")]
    pub env:
        ::std::collections::HashMap<::prost::alloc::string::String, ::prost::alloc::string::String>,
    /// 输入向量
    #[prost(string, repeated, tag = "7")]
    pub input_vector: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
    /// 输出向量
    #[prost(string, repeated, tag = "8")]
    pub output_vector: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
    ///time_grade暂时用不到。若customization=true且cost_grade=false，则pod配置资源最大化，即request=limit；若customization=true且cost_grade=true，则pod配置资源最小化；否则，按之前流程走
    ///
    ///该任务是否属于定制工作流
    #[prost(bool, tag = "9")]
    pub customization: bool,
    /// 是否存在时间等级
    #[prost(bool, tag = "10")]
    pub time_grade: bool,
    /// 是否花费（资金）等级
    #[prost(bool, tag = "11")]
    pub cost_grade: bool,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct CreateTaskPodResponse {
    ///成功创建 pod 的状态码（成功>=1，失败置为0）
    #[prost(int32, tag = "1")]
    pub result: i32,
    ///pod共享存储路径
    #[prost(string, tag = "2")]
    pub volume_path: ::prost::alloc::string::String,
    ///在失败状态下，可以设置状态码
    ///成功状态（result>=1），客户端不关系此字段，置为 0 即可
    #[prost(int32, tag = "3")]
    pub err_no: i32,
}
///调度器控制器推送调度器保活信号
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct UpdateSchedulerAliveStatusRequest {
    ///调度器Id
    #[prost(string, tag = "1")]
    pub scheduler_id: ::prost::alloc::string::String,
    ///保活状态
    #[prost(bool, tag = "2")]
    pub alive_status: bool,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct UpdateSchedulerAliveStatusResponse {
    ///成功更新调度器保活状态的状态码（成功>=1，失败置为0）
    #[prost(int32, tag = "1")]
    pub result: i32,
    ///在失败状态下，可以设置状态码
    ///客户端不关系此字段，置为 0 即可
    #[prost(int32, tag = "2")]
    pub err_no: i32,
}
///调度器发送给资源分配器执行完的工作流
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DeleteWorkflowNamespaceRequest {
    ///workflow的ID
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DeleteWorkflowNamespaceResponse {
    ///资源分配器删除该工作流的namespace的状态码（成功>=1，失败置为0）
    #[prost(int32, tag = "1")]
    pub result: i32,
}
///调度器控制器获取可分配的调度器数量请求
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct GetSchedulerNumRequest {
    ///调度器的资源request
    #[prost(int64, tag = "1")]
    pub cpu: i64,
    #[prost(int64, tag = "2")]
    pub mem: i64,
}
///响应调度器控制器请求
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct GetSchedulerNumResponse {
    #[prost(int64, tag = "1")]
    pub scheduler_pod_num: i64,
}
#[doc = r" Generated client implementations."]
pub mod resource_request_service_client {
    #![allow(unused_variables, dead_code, missing_docs)]
    use tonic::codegen::*;
    #[doc = "资源请求服务service"]
    pub struct ResourceRequestServiceClient<T> {
        inner: tonic::client::Grpc<T>,
    }
    impl ResourceRequestServiceClient<tonic::transport::Channel> {
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
    impl<T> ResourceRequestServiceClient<T>
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
        pub async fn get_resource_allocate_info(
            &mut self,
            request: impl tonic::IntoRequest<super::ResourceRequest>,
        ) -> Result<tonic::Response<super::ResourceAllocateInfo>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/resource_allocator.ResourceRequestService/GetResourceAllocateInfo",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn create_scheduler_pod(
            &mut self,
            request: impl tonic::IntoRequest<super::CreateSchedulerPodRequest>,
        ) -> Result<tonic::Response<super::CreateSchedulerPodResponse>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/resource_allocator.ResourceRequestService/CreateSchedulerPod",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn create_task_pod(
            &mut self,
            request: impl tonic::IntoRequest<super::CreateTaskPodRequest>,
        ) -> Result<tonic::Response<super::CreateTaskPodResponse>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/resource_allocator.ResourceRequestService/CreateTaskPod",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn delete_workflow_namespace(
            &mut self,
            request: impl tonic::IntoRequest<super::DeleteWorkflowNamespaceRequest>,
        ) -> Result<tonic::Response<super::DeleteWorkflowNamespaceResponse>, tonic::Status>
        {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/resource_allocator.ResourceRequestService/DeleteWorkflowNamespace",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn update_scheduler_alive_status(
            &mut self,
            request: impl tonic::IntoRequest<super::UpdateSchedulerAliveStatusRequest>,
        ) -> Result<tonic::Response<super::UpdateSchedulerAliveStatusResponse>, tonic::Status>
        {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/resource_allocator.ResourceRequestService/UpdateSchedulerAliveStatus",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
        pub async fn get_allocatable_scheduler_num(
            &mut self,
            request: impl tonic::IntoRequest<super::GetSchedulerNumRequest>,
        ) -> Result<tonic::Response<super::GetSchedulerNumResponse>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/resource_allocator.ResourceRequestService/GetAllocatableSchedulerNum",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
    }
    impl<T: Clone> Clone for ResourceRequestServiceClient<T> {
        fn clone(&self) -> Self {
            Self {
                inner: self.inner.clone(),
            }
        }
    }
    impl<T> std::fmt::Debug for ResourceRequestServiceClient<T> {
        fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
            write!(f, "ResourceRequestServiceClient {{ ... }}")
        }
    }
}
