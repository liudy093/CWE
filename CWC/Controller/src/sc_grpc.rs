use std::sync::Arc;

use crate::grpc::scheduler_controller::{DeleteWorkflowReply, DeleteWorkflowRequest};

use super::grpc::scheduler_controller::{
    scheduler_controller_server::SchedulerController, GetWorkflowByIdReply, GetWorkflowByIdRequest,
    GetWorkflowPhaseByCustomIdReply, GetWorkflowPhaseByCustomIdRequest, InputWorkflowReply,
    InputWorkflowRequest, KeepAliveReply, KeepAliveRequest, RegisterRaReply, RegisterRaRequest,
    WorkflowIdListReply, WorkflowIdListRequest,
};
use super::keep_alive::KeepAliveServer;
use super::metric_endpoint::PrometheusRegistry;
use super::ra_manager::RAManager;
use super::utils::encode_workflows_use_snappy;
use super::visual_apis::VisualWorkflow;
use flume::Sender;
use prometheus::{IntCounter, IntGauge, Opts, Registry};
use tonic::{Code, Request, Response, Status};

pub type WorkflowBinaryArray = Vec<u8>;

#[derive(Debug)]
pub struct SchedulerControllerGRPC {
    keep_alive_server: KeepAliveServer,

    /// 工作流队列 mpsc 发送端（接收端在 scheduler_distributor）
    sender: Sender<WorkflowBinaryArray>,

    /// 工作流队列内多少工作流待发送（缓存工作流数量），来自scheduler_distributor模块，本模块内：只写（增加）
    wf_count_in_queue: IntGauge,

    /// 接收工作流计数
    /// Prometheus 指标：接收到的工作流计数
    received_workflow_counter: IntCounter,

    visual_workflow: VisualWorkflow,

    ra_manager: Arc<RAManager>,
}

impl SchedulerControllerGRPC {
    pub fn new(
        keep_alive_server: KeepAliveServer,
        sender: Sender<WorkflowBinaryArray>,
        wf_count_in_queue: IntGauge,
        visual_workflow: VisualWorkflow,
        ra_manager: Arc<RAManager>,
    ) -> SchedulerControllerGRPC {
        SchedulerControllerGRPC {
            keep_alive_server,
            sender: sender,
            wf_count_in_queue,

            received_workflow_counter: IntCounter::with_opts(Opts::new(
                "received_workflow_counter",
                "received workflow number",
            ))
            .unwrap(),
            visual_workflow,
            ra_manager,
        }
    }
}

#[tonic::async_trait]
impl SchedulerController for SchedulerControllerGRPC {
    async fn keep_alive(
        &self,
        request: Request<KeepAliveRequest>,
    ) -> Result<Response<KeepAliveReply>, Status> {
        let wait_secs = self
            .keep_alive_server
            .process_keep_alive_signal(request.into_inner())
            .await;

        let reply = KeepAliveReply { wait_secs };

        Ok(Response::new(reply))
    }

    async fn input_workflow(
        &self,
        request: Request<InputWorkflowRequest>,
    ) -> Result<Response<InputWorkflowReply>, Status> {
        let workflow_list = request.into_inner();

        let wf_count = workflow_list.workflow.len() as u64;
        self.received_workflow_counter.inc_by(wf_count);

        self.wf_count_in_queue.add(wf_count as i64);

        debug!("[接收模块] 接收 {} 个压缩工作流", wf_count);

        for workflow in workflow_list.workflow.into_iter() {
            self.sender.send(workflow).unwrap();
        }

        let reply = InputWorkflowReply { accept: 1u32 };
        Ok(Response::new(reply))
    }

    async fn input_workflow_raw(
        &self,
        request: Request<InputWorkflowRequest>,
    ) -> Result<Response<InputWorkflowReply>, Status> {
        let workflow_list = request.into_inner();

        let wf_count = workflow_list.workflow.len() as u64;
        self.received_workflow_counter.inc_by(wf_count);

        self.wf_count_in_queue.add(wf_count as i64);

        debug!("[接收模块] 接收 {} 个未压缩工作流", wf_count);
        let mut workflow_list: Vec<Vec<u8>> = workflow_list.workflow.into();
        if let Ok(workflow_list) = encode_workflows_use_snappy(&mut workflow_list) {
            for workflow in workflow_list {
                self.sender.send(workflow).unwrap();
            }
            let reply = InputWorkflowReply { accept: 1u32 };
            Ok(Response::new(reply))
        } else {
            Err(Status::new(Code::Internal, format!("压缩工作流失败")))
        }
    }

