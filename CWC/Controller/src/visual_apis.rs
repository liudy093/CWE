use super::config::Configure;
use super::grpc::scheduler::{scheduler_client::SchedulerClient, DeleteWorkflowRequest};
use super::grpc::workflow::Workflow;
use super::tracker::SchedulerRegisterTable;
use prost::{DecodeError, Message};
use redis::cluster::{ClusterClient, ClusterConnection};
use redis::{Commands, RedisError, RedisResult};
use std::{collections::HashMap, sync::Arc};
use std::{convert::TryFrom, str};
use tokio::sync::RwLock;

#[derive(Debug)]
pub struct VisualWorkflow {
    /// 调度器注册表的一个ARC引用（来自 tracker）
    scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,
    /// 全局配置
    global_conf: Arc<Configure>,
}

impl VisualWorkflow {
    pub fn new(
        scheduler_register_table: Arc<RwLock<SchedulerRegisterTable>>,
        global_conf: Arc<Configure>,
    ) -> VisualWorkflow {
        VisualWorkflow {
            scheduler_register_table,
            global_conf,
        }
    }

    ///读取调度器注册表的所有ID
    pub async fn fetch_workflow_id_list(&self) -> Result<Vec<String>, u32> {
        let mut id_list = Vec::new();
        let sch_register_table = self.scheduler_register_table.read().await;
        for sch_id in sch_register_table.keys() {
            if let Ok(mut wkflow_ids) = self.get_workflow_ids_by_sch_from_redis(sch_id) {
                id_list.append(&mut wkflow_ids);
            } else {
                return Err(1);
            }
        }
        Ok(id_list)
    }

    /// 根据工作流的custom_id，获取工作流id
    pub fn custom_id_to_workflow_id(&self, custom_id: &str) -> Result<String, String> {
        match self.get_redis_cluster_connection() {
            Ok(mut con) => match con.get::<&str, Option<String>>(custom_id) {
                Ok(Some(workflow_id)) => Ok(workflow_id),
                Ok(None) => Err("Can not find custom_id from redis".to_owned()),
                Err(e) => Err(e.to_string()),
            },
            Err(e) => Err(e.to_string()),
        }
    }

    /// 读取工作流DAG，并把工作流状态填入到dag结构中
    pub fn fetch_workflow_dag(&self, workflow_id: &str) -> Result<Workflow, u32> {
        if let Ok(wkflow_binary) = self.get_workflow_dag_binary_from_redis(workflow_id) {
            match Workflow::try_from(wkflow_binary) {
                Ok(mut wkflow) => {
                    if let Ok(states) = self.get_workflow_state_from_redis(workflow_id) {
                        for mut itr in wkflow.topology.iter_mut() {
                            if let Some(ph) = states.get(&itr.name) {
                                itr.phase = ph.to_owned();
                            }
                        }
                        Ok(wkflow)
                    } else {
                        Err(1) // 错误码1：未能从 redis 读出 state 数据
                    }
                }
                Err(err) => {
                    error!(
                        "[可视化API] 从从二进制数据转换到 Workflow 结构出错：{:?}",
                        err
                    );
                    Err(2) // 错误码2：未能从二进制数据转换出 Workflow 结构
                }
            }
        } else {
            Err(3) // 错误码3：未能从 redis 读出 dag binary 数据
        }
    }

    /// 主动删除工作流
    /// 返回被删除工作流id
    pub async fn delete_workflow(&self, workflow_id: &str, custom_id: &str) -> Result<String, u32> {
        // 工作流id表示为：sch_id-workflow_index
        let parts: Vec<&str> = workflow_id.split("-").collect();
        if parts.len() != 2 {
            error!(
                "[状态跟踪器] 从工作流id中拆分调度器id失败：提供的工作流id为：{}",
                workflow_id
            );
            return Err(1);
        }
        let sch_id = parts[0];
        let sch_register_table = self.scheduler_register_table.read().await;
        if let Some(sch) = sch_register_table.get(sch_id) {
            let sch_address = format!("http://{}", sch.ipv4);
            if let Ok(mut client) = SchedulerClient::connect(sch_address.clone()).await {
                let request = tonic::Request::new(DeleteWorkflowRequest {
                    workflow_id: workflow_id.to_owned(),
                    custom_id: custom_id.to_owned(),
                });
                match client.delete_workflow(request).await {
                    Ok(response) => {
                        if response.into_inner().result == 1 {
                            debug!(
                                "[状态跟踪器] 成功向调度器 [{}]({}) 发送删除工作流请求",
                                sch_id, sch_address
                            );

                            Ok(workflow_id.to_owned())
                        } else {
                            warn!(
                                "[状态跟踪器] 调度器 [{}]({}) 未能成功删除工作流(result != 1)！",
                                sch_id, sch_address
                            );
                            Err(2)
                        }
                    }
                    Err(err) => {
                        error!(
                            "[状态跟踪器] 调用调度器 [{}]({}) 删除工作流功能失败, 错误信息：{}！",
                            sch_id,
                            sch_address,
                            err.message()
                        );
                        Err(3)
                    }
                }
            } else {
                error!(
                    "[状态跟踪器] 调用调度器 [{}]({}) 删除工作流功能，但连接失败！",
                    sch_id, sch_address
                );
                Err(4)
            }
        } else {
            error!(
                "[状态跟踪器] 从工作流id中拆分调度器id失败：提供的工作流id为：{}",
                workflow_id
            );
            Err(5)
        }
    }

    pub fn get_workflow_phase_by_custom_id(&self, custom_id: &str) -> RedisResult<&'static str> {
        let mut con = self.get_redis_cluster_connection()?;
        match con.get::<&str, Option<String>>(custom_id) {
            Ok(v) => {
                if let Some(_) = v {
                    Ok("在调度器内执行")
                } else {
                    Ok("等待分配调度器")
                }
            }
            Err(e) => Err(e),
        }
    }

    /// 获取redis cluster连接
    fn get_redis_cluster_connection(&self) -> RedisResult<ClusterConnection> {
        let redis_client = ClusterClient::open(self.global_conf.redis_host.clone())?;
        redis_client.get_connection()
    }

    fn get_workflow_ids_by_sch_from_redis(&self, sch_id: &str) -> RedisResult<Vec<String>> {
        let mut con = self.get_redis_cluster_connection()?;
        let all_keys: Vec<String> = con.keys(format!("{}-*-A", sch_id))?;
        Ok(all_keys.iter().map(|val| val.replace("-A", "")).collect())
    }

    fn get_workflow_dag_binary_from_redis(&self, workflow_id: &str) -> RedisResult<Vec<u8>> {
        let mut con = self.get_redis_cluster_connection()?;
        let wkflow_binary: Vec<u8> = con.get(format!("{}-A", workflow_id))?;
        Ok(wkflow_binary)
    }

    fn get_workflow_state_from_redis(
        &self,
        workflow_id: &str,
    ) -> RedisResult<HashMap<String, String>> {
        let mut con = self.get_redis_cluster_connection()?;
        let wkflow_state: HashMap<String, String> = con.hgetall(format!("{}-B", workflow_id))?;
        Ok(wkflow_state)
    }
}

impl TryFrom<Vec<u8>> for Workflow {
    type Error = DecodeError;

    fn try_from(binary: Vec<u8>) -> Result<Self, Self::Error> {
        Self::decode(&binary[..])
    }
}
