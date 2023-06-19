package bit.workflowScheduler.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bit.workflowScheduler.grpc.Scheduler.ProtoWF;
import bit.workflowScheduler.grpc.TaskStatusTracker.TaskStatusTrackerClient;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest;
import bit.workflowScheduler.problem.ParseWorflowByProtoWF;
import bit.workflowScheduler.problem.Task;
import bit.workflowScheduler.problem.Workflow;
import bit.workflowScheduler.util.Parameters.TaskState;
import bit.workflowScheduler.util.Parameters.WorkflowState;
import bit.workflowScheduler.util.RedisOld1;

public class ParseWorkflowThread implements Runnable {

//	private Deque<ProtoWF> workflowFileDeque;
//	private HashMap<Long, Workflow> workflowList;
//	private HashMap<Long, ArrayList<String>> workflowStateTable;
//	
//	public ParseWorkflowThread(Deque<ProtoWF> workflowFileDeque,
//			HashMap<Long, Workflow> workflowList,
//			HashMap<Long, ArrayList<String>> workflowStateTable) {
//	     this.workflowFileDeque = workflowFileDeque;
//	     this.workflowList = workflowList;
//	     this.workflowStateTable = workflowStateTable;
//	}
	
	@Override
	public void run() {
		
		while(!Main.deadFlag) {
			
			HashMap<String, ProtoWF.Workflow> tempWorkflowFiles = new HashMap<String, ProtoWF.Workflow>();
			
			/**step A：从workflowFileDeque中读取*/
			/*需线程同步workflowFileDeque的代码写在这：
			 * 从workflowFileDeque中读*/
			synchronized(Main.workflowFileDeque) {
				Iterator<Map.Entry<String, ProtoWF.Workflow>> iter = Main.workflowFileDeque.entrySet().iterator();

				if(Main.workflowFileDeque.size() > Main.MAXParseWorkflowsNum) { //取前Main.MAXParseWorkflowsNum个工作流解析
					int i = 0;
					while(iter.hasNext()) {
						Map.Entry<String, ProtoWF.Workflow> entry = iter.next();
						tempWorkflowFiles.put(entry.getKey(), entry.getValue());
						iter.remove();
						i++;
						if(i >= Main.MAXParseWorkflowsNum)
							break;
					}	
				}
				else {
					while(iter.hasNext()) {
						Map.Entry<String, ProtoWF.Workflow> entry = iter.next();
						tempWorkflowFiles.put(entry.getKey(), entry.getValue());
						iter.remove();
					}
				}
			}
			
			if(!tempWorkflowFiles.isEmpty()) {
				for(Map.Entry<String, ProtoWF.Workflow> entry : tempWorkflowFiles.entrySet()) {
					ProtoWF.Workflow tempWorkflowFile = entry.getValue();
					
					/**step B：解析当前tempWorkflowFile*/
					Main.log.finest("开始解析工作流：" + tempWorkflowFile.getWorkflowName());
					Workflow tempWorkflow = new Workflow();
					ParseWorflowByProtoWF.parse(entry.getKey(), tempWorkflowFile, tempWorkflow);
					
					List<Task> tempWorkflowTaskList = tempWorkflow.getTaskList();
					/**step C.1：将解析的工作流注册到workflowStateTable，并同步到Redis数据库
					 * 注意增加已指派工作流状态的获取*/ 
//					List<String> tasksState = new ArrayList<String>();
					synchronized(Main.deleteWorkflows) { 
					if(tempWorkflow.getWorkflowState().equals(WorkflowState.NEWASSIGNED)) {
//						//从数据库中获得任务状态
//						tasksState = Redis.read(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), 1);
//						tasksState.set(Main.wfStateBit, WorkflowState.UNEXECUTE.toString()); //工作流状态
						
						//从数据库中获得任务状态
//						String workflowState = Main.redis.read(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), "workflowState", 1);
						String workflowState = Main.redis.read(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), "workflowState", 1);
						if(workflowState.equals("UNEXECUTE")) { //此时没有任务状态
							//初始化Redis中该工作流的任务状态
							Map<String, String> tasksName2State = new HashMap<String, String>();
							for(Task t : tempWorkflowTaskList) { //排除虚拟出口任务
								if(t.getTaskName().equals("exit"))
									continue;
								else {
									tasksName2State.put(t.getTaskName(), TaskState.UNEXECUTE.toString());
								}
							}
							
//							Main.redis.write(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), tasksName2State, 1);
							Main.redis.write(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), tasksName2State, 1);
						}
						else { 
							//更新任务状态
							for(Task t : tempWorkflowTaskList) { //排除虚拟出口任务
								if(t.getTaskName().equals("exit"))
									continue;
								else {
//									t.setTaskState(TaskState.valueOf(Main.redis.read(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), t.getTaskName(), 1)));
									t.setTaskState(TaskState.valueOf(Main.redis.read(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), t.getTaskName(), 1)));
								}
							}
						}
					}
					else {
//						//初始化size
//						int size = tempWorkflow.getTasksNum()-1+Main.offsetTaskState; //不存放虚拟出口任务的状态,故-1
//						for(int i = 0; i < size; i++) 
//							tasksState.add("");
//						
//						//均初始化为UNEXECUTE
//						tasksState.set(Main.wfStateBit, WorkflowState.UNEXECUTE.toString()); //工作流状态
//						List<Task> taskList = tempWorkflow.getTaskList();
//						for(int i = 0; i < tempWorkflow.getTasksNum()-1; i++) { //排除虚拟出口任务
//							Task task = taskList.get(i);
//							tasksState.set(Main.offsetTaskState+task.getTaskId(), TaskState.UNEXECUTE.toString());
//						}
						
						//初始化Redis中该工作流的任务状态
						Map<String, String> tasksName2State = new HashMap<String, String>();
						for(Task t : tempWorkflowTaskList) { //排除虚拟出口任务
							if(t.getTaskName().equals("exit"))
								continue;
							else {
								tasksName2State.put(t.getTaskName(), TaskState.UNEXECUTE.toString());
							}
						}
						
//						Main.redis.write(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), tasksName2State, 1);	
						Main.redis.write(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), tasksName2State, 1);
					}
					}
//					/*需线程同步workflowStateTable的代码写在这：
//					 * 向workflowStateTable中写入*/
//					synchronized(Main.workflowStateTable) {
//						Main.workflowStateTable.put(tempWorkflow.getWorkflowId(), tasksState);
//					}
//					Redis.write(Main.getWorkflowNameB(tempWorkflow.getWorkflowId()), Main.workflowStateTable.get(tempWorkflow.getWorkflowId()), 1);		
					