    async fn fetch_workflow_id_list(
        &self,
        _: Request<WorkflowIdListRequest>,
    ) -> Result<Response<WorkflowIdListReply>, Status> {
        match self.visual_workflow.fetch_workflow_id_list().await {
            Ok(ids) => Ok(Response::new(WorkflowIdListReply { ids })),
            Err(err_no) => Err(Status::new(
                Code::Internal,
                format!("调度器控制器获取工作流id列表出错，错误码为：{}", err_no),
            )),
        }
    }

    async fn get_workflow_by_id(
        &self,
        request: Request<GetWorkflowByIdRequest>,
    ) -> Result<Response<GetWorkflowByIdReply>, Status> {
        let req = request.into_inner();

        if req.custom_id.is_empty() {
            error!("外部调用服务器gRPC失败：custom_id不能为空字符串");
            return Err(Status::new(
                Code::InvalidArgument,
                "custom_id不能为空字符串",
            ));
        }

        let mut workflow_id = req.workflow_id;
        if workflow_id.is_empty() {
            match self
                .visual_workflow
                .custom_id_to_workflow_id(&req.custom_id)
            {
                Ok(tmp_wf_id) => workflow_id = tmp_wf_id,
                Err(_) => {
                    error!(
                        "custom_id({}) 向 workflow_id({}) 转换失败",
                        req.custom_id, workflow_id
                    );
                    return Err(Status::new(
                        Code::Internal,
                        "custom_id 向 workflow_id 转换失败",
                    ));
                }
            }
        }

        if workflow_id.is_empty() {
            return Err(Status::new(
                Code::Internal,
                "未在redis内发现 custom_id 对应的 workflow_id",
            ));
        }

        match self.visual_workflow.fetch_workflow_dag(&workflow_id) {
            Ok(workflow_dag) => Ok(Response::new(GetWorkflowByIdReply {
                workflow_dag: Some(workflow_dag),
            })),
            Err(err_no) => Err(Status::new(
                Code::Internal,
                format!("调度器控制器获取工作流dag出错，错误码为：{}", err_no),
            )),
        }
    }

    async fn register_resource_allocator(
        &self,
        request: Request<RegisterRaRequest>,
    ) -> Result<Response<RegisterRaReply>, Status> {
        let req = request.into_inner();
        let boot_id = self
            .ra_manager
            .register_resource_allocator(&req.cluster_id, &req.ipv4)
            .await;
        Ok(Response::new(RegisterRaReply { boot_id }))
    }

    async fn get_workflow_phase_by_custom_id(
        &self,
        request: Request<GetWorkflowPhaseByCustomIdRequest>,
    ) -> Result<Response<GetWorkflowPhaseByCustomIdReply>, Status> {
        let req = request.into_inner();

        match self
            .visual_workflow
            .get_workflow_phase_by_custom_id(&req.custom_id)
        {
            Ok(r) => Ok(Response::new(GetWorkflowPhaseByCustomIdReply {
                phase: r.to_owned(),
            })),
            Err(e) => Err(Status::new(Code::Internal, format!("redis错误: {:?}", e))),
        }
    }

    async fn delete_workflow(
        &self,
        request: Request<DeleteWorkflowRequest>,
    ) -> Result<Response<DeleteWorkflowReply>, Status> {
        let req = request.into_inner();

        if req.custom_id.is_empty() {
            error!("外部调用服务器gRPC失败：custom_id不能为空字符串");
            return Err(Status::new(
                Code::InvalidArgument,
                "custom_id不能为空字符串",
            ));
        }

        let mut workflow_id = req.workflow_id;
        if workflow_id.is_empty() {
            match self
                .visual_workflow
                .custom_id_to_workflow_id(&req.custom_id)
            {
                Ok(tmp_wf_id) => workflow_id = tmp_wf_id,
                Err(e) => {
                    error!(
                        "custom_id({}) 向 workflow_id({}) 转换失败，RedisError: {}",
                        req.custom_id, workflow_id, e
                    );
                    return Err(Status::new(
                        Code::Internal,
                        "custom_id 向 workflow_id 转换失败",
                    ));
                }
            }
        }

        match self
            .visual_workflow
            .delete_workflow(&workflow_id, &req.custom_id)
            .await
        {
            Ok(wf_id) => Ok(Response::new(DeleteWorkflowReply { workflow_id: wf_id })),
            Err(err_code) => Err(Status::new(
                Code::Internal,
                format!("删除工作流错误, 错误码: {}", err_code),
            )),
        }
    }
}

impl PrometheusRegistry for SchedulerControllerGRPC {
    fn register_metric(&self, r: &Registry) {
        r.register(Box::new(self.received_workflow_counter.clone()))
            .unwrap();
    }
}
