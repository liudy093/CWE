/// The request message.
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct PodsPowerPredictRequest {
    #[prost(string, tag = "1")]
    pub pod_id: ::prost::alloc::string::String,
    #[prost(uint64, tag = "2")]
    pub target_timestamp: u64,
}
/// The response message.
#[derive(Clone, PartialEq, ::prost::Message)]
pub struct PodsPowerPredictReply {
    #[prost(int32, tag = "1")]
    pub power_level: i32,
}
#[doc = r" Generated client implementations."]
pub mod pods_power_predict_service_client {
    #![allow(unused_variables, dead_code, missing_docs)]
    use tonic::codegen::*;
    pub struct PodsPowerPredictServiceClient<T> {
        inner: tonic::client::Grpc<T>,
    }
    impl PodsPowerPredictServiceClient<tonic::transport::Channel> {
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
    impl<T> PodsPowerPredictServiceClient<T>
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
        #[doc = " Sends a request"]
        pub async fn pods_power_predict(
            &mut self,
            request: impl tonic::IntoRequest<super::PodsPowerPredictRequest>,
        ) -> Result<tonic::Response<super::PodsPowerPredictReply>, tonic::Status> {
            self.inner.ready().await.map_err(|e| {
                tonic::Status::new(
                    tonic::Code::Unknown,
                    format!("Service was not ready: {}", e.into()),
                )
            })?;
            let codec = tonic::codec::ProstCodec::default();
            let path = http::uri::PathAndQuery::from_static(
                "/PodsPowerPredict.PodsPowerPredictService/PodsPowerPredict",
            );
            self.inner.unary(request.into_request(), path, codec).await
        }
    }
    impl<T: Clone> Clone for PodsPowerPredictServiceClient<T> {
        fn clone(&self) -> Self {
            Self {
                inner: self.inner.clone(),
            }
        }
    }
    impl<T> std::fmt::Debug for PodsPowerPredictServiceClient<T> {
        fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
            write!(f, "PodsPowerPredictServiceClient {{ ... }}")
        }
    }
}