					/**step C.2：将解析的工作流注册到任务状态跟踪器*/ //本地测试时，任务注册在Main中; 正常运行时，使用该任务注册
			    	List<TaskStateRequest.NodesState> nodesStateList = new ArrayList<TaskStateRequest.NodesState>();
			    	Task t = null;
			    	for(int i = 0; i < tempWorkflow.getTasksNum()-1; i++) { //排除虚拟出口任务
			    		t = tempWorkflowTaskList.get(i);
			    		nodesStateList.add(TaskStateRequest.NodesState.newBuilder()
			    				.setTaskID(t.getTaskId()).setTaskPodName(t.getPodName()).build());
			    	}
					int status = 0;
//					while(status != 1) {
//						status = TaskStatusTrackerClient.registerTasksToTST("workflow"+tempWorkflow.getWorkflowId(), 
//								Main.IP, nodesStateList);
//					}
					int tryTimes = 0;
					Main.log.finest("向状态跟踪器注册批量任务");
		    		while(tryTimes < 5) {
		    			status = TaskStatusTrackerClient.registerTasksToTST("workflow"+tempWorkflow.getWorkflowId(), 
								Main.IP, nodesStateList);
		    			if(status >= 1) {
		    				Main.log.finest("向状态跟踪器注册批量任务成功");
							break;
		    			}
						tryTimes++;
						try {
							Thread.currentThread().sleep(1000*tryTimes);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//毫秒
		    		}
		    		if(status < 1) { 
						Main.log.warning("向状态跟踪器注册批量任务失败，重试后还是失败，正常退出");
						System.exit(0);
					}
					
					
					/**step C.3：计算解析的工作流中所有任务的资源需求和，并累加到*/
					Main.allTasksResReq.add(Main.calResourceRequests(tempWorkflow.getTaskList()));
					
					/**step C.4：将解析的工作流写入workflowList, 
					 * 若有已指派任务，得到任务的状态再放入workflowList，否则Main中的一些操作受影响，比如有可能操作的是未获得已有状态的任务*/
					/*需线程同步workflowList的代码写在这：
					 * 向workflowList中写入*/
					synchronized(Main.workflowList) {
						Main.workflowList.put(tempWorkflow.getWorkflowId(), tempWorkflow);
						Main.NumParseWorkflows++;
					}
					
//					Main.log.info("当前已经解析的工作流的数量：" + Main.NumParseWorkflows);
				}
			}
		}
		
	}//end run
}
