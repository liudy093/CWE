#[derive(Clone, PartialEq, ::prost::Message)]
pub struct KeepAliveRequest {
    /// 调度器id
    #[prost(string, tag = "1")]
    pub sid: ::prost::alloc::string::String,
    /// 调度器自测压力值（0~100，整数）
    #[prost(fixed32, tag = "2")]
    pub pressure: u32,
    /// 调度器承载力（还可以承载/接收多少工作流，单位：个，整数）
    #[prost(fixed32, tag = "3")]
    pub capacity: u32,
    /// 保活序号（单调增，整数，从1开始）
    #[prost(uint64, tag = "4")]
    pub serial_number: u64,
    /// ipv4地址(ip:port)
    #[prost(string, tag = "5")]
    pub ipv4: ::prost::alloc::string::String,
    /// 集群id（必须保证与集群内资源分配器使用相同的集群id）
    #[prost(string, tag = "6")]
    pub cluster_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct KeepAliveReply {
    /// 下一次发送保活信号等待时间(从收到此应答到再次发送保活信号的等待时间，单位：秒)
    /// 不会小于 1 秒
    #[prost(int32, tag = "1")]
    pub wait_secs: i32,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct InputWorkflowRequest {
    ///
    #[prost(bytes = "vec", repeated, tag = "1")]
    pub workflow: ::prost::alloc::vec::Vec<::prost::alloc::vec::Vec<u8>>,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct InputWorkflowReply {
    #[prost(uint32, tag = "1")]
    pub accept: u32,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct WorkflowIdListRequest {}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct WorkflowIdListReply {
    #[prost(string, repeated, tag = "1")]
    pub ids: ::prost::alloc::vec::Vec<::prost::alloc::string::String>,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct GetWorkflowByIdRequest {
    /// 调度器内的工作流id，如果同时指定 workflow_id 和 custom_id，优先使用
    /// workflow_id
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
    /// 后端有意义的 custom_id。至少要指定 workflow_id 和 custom_id 中的一个
    /// 必须提供 custom_id
    #[prost(string, tag = "2")]
    pub custom_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct GetWorkflowByIdReply {
    #[prost(message, optional, tag = "1")]
    pub workflow_dag: ::core::option::Option<super::workflow::Workflow>,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct RegisterRaRequest {
    /// 集群id
    #[prost(string, tag = "1")]
    pub cluster_id: ::prost::alloc::string::String,
    /// ipv4地址(ip:port)
    #[prost(string, tag = "2")]
    pub ipv4: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct RegisterRaReply {
    #[prost(string, tag = "1")]
    pub boot_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct GetWorkflowPhaseByCustomIdRequest {
    #[prost(string, tag = "1")]
    pub custom_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct GetWorkflowPhaseByCustomIdReply {
    #[prost(string, tag = "1")]
    pub phase: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DeleteWorkflowRequest {
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
    /// 必须提供 custom_id
    #[prost(string, tag = "2")]
    pub custom_id: ::prost::alloc::string::String,
}
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct DeleteWorkflowReply {
    #[prost(string, tag = "1")]
    pub workflow_id: ::prost::alloc::string::String,
}
#[doc = r" Generated server implementations."]
pub mod scheduler_controller_server {
    #![allow(unused_variables, dead_code, missing_docs)]
    use tonic::codegen::*;
    #[doc = "Generated trait containing gRPC methods that should be implemented for use with SchedulerControllerServer."]
    #[async_trait]
    pub trait SchedulerController: Send + Sync + 'static {
        #[doc = " 调度器内保活器向调度器控制器发送保活信号"]
        async fn keep_alive(
            &self,
            request: tonic::Request<super::KeepAliveRequest>,
        ) -> Result<tonic::Response<super::KeepAliveReply>, tonic::Status>;
        #[doc = " 前端输入工作流（压缩）"]
        async fn input_workflow(
            &self,
            request: tonic::Request<super::InputWorkflowRequest>,
        ) -> Result<tonic::Response<super::InputWorkflowReply>, tonic::Status>;
        #[doc = " 前端输入工作流（未压缩）"]
        async fn input_workflow_raw(
            &self,
            request: tonic::Request<super::InputWorkflowRequest>,
        ) -> Result<tonic::Response<super::InputWorkflowReply>, tonic::Status>;
        #[doc = " 导出工作流ID"]
        async fn fetch_workflow_id_list(
            &self,
            request: tonic::Request<super::WorkflowIdListRequest>,
        ) -> Result<tonic::Response<super::WorkflowIdListReply>, tonic::Status>;
        #[doc = " 选择某一个工作流查看DAG"]
        async fn get_workflow_by_id(
            &self,
            request: tonic::Request<super::GetWorkflowByIdRequest>,
        ) -> Result<tonic::Response<super::GetWorkflowByIdReply>, tonic::Status>;
        #[doc = " 注册资源分配器"]
        async fn register_resource_allocator(
            &self,
            request: tonic::Request<super::RegisterRaRequest>,
        ) -> Result<tonic::Response<super::RegisterRaReply>, tonic::Status>;
        #[doc = " 获取工作流当前状态"]
        async fn get_workflow_phase_by_custom_id(
            &self,
            request: tonic::Request<super::GetWorkflowPhaseByCustomIdRequest>,
        ) -> Result<tonic::Response<super::GetWorkflowPhaseByCustomIdReply>, tonic::Status>;
        #[doc = " 删除工作流"]
        async fn delete_workflow(
            &self,
            request: tonic::Request<super::DeleteWorkflowRequest>,
        ) -> Result<tonic::Response<super::DeleteWorkflowReply>, tonic::Status>;
    }
    #[derive(Debug)]
    pub struct SchedulerControllerServer<T: SchedulerController> {
        inner: _Inner<T>,
    }
    struct _Inner<T>(Arc<T>, Option<tonic::Interceptor>);
    impl<T: SchedulerController> SchedulerControllerServer<T> {
        pub fn new(inner: T) -> Self {
            let inner = Arc::new(inner);
            let inner = _Inner(inner, None);
            Self { inner }
        }
        pub fn with_interceptor(inner: T, interceptor: impl Into<tonic::Interceptor>) -> Self {
            let inner = Arc::new(inner);
            let inner = _Inner(inner, Some(interceptor.into()));
            Self { inner }
        }
    }
    impl<T, B> Service<http::Request<B>> for SchedulerControllerServer<T>
    where
        T: SchedulerController,
        B: HttpBody + Send + Sync + 'static,
        B::Error: Into<StdError> + Send + 'static,
    {
        type Response = http::Response<tonic::body::BoxBody>;
        type Error = Never;
        type Future = BoxFuture<Self::Response, Self::Error>;
        fn poll_ready(&mut self, _cx: &mut Context<'_>) -> Poll<Result<(), Self::Error>> {
            Poll::Ready(Ok(()))
        }
        fn call(&mut self, req: http::Request<B>) -> Self::Future {
            let inner = self.inner.clone();
            match req.uri().path() {
                "/scheduler_controller.SchedulerController/KeepAlive" => {
                    #[allow(non_camel_case_types)]
                    struct KeepAliveSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::KeepAliveRequest> for KeepAliveSvc<T>
                    {
                        type Response = super::KeepAliveReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::KeepAliveRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { (*inner).keep_alive(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = KeepAliveSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/InputWorkflow" => {
                    #[allow(non_camel_case_types)]
                    struct InputWorkflowSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::InputWorkflowRequest>
                        for InputWorkflowSvc<T>
                    {
                        type Response = super::InputWorkflowReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::InputWorkflowRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { (*inner).input_workflow(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = InputWorkflowSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/InputWorkflowRaw" => {
                    #[allow(non_camel_case_types)]
                    struct InputWorkflowRawSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::InputWorkflowRequest>
                        for InputWorkflowRawSvc<T>
                    {
                        type Response = super::InputWorkflowReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::InputWorkflowRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { (*inner).input_workflow_raw(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = InputWorkflowRawSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/FetchWorkflowIDList" => {
                    #[allow(non_camel_case_types)]
                    struct FetchWorkflowIDListSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::WorkflowIdListRequest>
                        for FetchWorkflowIDListSvc<T>
                    {
                        type Response = super::WorkflowIdListReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::WorkflowIdListRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { (*inner).fetch_workflow_id_list(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = FetchWorkflowIDListSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/GetWorkflowByID" => {
                    #[allow(non_camel_case_types)]
                    struct GetWorkflowByIDSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::GetWorkflowByIdRequest>
                        for GetWorkflowByIDSvc<T>
                    {
                        type Response = super::GetWorkflowByIdReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::GetWorkflowByIdRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { (*inner).get_workflow_by_id(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = GetWorkflowByIDSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/RegisterResourceAllocator" => {
                    #[allow(non_camel_case_types)]
                    struct RegisterResourceAllocatorSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::RegisterRaRequest>
                        for RegisterResourceAllocatorSvc<T>
                    {
                        type Response = super::RegisterRaReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::RegisterRaRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut =
                                async move { (*inner).register_resource_allocator(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = RegisterResourceAllocatorSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/GetWorkflowPhaseByCustomID" => {
                    #[allow(non_camel_case_types)]
                    struct GetWorkflowPhaseByCustomIDSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::GetWorkflowPhaseByCustomIdRequest>
                        for GetWorkflowPhaseByCustomIDSvc<T>
                    {
                        type Response = super::GetWorkflowPhaseByCustomIdReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::GetWorkflowPhaseByCustomIdRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move {
                                (*inner).get_workflow_phase_by_custom_id(request).await
                            };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = GetWorkflowPhaseByCustomIDSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                "/scheduler_controller.SchedulerController/DeleteWorkflow" => {
                    #[allow(non_camel_case_types)]
                    struct DeleteWorkflowSvc<T: SchedulerController>(pub Arc<T>);
                    impl<T: SchedulerController>
                        tonic::server::UnaryService<super::DeleteWorkflowRequest>
                        for DeleteWorkflowSvc<T>
                    {
                        type Response = super::DeleteWorkflowReply;
                        type Future = BoxFuture<tonic::Response<Self::Response>, tonic::Status>;
                        fn call(
                            &mut self,
                            request: tonic::Request<super::DeleteWorkflowRequest>,
                        ) -> Self::Future {
                            let inner = self.0.clone();
                            let fut = async move { (*inner).delete_workflow(request).await };
                            Box::pin(fut)
                        }
                    }
                    let inner = self.inner.clone();
                    let fut = async move {
                        let interceptor = inner.1.clone();
                        let inner = inner.0;
                        let method = DeleteWorkflowSvc(inner);
                        let codec = tonic::codec::ProstCodec::default();
                        let mut grpc = if let Some(interceptor) = interceptor {
                            tonic::server::Grpc::with_interceptor(codec, interceptor)
                        } else {
                            tonic::server::Grpc::new(codec)
                        };
                        let res = grpc.unary(method, req).await;
                        Ok(res)
                    };
                    Box::pin(fut)
                }
                _ => Box::pin(async move {
                    Ok(http::Response::builder()
                        .status(200)
                        .header("grpc-status", "12")
                        .header("content-type", "application/grpc")
                        .body(tonic::body::BoxBody::empty())
                        .unwrap())
                }),
            }
        }
    }
    impl<T: SchedulerController> Clone for SchedulerControllerServer<T> {
        fn clone(&self) -> Self {
            let inner = self.inner.clone();
            Self { inner }
        }
    }
    impl<T: SchedulerController> Clone for _Inner<T> {
        fn clone(&self) -> Self {
            Self(self.0.clone(), self.1.clone())
        }
    }
    impl<T: std::fmt::Debug> std::fmt::Debug for _Inner<T> {
        fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
            write!(f, "{:?}", self.0)
        }
    }
    impl<T: SchedulerController> tonic::transport::NamedService for SchedulerControllerServer<T> {
        const NAME: &'static str = "scheduler_controller.SchedulerController";
    }
}
