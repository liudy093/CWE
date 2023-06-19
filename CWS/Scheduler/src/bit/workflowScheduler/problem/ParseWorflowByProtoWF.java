package bit.workflowScheduler.problem;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import java.util.List;

import bit.workflowScheduler.grpc.Scheduler.ProtoWF;
import bit.workflowScheduler.main.Main;
import bit.workflowScheduler.util.DistrIdGenerator2;
import bit.workflowScheduler.util.Parameters.WorkflowState;
import bit.workflowScheduler.util.RedisOld1;


/**
 * 解析ProtoWF.Workflow，得到各用户提交的workflows
 * 每个ProtoWF.Workflow对于一个workflow
 * @author YangLiwen
 * @version date：2020年7月30日  下午9:13:26
 *
 */

public class ParseWorflowByProtoWF {

	/**
	 * 解析工作流ProtoWF.Workflow
	 * @param file 这是在DAG缓存队列中的工作流
	 * @param workflow 解析后的工作流
	 */
	public static void parse(String workflowId, ProtoWF.Workflow file, Workflow workflow) {
    
		Map<String, Task> mName2Task = new HashMap<>(); // taskName与task的map
		Map<String, ProtocolStringList> mName2Deps = new HashMap<>(); // taskName与该task父任务的map
        
        //判断工作流是否被指派过（若为ID，则没有被指派过；否则，指派过）
		/*新解析的工作流, 若没有被指派过，则其状态都标记为WorkflowState.NEWUNASSIGNED，
         * 若曾经被指派过，则其状态都标记为WorkflowState.NEWASSIGNED
         * 用于更新readyTaskList以及获取已指派任务的状态
         */
		workflow.setWorkflowId(workflowId);
        if(file.getWorkflowName().equals("NoName"))  
        	//设置工作流状态
        	workflow.setWorkflowState(WorkflowState.NEWUNASSIGNED);
        else
        	workflow.setWorkflowState(WorkflowState.NEWASSIGNED);
        
//        workflow.setWorkflowName("workflow" + workflow.getWorkflowId()); 	
        workflow.setWorkflowStyle(file.getStyle());
        workflow.customization = file.getCustomization();
        workflow.time_grade = file.getTimeGrade();
        workflow.cost_grade = file.getCostGrade();
        
        int taskNum = file.getTopologyCount();
        for (int i = 0 ; i < taskNum; i++){
            ProtoWF.WorkflowNode node = file.getTopology(i);
            Task task = new Task(); //一个node对应一个task
            Resource taskRes = new Resource(); //任务的资源需求
            
            task.setWorkflowId(workflowId);
            task.setTaskId(i);
            task.setTaskName(node.getName());
            mName2Task.put(task.getTaskName(), task);
            mName2Deps.put(task.getTaskName(), node.getDependenciesList());
            task.setImage(node.getTemplate());
            task.setTaskPhase(node.getPhase());
            task.setTaskNodeInfo(node.getNodeInfo());
            taskRes.setCPU(node.getCpu()*1000);
            taskRes.setMemory((long)Math.ceil(Math.abs(node.getMem())/(1024.0*1024)));
            task.setRes(taskRes);
            task.setEnv(node.getEnvMap());
            task.setInFileList(node.getInputVectorList());
            task.setOutFileList(node.getOutputVectorList());
            
            workflow.addTaskList(task);
//            Main.log.info("收到的任务的image：" + node.getTemplate());
            task.setWorkflowStyle(workflow.getWorkflowStyle());
            task.customization=workflow.customization;
            if(workflow.time_grade.isEmpty() || workflow.time_grade.equals("C") || workflow.time_grade.equals("D"))
            	task.time_grade=false;
            else
            	task.time_grade=true;
            if(workflow.cost_grade.isEmpty() || workflow.cost_grade.equals("C") || workflow.cost_grade.equals("D"))
            	task.cost_grade=false;
            else
            	task.cost_grade=true;
        }
        
        //解析任务中的依赖关系
        for(Map.Entry<String, ProtocolStringList> entry : mName2Deps.entrySet()) {
        	Task child = mName2Task.get(entry.getKey());
        	ProtocolStringList dependencies = entry.getValue();
        	for (int i = 0 ; i < dependencies.size();i++){
        		Task parent = mName2Task.get(dependencies.get(i).toString());
        		child.addParent(parent);
        		parent.addChild(child);
        	}
        }
        
        //增加出口任务，使出口任务唯一，用于判断工作流什么时候执行完
        Task exitT = new Task();
        Resource taskRes = new Resource();
        exitT.setWorkflowId(workflowId);
        exitT.setTaskId(workflow.getTasksNum());
        exitT.setTaskName("exit");
        taskRes.setCPU(0);
        taskRes.setMemory(0);
        exitT.setRes(taskRes);
        exitT.setWorkflowStyle(workflow.getWorkflowStyle());
        workflow.addTaskList(exitT);
        List<Task> taskList = workflow.getTaskList();
        for(int i = 0; i < workflow.getTasksNum()-1; i++) {
        	Task t = taskList.get(i);
        	if(t.getChildList().isEmpty()) {
        		t.addChild(exitT);
        		exitT.addParent(t);
        	}
        }
        
        //for DG
        if(workflow.getWorkflowStyle().equals("DG")) {
	        for(Task t : exitT.getParentList())
	        	t.setTaskName("trueExit");
        }
        
        if(Main.CeShi) {
	        //测试解析工作流是否正确
	        boolean trueF = true;
	        for(ProtoWF.WorkflowNode node : file.getTopologyList()) {
	        	Task t = mName2Task.get(node.getName());
	        	if(node.getDependenciesCount() != t.getParentList().size())
	        		trueF = false;
	        	for(Task p : t.getParentList()) {
	        		if (!node.getDependenciesList().contains(p.getTaskName())) {
	        			trueF = false;
	        		}
	        	}
	        }
//	        Main.log.finest("工作流的ID" + workflow.getWorkflowId());
	        if(trueF) {
	        	Main.numParseSucc++;
	        	Main.log.finest("解析工作流正确");
	        }
	        else {
	        	Main.numParseFailed++;
	        	Main.log.finest("解析工作流不正确");
	        }
	        Main.monitor.schedulerParseEfficiencySet("parseSuccessfulRate", Main.numParseSucc*1.0/(Main.numParseSucc+Main.numParseFailed)*100);
	        Main.log.finest("工作流解析成功率：" + Main.numParseSucc*1.0/(Main.numParseSucc+Main.numParseFailed)*100 + "%");
        
	        Main.log.info("工作流ID："+ workflow.getWorkflowId());
	        for(ProtoWF.WorkflowNode node : file.getTopologyList()) {
	        	Task t = mName2Task.get(node.getName());
	        	Main.log.info(t.getTaskId() + "," +node.getName() + ": " + node.getDependenciesList().toString());
	        }
        }
    }
	
}
