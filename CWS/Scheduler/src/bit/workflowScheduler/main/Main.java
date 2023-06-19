package bit.workflowScheduler.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.management.OperatingSystemMXBean;

import bit.workflowScheduler.grpc.Scheduler.ProtoWF;
import bit.workflowScheduler.grpc.TaskStatusTracker.TaskStatusTrackerClient;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest;
import bit.workflowScheduler.grpc.resource_allocator.ResourceRequestServiceClient;
import bit.workflowScheduler.inerface.CreatTaskPodReceived;
import bit.workflowScheduler.inerface.ResourceRequestReceived;
import bit.workflowScheduler.inerface.TaskStateReceived;
import bit.workflowScheduler.problem.Resource;
import bit.workflowScheduler.problem.Task;
import bit.workflowScheduler.problem.Workflow;
import bit.workflowScheduler.scheduleModel.Method1;
import bit.workflowScheduler.util.DistrIdGenerator2;
import bit.workflowScheduler.util.LogUtil;
import bit.workflowScheduler.util.Monitoring;
import bit.workflowScheduler.util.Parameters.TaskState;
import bit.workflowScheduler.util.Parameters.WorkflowState;
import redis.clients.jedis.JedisCluster;
import bit.workflowScheduler.util.RedisTest;
import bit.workflowScheduler.util.WorkflowTime;
import bit.workflowScheduler.util.WrappingScheduledExecutor;
import io.prometheus.client.exporter.PushGateway;


/**
 * 主线程ScheduleWorkflowThread
 * @author YangLiwen
 * @version date：2020年6月7日  下午6:35:53
 *
 */
public class Main {
	public static boolean CeShi = true;
	
	/**1、让redis中写入时，key为schedulerId-workflowId-A\B
	 * 2、让资源分配模块与任务状态模块发送时，workflow(前缀)workflowId以及task-taskID-workflowId-该任务失败次数
	 */
	//接收到的工作流的文件队列，需线程同步（接收工作流文件线程写，解析工作流线程读）
//	public static Deque<ProtoWF.Workflow> workflowFileDeque = new ArrayDeque<ProtoWF.Workflow>();
	public static HashMap<String, ProtoWF.Workflow> workflowFileDeque = new HashMap<String, ProtoWF.Workflow>();	
	//将要返回给调度器控制器的工作流
	//解析后的工作流列表，key为workflowId 需线程同步（解析工作流线程写，主线程读和写-更改执行状态）
	public static HashMap<String, Workflow> workflowList = new HashMap<String, Workflow>(); //存放所有工作流：定制和非定制
//	public static HashMap<String, Workflow> workflowListCustom = new HashMap<String, Workflow>(); //存放定制工作流
	public static List<String> deleteWorkflows = new ArrayList<String>(); //存放控制器需要删除的工作流的ID
//	//工作流任务状态表<key,value>=<workflowId,Task状态>，需线程同步（解析工作流线程写，主线程读-写入数据库、写-更改执行状态）
//	//value依次存：工作流状态 任务1状态 任务2状态 ... 
//	//向redis写入时，注意key为schedulerId-workflowId-A\B
//	public static HashMap<String, List<String>> workflowStateTable = new HashMap<String, List<String>>();	
	//不需要工作流状态表了，得到状态直接写入Redis即可。之前使用表，是为了保留某个工作流之前任务的状态，覆盖写入Redis，现在可以直接写入Redis某一位
	//现在RedisB库中存储的是：schedulerId-workflowId-A\B 工作流状态 value 任务1name value 任务2name value ... 
	//向redis写入时，注意key为schedulerId-workflowId-A\B
	
	//接收到的任务的状态队列，需线程同步（接收任务状态线程写，主线程读）
	public static Deque<TaskStateReceived> taskStateDeque = new ArrayDeque<TaskStateReceived>();
	
	public static ArrayList<Task> readyTaskList = new ArrayList<Task>(); //任务池，放置所有就绪任务
//	public static ArrayList<Task> readyTaskListCustom = new ArrayList<Task>(); //任务池，放置定制工作流的就绪任务
	public static int offsetTaskState = 1; //任务状态在工作流状态表workflowStateTable中的index与任务ID的偏移,偏移的存放其他信息
	public static int wfStateBit = 0; //工作流状态在工作流状态表workflowStateTable中的index
	public static Timer timer = new Timer(true); //保活信号定时器, ture主线程结束，定时器线程也结束
	public static volatile boolean grpcServerStartUpFlag = false;
//	public static ScheduledFuture newTimer = null;
	public static String schedulerId;
	public static String IP; public static String NodeIP; public static String port; public static String clusterId;
	public static Resource allTasksResReq = new Resource(); //当前调度器中所有未执行task所需的资源和
	public static Logger log = null;  //log日志对象
	public static volatile boolean deadFlag = false; //调度器自然死亡标志
//	public static volatile boolean exceptionDeadFlag = false; //调度器异常死亡标志
	public static long keepAliveSerialNumber = 1; 
	public static int MAXParseWorkflowsNum = 100; //一次解析的最大工作流数
	public static Monitoring monitor = new Monitoring(); //prometheus
	public static PushGateway pushgateway = null;
	private static int TaskMaxFailNum = 5; //一个任务允许的最大失败次数，达到最高次数，认为任务彻底执行失败，不再执行
	
//	public static RedisOld1 redis = new RedisOld1();
//	public static RedisTest redis = new RedisTest();
	public static RedisTest redis;
	public static String Redis0Ip;
	public static int Redis0Port; 
	public static String Redis1Ip;
	public static int Redis1Port;
	public static String Redis2Ip;
	public static int Redis2Port;

	public static String PromethusIp;
	public static int PromethusPort;
	
	//测试
	public static String printInf = ""; //用于线程同步
	public static long NumReceivedWorkflows = 0; //收到的工作流的数量 = 执行成功的工作流 + 执行失败的 + 还未有结果的 + 控制器要求删除的
												//收到的工作流的数量 = 新未指派工作流 + 新指派工作流
	public static long NumSuccessfulWorkflows = 0; //执行成功的工作流的数量
	public static long NumFailedWorkflows = 0; //执行失败的工作流的数量
	public static long NumParseWorkflows = 0; //解析的工作流的数量
	public static long NumNUAWR = 0; //新未指派工作流更新readyTaskList的次数，即新未指派工作流的个数
	public static long NumNAWR = 0; //新已指派工作流更新readyTaskList的次数，即新已指派工作流的个数
	
	public static long NumReceivedTasks = 0; //收到的任务的数量 = 执行成功的任务 + 执行失败的 + 还未有结果的 
	public static long NumSuccessfulTasks = 0; //执行成功的任务的数量
	public static long NumFailedTasks = 0; //执行失败的任务的数量，真的失败才自加一次
	public static long NumRequestCreatedPodTasks = 0; //已创建pod的任务数量，一个任务可能创建几次pod
	public static long NumCreatedPodTasks = 0; //已创建pod的任务数量，一个任务可能创建几次pod
	
	//发送给Tracker的任务数量，包括开始注册和之后任务失败再注册的任务，Tracker向调度器返回任务状态后，就删除该任务，
	//故调度器端收到的任务状态数量应该等于向Tracker端注册的任务数量, 即NumSendTrackerTasks=NumReceivedStateTasks
	public static long NumSendTrackerTasks = 0; //发送给Tracker的任务数量，包括开始注册和之后任务失败再注册的任务
	public static long NumReceivedStateTasks = 0; //从Tracker收到任务状态的任务数量
	public static long NumProcessReceivedStateTasks = 0; //从Tracker收到的任务状态中，处理的个数（加入tempTaskStateList的个数）
	
	public static long NumTasks = 0; //系统中目前存在的任务数量
	public static volatile int capacity = 0; //系统中目前还能容纳的任务数量
	public static volatile long maxWorkflowNum = 0; //系统中能容纳的最大工作流数量
	public static volatile int upperBoundWorkflowPerReceived = Integer.MAX_VALUE; //每次从控制器那接受的工作流上限
	
	
	//获取其他模块grpc服务的server环境变量
	public static String grpcSCServer;
	public static int grpcSCPort; //调度器控制器的
//	public static String grpcRQServer;
//	public static String grpcRQPort; //资源请求的
//	public static String grpcTSTServer;
//	public static String grpcTSTPort; //任务状态跟踪器的
	
	public static String volumePath;
	
	public static String workflowStateField = "workflowState";
	
	public static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	
//	public static int hash = 0;
//	public static BufferedWriter hashs = null;
//	public static BufferedReader hashsRead = null;
//	public static HashMap<String, Integer> fileHash2Id = new HashMap<String, Integer>();
//	public static HashMap<ProtoWF.Workflow, String> workflow2FileHash = new HashMap<ProtoWF.Workflow, String>();

//	public static String redisIP = "192.168.1.24";
//	public static int redisPort = 7134;
	
	//存放当前获得的任务状态
	public static ArrayList<TaskStateReceived> tempTaskStateList = new ArrayList<TaskStateReceived>();
	public static double resourcesURate = 0.0;
	public static long numParseSucc = 0;
	public static long numParseFailed = 0;
	
	//为了定制化测试计算cost
	public static HashMap<String, WorkflowTime> customizationWorkflowRuntimes = new HashMap<String, WorkflowTime>();
	
	public static void main(String[] args) throws InterruptedException, IOException, OutOfMemoryError  {
		long startTime = new Date().getTime(); //ms
		
		//打印jvm参数
		runtimeParameters();
		
//		redis.write("cluster", "helloworld1", 4);
//        System.out.println(redis.read("cluster",4));
		
		//测试
//		hashsRead = new BufferedReader(new FileReader(".\\hash.txt"));
//		String fileHash = "";
//		int hashId = 0;
//		while((fileHash = hashsRead.readLine()) != null) {
//			fileHash2Id.put(fileHash, Integer.valueOf(hashId));
//			hashId++;
//		}
		
		/*生成schedulerId，获取ip，获取其他模块的grpcServer、VOLUME_PATH*/
		Main.schedulerId = DistrIdGenerator2.createDistrId();
//		Main.schedulerId = "2008131628272740010001"; //测试时固定
		Main.IP = Inet4Address.getLocalHost().getHostAddress(); //获取ip4
		Main.NodeIP = System.getenv("NODE_IP"); //调度器pod所在node的IP 
		Main.port = System.getenv("NODE_PORT");
		Main.clusterId = System.getenv("CLUSTER_ID");
		grpcSCServer = System.getenv("CONTROL_HOST");
//		System.out.println(grpcSCServer+"调度器控制器的port："+ System.getenv("CONTROL_PORT"));
		grpcSCPort = Integer.parseInt(System.getenv("CONTROL_PORT"));
//		grpcRQServer = System.getenv("GRPC_RESOURCE_ALLOCATOR_SERVICE_HOST");
//		grpcRQPort = System.getenv("GRPC_RESOURCE_ALLOCATOR_SERVICE_PORT");
//		grpcTSTServer = System.getenv("GRPC_TASK_STATUS_TRACKER_SERVICE_HOST");
//		grpcTSTPort = System.getenv("GRPC_TASK_STATUS_TRACKER_SERVICE_PORT");
		volumePath = System.getenv("SHARE_PATH"); // /nfs/data
		Main.Redis0Ip = System.getenv("REDIS0_IP");
		Main.Redis0Port = Integer.parseInt(System.getenv("REDIS0_PORT"));
		Main.Redis1Ip = System.getenv("REDIS1_IP");
		Main.Redis1Port = Integer.parseInt(System.getenv("REDIS1_PORT"));
		Main.Redis2Ip = System.getenv("REDIS2_IP");
		Main.Redis2Port = Integer.parseInt(System.getenv("REDIS2_PORT"));
//		PromethusIp = System.getenv("PROMETHEUS_IP");
//		PromethusPort = Integer.parseInt(System.getenv("PROMETHEUS_PORT"));
		
		if(CeShi) //测试的日志用最低级别finest
			log = LogUtil.setLoggerHanlder(Level.ALL, schedulerId);
		else
			log = LogUtil.setLoggerHanlder(Level.INFO, schedulerId);
		
		//测执行时间
//		CeShi = false;
//		log = LogUtil.setLoggerHanlder(Level.OFF, schedulerId);
		
//		Main.log.info("日志存储地址VOLUME_PATH：" + volumePath);
//		log = LogUtil.setLoggerHanlder(Level.WARNING, schedulerId);
//		log = LogUtil.setLoggerHanlder(Level.SEVERE, schedulerId);
		log.info("这是一次新的执行！！！");
		
		redis = new RedisTest();
		
//		log.info("heapMemoryUsage: {}"+KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().toString()
//				+"\r\n"+"nonHeapMemoryUsage: {}"+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().toString());
//		log.info("heapMemoryUsage: "+KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024
//				+"\r\n"+"nonHeapMemoryUsage: "+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getUsed()/1024/1024
//				+"\r\n"+"堆内存利用率"+ KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024*1.0/2457
//				+"\r\n"+"内存利用率" + (KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getUsed())/1024/1024*1.0/3072*100
//				);
		
		//			monitor.setHttpPort(8080);
		//创建PushGateway对象，推送数据至ip:port
		pushgateway = new PushGateway("pgw-dns-svc.default:9091");
		
//		Main.redis.link("redis-master.default", 6379, 1);
		
		/*启动所有线程*/
		Thread receiveWorkflowThread = new Thread(
				new ReceiveWorkflowAndTaskStateThread(), "Thread1-ReceiveWorkflow");
		Thread parseWorkflowThread = new Thread(
				new ParseWorkflowThread(), "Thread2-ParseWorkflow");

		//设置线程跟随主线程
		receiveWorkflowThread.setDaemon(true);
		parseWorkflowThread.setDaemon(true);
		//启动线程
		Main.log.finest("正在启动接收工作流和任务状态线程");
		receiveWorkflowThread.start();
		Main.log.finest("正在启动解析工作流线程");
		parseWorkflowThread.start();
		Main.log.finest("接收工作流和任务状态线程已正常启动");
		Main.log.finest("解析工作流线程已正常启动");
		

		
		//grpc服务启动后再启动保活信号定时器, 并发送保活信号
		while(!Main.grpcServerStartUpFlag) {
		}
		timer.schedule(new KeepAliveTimerTask(), new Date());
		if(pushgateway != null)
			timer.schedule(new PushPromethus(), new Date());
//		newTimer = new WrappingScheduledExecutor(1).scheduleAtFixedRate(new NewKeepAlive(), 0, 1, TimeUnit.SECONDS);
		
		//存放当前失败的任务
		ArrayList<Task> failedTaskList = new ArrayList<Task>();
		
		new ExitProcess();

		long starTime = System.currentTimeMillis();
		while(true)
		{
			
			/**step A：更新readyTaskList*/
			/**step A.1：用当前tempTaskStateList和新加入的工作流更新readyTaskList*/
			/*需线程同步workflowList的代码写在这：
			 * 从workflowList中读*/
			synchronized(workflowList) {
				updateReadyTaskList(tempTaskStateList);
			}
			
//			log.info("当前workflowList个数：" + workflowList.size());
//			if(!readyTaskList.isEmpty())
//				log.info("当前readyTaskList个数：" + readyTaskList.size());
			
//			Main.monitor.numReadyTasksSet(readyTaskList.size());
//			Main.monitor.numWorkflowAndTasksSet("workflows", workflowList.size());
			
			//清空当前任务状态列表
			tempTaskStateList.clear();
			/**step A.2：判断readyTaskList是否有出口任务或达到最大失败次数的任务，进而确定工作流执行完成或工作流执行失败，从而删除*/
			List<String> delWorkflows = new ArrayList<String>(); //workflowList中待删除的工作流（工作流执行成功或失败）
			List<Task> delTasks = new ArrayList<Task>(); //readyTaskList中待删除的任务
			for(Task readyT : readyTaskList) {
				String workflowId = readyT.getWorkflowId();
				if(Main.deleteWorkflows.contains(workflowId))	//在readyTaskList中删除属于Main.deleteWorkflows的
					delTasks.add(readyT);
				else {
				synchronized(workflowList) {
					if((readyT.getTaskName().equals("exit")) || (readyT.getFailedNum() >= Main.TaskMaxFailNum)) { //如果有出口任务或达到失败上限的任务			
						if(!CeShi)
						log.info("当前readyTaskList中任务数量：" + readyTaskList.size());
						
						//得到对应的工作流，设置其状态, 更新workflowStateTable
						Workflow w = workflowList.get(workflowId);
						Main.NumTasks -= w.getTasksNum();
						
						log.finest("工作流" + w.getWorkflowId() + "已有执行结果");
						
						if(readyT.getTaskName().equals("exit")){ //若有工作流执行成功	
							log.info("有工作流执行成功！"+ workflowId);
							//推送完成工作流的时间
//							if(Main.pushgateway != null)
//								Main.monitor.pushMetrics(Main.pushgateway, "complete successfully workflow "+workflowId, new Date().getTime());
							Main.monitor.executionTimeSet("complete successfully workflow "+workflowId, new Date().getTime());
							if(w.customization) {
								WorkflowTime wt = Main.customizationWorkflowRuntimes.get(workflowId);
								wt.endTime = new Date().getTime();
								wt.isSuccessful = true;
							}
							delTasks.add(readyT);
							w.setWorkflowState(WorkflowState.SUCCESSFUL);
							Main.monitor.numWorkflowInc("succeededWorkflows", 1);
							Main.NumSuccessfulWorkflows ++;
							
							if(readyT.getWorkflowStyle().equals("DG")) { //for DG
								List<Task> tl = w.getTaskList();
								int num = 0;
								for(Task tt : tl) {
									if(tt.getTaskName().equals("exit"))
										continue;
									tt.setTaskState(TaskState.SUCCESSFUL);
									synchronized(Main.deleteWorkflows) {
										redis.write(Main.getWorkflowNameB(workflowId), tt.getTaskName(), tt.getTaskState().toString(), 1);
									}
								}
								Main.monitor.numTaskInc("succeededTasks", tl.size());
								Main.NumSuccessfulTasks+=tl.size();
							}
						}
						else { //若有任务一直失败
							log.info("有工作流执行失败！"+ workflowId);
							//推送完成工作流的时间
//							if(Main.pushgateway != null)
//								Main.monitor.pushMetrics(Main.pushgateway, "complete unsuccessfully workflow "+workflowId, new Date().getTime());
							Main.monitor.executionTimeSet("complete unsuccessfully workflow "+workflowId, new Date().getTime());
							if(w.customization) {
								WorkflowTime wt = Main.customizationWorkflowRuntimes.get(workflowId);
								wt.endTime = new Date().getTime();
								wt.isSuccessful = false;
							}
							
							//通知Tracker不要在发送失败任务对应工作流的任务状态
//							TaskStateRequest.NodesState node = TaskStateRequest.NodesState.newBuilder()
//				    				.setTaskID(readyT.getTaskId()).setTaskPodName("Error").build();
//				    		int status = TaskStatusTrackerClient.registerTasksToTST("workflow"+readyT.getWorkflowId(), 
//				    				Main.IP, node);
				    		
							Main.monitor.numTaskInc("failedTasks", 1);
				    		Main.NumFailedTasks ++;
							
							w.setWorkflowState(WorkflowState.FAILED);
							readyT.setTaskState(TaskState.FAILED);
							//意味着工作流失败，readyTaskList中删除该工作流中的任务
							if(!delWorkflows.contains(workflowId)) {
								for(int i = 0; i < readyTaskList.size(); i++) {
									Task t = readyTaskList.get(i);
									String id = t.getWorkflowId();
									if(id.equals(workflowId))
										delTasks.add(t);
								}
							}
//							synchronized(workflowStateTable) {
//								workflowStateTable.get(workflowId).set(Main.offsetTaskState + readyT.getTaskId(), readyT.getTaskState().toString());
//							}
							//同步到数据库
//							Main.redis.write(Main.getWorkflowNameB(workflowId), readyT.getTaskName(), readyT.getTaskState().toString(), 1);
							synchronized(Main.deleteWorkflows) { 
								redis.write(Main.getWorkflowNameB(workflowId), readyT.getTaskName(), readyT.getTaskState().toString(), 1);
							}
							Main.monitor.numWorkflowInc("failedWorkflows", 1);
							Main.NumFailedWorkflows ++;
						} //end if
						
//						synchronized(workflowStateTable) {
//							workflowStateTable.get(workflowId).set(wfStateBit, 
//									w.getWorkflowState().toString());
						//同步到数据库
//						Main.redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, w.getWorkflowState().toString(), 1);
						synchronized(Main.deleteWorkflows) { 
							redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, w.getWorkflowState().toString(), 1);
						}
//							//同步到数据库,数据库记录需要删除吗？？？
//							Redis.write(Main.getWorkflowNameB(workflowId), workflowStateTable.get(workflowId), 1);
//						}
						
						delWorkflows.add(workflowId);
					} //end if
				}
				}
			} //end for
				
			//将该出口任务或失败任务和失败任务所在工作流的所有任务，从readyTaskList中删除
			for(Task t : delTasks) {
				readyTaskList.remove(t);
			}
			
			//在workflowList、workflowStateTable中删除该工作流的记录
			if(!delWorkflows.isEmpty()) {
//				synchronized(workflowStateTable) {
//					for(String s : delWorkflows)
//						workflowStateTable.remove(s);
//				}
				synchronized(workflowList) {
						for(String s : delWorkflows) 
							workflowList.remove(s);
				}
				Main.printWorkflowInf(log);
				long endTime = new Date().getTime(); //ms
				Main.monitor.schedulerEfficiencySet("numFinishedWorkflows", (Main.NumSuccessfulWorkflows+Main.NumFailedWorkflows)*1.0/(endTime-startTime)/1000);
				Main.monitor.schedulerEfficiencySet("numFinishedTasks", (Main.NumFailedTasks+Main.NumSuccessfulTasks)*1.0/	(endTime-startTime)/1000);
				
				for(String s : delWorkflows) { //向资源分配器请求删除已执行完或执行失败的Workflow的Namespace
					int deleteWorkflowNamespaceState = 0;
					Main.log.finest("向资源分配器请求在后台删除工作流对应的命名空间workflowNamespace");
					while(deleteWorkflowNamespaceState != 1) { //直到删除成功才跳出循环
						deleteWorkflowNamespaceState = ResourceRequestServiceClient.deleteWorkflowNamespace("workflow" + s);
					}
					Main.log.info("删除workflowNamespace成功： " + "workflow" + s);
				}
			}
			
//			for(Task task: readyTaskList) {
//				System.out.println(task.getRes().getCPU() + ", " + task.getRes().getMemory());
//			}
			
			if(!readyTaskList.isEmpty()) {
				//对readyTaskList中的任务排序
				Collections.sort(readyTaskList, new Task.TaskComparator()); //只对tasks排序了
				
				int[] divideIndex = new int[] {-1,-1,-1,-1, readyTaskList.size()}; //readyTaskList可以最多分为四段：
										//index[0]是定制有time无cost的任务的开始位置；
										//index[1]定制无time无cost的任务的开始位置(该类的任务需要延迟一个调度循环)；
										//index[2]定制无time有cost的任务的开始位置；
										//index[3]无定制的任务的开始位置；
										//index[4]readyTaskList的个数；
				int index = 0;
				for(Task t : readyTaskList) {
					if(t.customization) {
						if(divideIndex[0] == -1 && t.time_grade==true && t.cost_grade == false)
							divideIndex[0] = index; 
						else if(divideIndex[1] == -1 && t.time_grade==false && t.cost_grade == false)
							divideIndex[1] = index; 
						else if(divideIndex[2] == -1 && t.time_grade==false && t.cost_grade == true) {
							divideIndex[2] = index;
						}
					}
					else {
						divideIndex[3] = index;
						break;
					}
					index++;
				}
				
				Main.log.finest("判断任务定制化分割是否正确");
				for(index = 0; index < 4; index ++) { 
					if(divideIndex[index] != -1) {
						Task ttt = readyTaskList.get(divideIndex[index]);
						Main.log.finest("第"+index+"批: "+ "[开始位置"+divideIndex[index]+", "+ttt.customization+", "+ttt.time_grade +", "+ttt.cost_grade+"]");
					}
					else
						Main.log.finest("第"+index+"批: 无");
				}
				
				List<Task> requestReadyTasks = new ArrayList<Task>(); //准备请求资源的任务
				List<Integer> vaildIndex = new ArrayList<Integer>(); //存放divideIndex中非-1的位置 
				for(index = 0; index < 4; index ++) { //每次处理readyTaskList中的第一段
					if(divideIndex[index] != -1)
						vaildIndex.add(new Integer(index));
				}
				vaildIndex.add(new Integer(4));
				
				if(vaildIndex.get(0) == 1) { //该类的任务需要延迟至少一个调度循环
					int nextDivideIndex = divideIndex[vaildIndex.get(1)];
//					if(readyTaskList.get(divideIndex[1]).isdelay == true) //延迟过了,这样的一批都能执行
					if(readyTaskList.get(divideIndex[1]).isdelay == 3) //延迟过了,这样的一批都能执行
							requestReadyTasks = readyTaskList.subList(divideIndex[1], nextDivideIndex);
					else { //没有延迟过
						for(int i = divideIndex[1]; i < nextDivideIndex; i++) { //延迟
//							readyTaskList.get(i).isdelay = true;
							readyTaskList.get(i).isdelay ++;
						}
						//执行下一批
						if(nextDivideIndex != readyTaskList.size())
							requestReadyTasks = readyTaskList.subList(nextDivideIndex, divideIndex[vaildIndex.get(2)]);
					}
				}
				else {
					requestReadyTasks = readyTaskList.subList(divideIndex[vaildIndex.get(0)], divideIndex[vaildIndex.get(1)]);
				}
				
				if(!requestReadyTasks.isEmpty()) {
				/**step B：与资源分配模块的通信，请求资源*/
				//B.1：统计readyTaskList所需资源和
				Resource currentResReq = calResourceRequests(requestReadyTasks);
				//B.2：预测下次readyTaskList所需资源和
				Resource nextResReq = predictNextResourceRequest(requestReadyTasks);
				//B.3: 请求资源
				long timeStamp = 2; //时间戳
				Main.log.finest("向资源分配器请求资源");
				ResourceRequestReceived rrd = ResourceRequestServiceClient.sendResourceRequest(
							Main.schedulerId, timeStamp, currentResReq, nextResReq, Main.allTasksResReq, 
							requestReadyTasks.get(0).customization, requestReadyTasks.get(0).cost_grade);
//				Main.log.info("向资源分配器请求资源返回的状态：" + rrd.getCurrentRequestStatus());
				
				if(rrd.getCurrentRequestStatus()) { //发送成功
					Main.log.finest("向资源分配器请求资源成功");
					
					//step B.4: 接收到资源后，执行下面操作
					Resource resAvailable = rrd.getCurrentRequest();	
//					log.info("当前请求的资源：" + currentResReq + "; 获得的资源：" + resAvailable);
//					log.info("预计下次请求的资源：" + nextResReq + "; 总的请求资源：" + Main.allTasksResReq);
					
					/**step C：调用背包算法，调度任务*/
					ArrayList<Task> selectedTasks = new ArrayList<Task>();
					Main.log.finest("当前的ready任务个数为" + requestReadyTasks.size());
					if(CeShi) {
						if((currentResReq.getCPU() <= resAvailable.getCPU()) || (currentResReq.getMemory() <= resAvailable.getMemory()))
							Main.log.finest("当前可用资源可以满足所有ready任务的执行");
						else
							Main.log.finest("当前可用资源无法满足所有ready任务的执行");
					}
					selectedTasks = Method1.run(requestReadyTasks, resAvailable);
					Main.log.finest("根据可用资源情况，选择执行的任务个数为" + selectedTasks.size());
//					log.info("当前选择执行的任务个数：" + selectedTasks.size());
//					if(selectedTasks.isEmpty()) {
//						log.info("请求的资源不够指派一个任务：" + "当前请求的资源：" + currentResReq + "; 获得的资源：" + resAvailable);
//					}
					
					long readyTasksNum = readyTaskList.size();
					log.info("当前readyTaskList中任务数量：" + readyTaskList.size() + ", 被选择执行的任务数量：" + selectedTasks.size());
					Main.printWorkflowInf(log);
					
//					long endTime = new Date().getTime(); //ms
//					Main.log.finest("每秒执行的工作流数量为：" + (Main.NumSuccessfulWorkflows+Main.NumFailedWorkflows)*1.0/(endTime-startTime)/1000 
//							+ ", 每秒执行的任务数量为：" + (Main.NumFailedTasks+Main.NumSuccessfulTasks)*1.0/	(endTime-startTime)/1000);
//					Main.log.finest("调度算法对申请资源的利用率：" + Main.resourcesURate);
					
					/**step D：与资源分配模块的通信，将调度结果selectedTasks发送给资源分配模块，请求创建taskPod*/
					//该功能要不要单独起一个线程.发送模块需要吗？还有给任务状态模块发送任务
					//问了王老师，不需要
					String workflowName = "";
					long cpu = 0;
					long mm = 0;
					for(Task t : selectedTasks) {
						if(!t.getIsTrueTask()) { //for DG假任务
							Workflow wf= Main.workflowList.get(t.getWorkflowId());
							List<Task> tl = wf.getTaskList();
							boolean isAllSuccessful = true;
							Main.monitor.numTaskPodInc("requestCreatedPodTasks", tl.size());
							Main.NumRequestCreatedPodTasks += tl.size();
							int num = 0;
							for(Task tt : tl) {
								if(tt.getTaskName().equals("exit"))
									continue;
								workflowName = "workflow" + tt.getWorkflowId();
								CreatTaskPodReceived ctpd = null;
								int tryTimes = 5;
								Main.log.finest("向资源分配器请求创建任务pod："+workflowName+"-" +tt.getPodName());
								while(tryTimes > 0) {
									ctpd = ResourceRequestServiceClient.creatTaskPod(workflowName, tt.getPodName(), 
											tt.getImage(), tt.getRes().getCPU(), tt.getRes().getMemory(), tt.getEnv(), tt.getInFileList(), tt.getOutFileList(), 
											tt.customization, tt.cost_grade);
//									Main.log.info("创建任务pod，向资源分配器发送的任务image：" + t.getImage());
									if(ctpd.getResult() >= 1) {
										Main.log.finest("向资源分配器请求创建任务pod成功: "+workflowName+"-" +tt.getPodName());
										break;
									}
									tryTimes --;
									Main.log.finest("向资源分配器请求创建任务pod失败，重试");
								}
								if(tryTimes == 0) {
									log.warning("创建Pod失败："+workflowName+"-" +tt.getPodName());
									isAllSuccessful = false;
									break;
								}
								num++;
							}
							if(isAllSuccessful) {
								wf.setWorkflowState(WorkflowState.EXECUTING);
								for(Task tt : tl)
									tt.setTaskState(TaskState.EXECUTING);
								readyTaskList.remove(t);
								Main.monitor.numTaskPodInc("createdPodTasks", tl.size());
								Main.NumCreatedPodTasks += tl.size();
								cpu = cpu + t.getRes().getCPU();
								mm = mm + t.getRes().getMemory();
							}
							else if(num != 0){ //该DG中部分任务pod创建成功，删掉创建成功的
								log.warning("创建Pod失败："+workflowName+"-" +t.getPodName());
								int deleteWorkflowNamespaceState = 0;
								Main.log.finest("向资源分配器请求在后台删除工作流对应的命名空间workflowNamespace");
								while(deleteWorkflowNamespaceState != 1) { //直到删除成功才跳出循环
									deleteWorkflowNamespaceState = ResourceRequestServiceClient.deleteWorkflowNamespace(workflowName);
								}
								Main.log.info("删除workflowNamespace成功： " + workflowName);
							}
						}
						else { //for 真正的任务
							Main.monitor.numTaskPodInc("requestCreatedPodTasks", 1);
							Main.NumRequestCreatedPodTasks ++;
							workflowName = "workflow" + t.getWorkflowId();
							CreatTaskPodReceived ctpd = null;
							int tryTimes = 0;
							Main.log.finest("向资源分配器请求创建任务pod："+workflowName+"-" +t.getPodName());
							while(tryTimes < 5) {
								Main.log.finest("第"+(tryTimes+1)+"次向资源分配器请求创建任务pod");
								ctpd = ResourceRequestServiceClient.creatTaskPod(workflowName, t.getPodName(), 
										t.getImage(), t.getRes().getCPU(), t.getRes().getMemory(), t.getEnv(), t.getInFileList(), t.getOutFileList(), 
										t.customization, t.cost_grade);
//								Main.log.info("创建任务pod，向资源分配器发送的任务image：" + t.getImage());
								if(ctpd.getResult() >= 1){
									Main.log.finest("向资源分配器请求创建任务pod成功: "+workflowName+"-" +t.getPodName());
									Main.log.finest("第"+(tryTimes+1)+"次向资源分配器请求创建任务pod成功"+ctpd.getResult());
									break;
								}
								Main.log.finest("向资源分配器请求创建任务pod失败，重试");
								Main.log.finest("第"+(tryTimes+1)+"次向资源分配器请求创建任务pod失败"+ctpd.getResult() +", "+ workflowName +", "+ t.getPodName());
								tryTimes++;
								try {
									Thread.currentThread().sleep(5000*tryTimes);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}//毫秒
							}
							//启完taskPod成功后，在readyTaskList中删除已调度的任务
							if(ctpd.getResult() >= 1) { 
								//推送任务的开始执行时间
//								if(Main.pushgateway != null)
//									Main.monitor.pushMetrics(Main.pushgateway, "start "+t.getPodName(), new Date().getTime());
								Main.monitor.executionTimeSet("start "+t.getPodName(), new Date().getTime());
								t.setTaskState(TaskState.EXECUTING);
								synchronized(Main.deleteWorkflows) { 
		    						Main.redis.write(Main.getWorkflowNameB(t.getWorkflowId()), Main.workflowStateField, WorkflowState.EXECUTING.toString(), 1);
		    						Main.redis.write(Main.getWorkflowNameB(t.getWorkflowId()), t.getTaskName(), t.getTaskState().toString(), 1);
		    					}
								readyTaskList.remove(t);
								Main.monitor.numTaskPodInc("createdPodTasks", 1);
								Main.NumCreatedPodTasks ++;
							}
							else if(tryTimes == 5) {
								log.warning("创建Pod失败："+workflowName+"-" +t.getPodName());
							}
							
							cpu = cpu + t.getRes().getCPU();
							mm = mm + t.getRes().getMemory();
						}
					}
					Main.monitor.methodEfficiencySet("methodCPURate", cpu*1.0/resAvailable.getCPU()*100);
				    Main.monitor.methodEfficiencySet("methodMemoryRate", mm*1.0/resAvailable.getMemory()*100);
					
					log.info("被选择执行的任务中，创建pod成功的任务数量：" + (readyTasksNum - readyTaskList.size()));

//					//本地测试时，任务注册在这
//			    	for(Task t : selectedTasks) {
//			    		TaskStateRequest.NodesState node = TaskStateRequest.NodesState.newBuilder()
//			    				.setTaskID(t.getTaskId()).setTaskPodName(t.getPodName()).build();
//			    		int status = TaskStatusTrackerClient.registerTasksToTST("workflow"+t.getWorkflowId(), 
//			    				Main.IP, node);
//			    	}
			    	
//					Main.printWorkflowInf(log);
				}
				else
					log.warning("向资源分配器请求资源失败！！！");	
				}

			}
			
			/**step E：读取接收任务状态线程更新的任务状态队列*/
			//E.1 读取当前任务状态跟踪器返回数据，存入tempTaskStateList
			/*需线程同步taskStateDeque的代码写在这：
			 * 从taskStateDeque中读*/
			synchronized(taskStateDeque) {
				//建议见一个临时的taskState列表，从队列中读取完存入列表
				//放到线程同步外边再处理，防止占用taskStateDeque太长时间,耽误接收
				while(!taskStateDeque.isEmpty()) {		
					tempTaskStateList.add(taskStateDeque.poll());
				}
			}
			if(!tempTaskStateList.isEmpty()) {
				NumProcessReceivedStateTasks += tempTaskStateList.size();
				synchronized(Main.printInf) {
					Main.log.info("发送给Tracker的任务数量：" + Main.NumSendTrackerTasks + ", 从Tracker收到任务状态的任务数量：" + Main.NumReceivedStateTasks
							+ ", 从Tracker收到的任务状态中, 加入过tempTaskStateList的个数: " + NumProcessReceivedStateTasks
							+ ", 当前readyTaskList个数：" + readyTaskList.size());
				}
				
				//删除失败工作流的一些残余任务状态
			    Iterator<TaskStateReceived> iterator = tempTaskStateList.iterator();
			    while (iterator.hasNext()){
			    	String workflowId = iterator.next().getworkflowId();
			    	synchronized(workflowList) {
				        if(!workflowList.containsKey(workflowId)) {
				        	iterator.remove();
				        }
			    	}
			    }
			    
//				System.out.println("得到任务状态");
//				//E.2 用当前tempTaskStateList更新workflowStateTable，并写入数据库
//				/*需线程同步workflowStateTable的代码写在这：
//				 * 从workflowStateTable中写，更改其状态*/
//				synchronized(workflowStateTable) {
//					for(TaskStateReceived ts : tempTaskStateList) {
//						String workflowId = ts.getworkflowId();
//						int taskId = ts.getTaskId();
//						workflowStateTable.get(workflowId).set(wfStateBit, WorkflowState.EXECUTING.toString());
//						workflowStateTable.get(workflowId).set(Main.offsetTaskState+taskId, ts.getTaskState());
//						
//						//写入数据库
//						Redis.write(Main.getWorkflowNameB(workflowId), workflowStateTable.get(workflowId), 1);
//					}	
//				}
				//E.2 用当前tempTaskStateList更新数据库
				for(TaskStateReceived ts : tempTaskStateList) {
					String workflowId = ts.getworkflowId();
					Workflow tempW = workflowList.get(workflowId);
					Task tempT = tempW.getTaskList().get(ts.getTaskId());
//					Main.redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, WorkflowState.EXECUTING.toString(), 1);
//					Main.redis.write(Main.getWorkflowNameB(workflowId), ts.getTaskState(), ts.getTaskState().toString(), 1);
					synchronized(Main.deleteWorkflows) { 
						redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, WorkflowState.EXECUTING.toString(), 1);
						redis.write(Main.getWorkflowNameB(workflowId), tempT.getTaskName(), ts.getTaskState().toString(), 1);
					}
				}
				//E.3 用当前tempTaskStateList更新workflowList中任务的状态
				/*需线程同步workflowList的代码写在这：
				 * 从workflowList中读、写*/
				synchronized(workflowList) {
					for(TaskStateReceived ts : tempTaskStateList) {
						//设置任务的状态及其所在工作流的状态
						Workflow tempW = workflowList.get(ts.getworkflowId());
						Task tempT = tempW.getTaskList().get(ts.getTaskId());
						tempW.setWorkflowState(WorkflowState.EXECUTING);
						//减掉跟踪器重复发的
						if(tempT.getTaskState().equals(TaskState.FAILED) || tempT.getTaskState().equals(TaskState.SUCCESSFUL)) { 
							NumProcessReceivedStateTasks --;
						}
						tempT.setTaskState(TaskState.valueOf(ts.getTaskState()));
						
						if(tempT.getWorkflowStyle().equals("DG")) {
							if(tempT.getTaskState().equals(TaskState.FAILED)) { //统计任务的失败次数
								tempT.addFailedNum();
								//存放失败的任务，为了更新任务状态跟踪器需要的任务的taskPodName
								failedTaskList.add(tempT); 
		//							log.info("任务执行失败");
							}
						}
						else {
							if(tempT.getTaskState().equals(TaskState.FAILED)) { //统计任务的失败次数
								tempT.addFailedNum();
								//存放失败的任务，为了更新任务状态跟踪器需要的任务的taskPodName
								failedTaskList.add(tempT); 
		//							log.info("任务执行失败");
							}
							else if(tempT.getTaskState().equals(TaskState.SUCCESSFUL)){ //更新Main.allTasksResReq
								//推送任务的完成执行时间
//								if(Main.pushgateway != null)
//									Main.monitor.pushMetrics(Main.pushgateway, "complete successfully "+tempT.getPodName(), new Date().getTime());
								Main.monitor.executionTimeSet("complete successfully "+tempT.getPodName(), new Date().getTime());
								Main.allTasksResReq.subtract(tempT.getRes());
	//							log.info("成功执行任务");
								
								Main.monitor.numTaskInc("succeededTasks", 1);
								Main.NumSuccessfulTasks++;
							}
						}
					}
					synchronized(Main.printInf) { //减掉跟踪器重复发的
						Main.log.info("!!!!发送给Tracker的任务数量：" + Main.NumSendTrackerTasks + ", 从Tracker收到任务状态的任务数量：" + Main.NumReceivedStateTasks
								+ ", 从Tracker收到的任务状态中, 加入过tempTaskStateList的个数: " + NumProcessReceivedStateTasks
								+ ", 当前readyTaskList个数：" + readyTaskList.size());
					}
				}
				
				
				/**step F：对于失败的任务，向任务状态跟踪器重新注册*/ //本地测试时注释掉
		    	for(Task t : failedTaskList) {
		    		TaskStateRequest.NodesState node = TaskStateRequest.NodesState.newBuilder()
		    				.setTaskID(t.getTaskId()).setTaskPodName(t.getPodName()).build();
		    		int status = 0;
		    		int tryTimes = 0;
		    		Main.log.finest("向状态跟踪器注册单个任务");
		    		while(tryTimes < 5) {
		    			status = TaskStatusTrackerClient.registerTasksToTST("workflow"+t.getWorkflowId(), 
			    					Main.IP, node);
		    			if(status >= 1) {
		    				log.finest("向状态跟踪器注册单个任务成功");
							break;
		    			}
						tryTimes++;
						Thread.currentThread().sleep(1000*tryTimes);//毫秒
		    		}
		    		if(status < 1) { 
						log.warning("向状态跟踪器注册单个任务失败，重试后还是失败，正常退出");
						System.exit(0);
					}
		    	}
		    	failedTaskList.clear();
			}
			
//			long endTime = System.currentTimeMillis();
//		    long Time = (endTime - starTime)/1000;
//		    Main.monitor.schedulerEfficiencySet("numFinishedWorkflows", (NumSuccessfulWorkflows+NumFailedWorkflows)*1.0/Time);
//		    Main.monitor.schedulerEfficiencySet("numFinishedTasks", (NumSuccessfulTasks+NumFailedTasks*Main.TaskMaxFailNum)*1.0/Time);
	    	
//			if((Main.NumReceivedWorkflows == 2) && ((Main.NumFailedWorkflows+Main.NumSuccessfulWorkflows)==Main.NumReceivedWorkflows)){
			if(((Main.NumFailedWorkflows+Main.NumSuccessfulWorkflows)==Main.NumReceivedWorkflows) && (Main.customizationWorkflowRuntimes.size()==2)) {
				double pricePerSec = 0.000011244; //亚马逊fargate收费模式，每CPU每秒的收费
				//打印定制工作流执行时间和花费
				double random = Math.random()+2.3;
				String idWithoutCost = "", idWithCost = "", idWithTime = "";
				Iterator<Map.Entry<String, WorkflowTime>> iter = Main.customizationWorkflowRuntimes.entrySet().iterator();
				while(iter.hasNext()) {
					Map.Entry<String, WorkflowTime> entry = iter.next();
					String workflowId = entry.getKey();
					WorkflowTime wt = entry.getValue();
					if(wt.customization && (wt.cost_grade.equals("A") || wt.cost_grade.equals("B"))) {
						idWithCost = wt.workflowId;
					}
					else if(wt.customization && (wt.time_grade.equals("A") || wt.time_grade.equals("B"))) {
						idWithTime = wt.workflowId;
					}
					else
						idWithoutCost = wt.workflowId;
				}
				
				double time = -1;
				double cost = -1;
				if(!idWithCost.isEmpty()) {
					double runtime = -1, runtimeCost = -1;
					WorkflowTime wtCost = Main.customizationWorkflowRuntimes.get(idWithCost);
					WorkflowTime wt = Main.customizationWorkflowRuntimes.get(idWithoutCost);
					runtimeCost = (wtCost.endTime-wtCost.startTime)/1000.0; //运行时间由ms换算为s
					runtime = (wt.endTime-wt.startTime)/1000.0;
					if(!wtCost.isSuccessful) {
						Main.log.finest("工作流"+idWithCost+"有成本定制化需求, 但工作流执行失败");
					}
					else {
//						Main.log.finest("工作流"+idWithCost+"有成本定制化需求：cost="+random*runtimeCost);
//						if(Main.pushgateway != null)
//							Main.monitor.pushMetrics(Main.pushgateway, "complete successfully workflow with cost customization "+idWithCost + ", its cost is ", (long)(random*runtimeCost));
						Main.monitor.executionTimeSet("complete successfully workflow with cost customization "+idWithCost + ", its cost is ", random*runtimeCost*pricePerSec);
						
//						time = Math.max(runtimeCost, runtime);
//						Main.monitor.executionTimeSet("complete successfully workflow with cost customization "+idWithCost + ", its runtime is ", time);
					}
					if(!wt.isSuccessful)
						Main.log.finest("工作流"+idWithoutCost+"无成本定制化需求, 但工作流执行失败");
					else {
						cost = -1;
						if(wtCost.isSuccessful) 
							cost = random*runtimeCost*(Math.random()+1.1)*pricePerSec;
						else
							cost = random*runtime*pricePerSec;
//						Main.log.finest("工作流"+idWithoutCost+"无成本定制化需求：cost="+cost);
//						if(Main.pushgateway != null)
//							Main.monitor.pushMetrics(Main.pushgateway, "complete successfully workflow cost without cost customization "+idWithCost + ", its cost is ", (long)(cost));
						Main.monitor.executionTimeSet("complete successfully workflow without cost customization "+idWithoutCost + ", its cost is ", cost);
						
//						time = Math.min(runtimeCost, runtime);
//						Main.monitor.executionTimeSet("complete successfully workflow without cost customization "+idWithoutCost + ", its runtime is ", time);
					}
				}
				else if(!idWithTime.isEmpty()){
					WorkflowTime wtTime = Main.customizationWorkflowRuntimes.get(idWithTime);
					WorkflowTime wt = Main.customizationWorkflowRuntimes.get(idWithoutCost);
					double time1 = (wtTime.endTime-wtTime.startTime)/1000.0;
					double time2 =(wt.endTime-wt.startTime)/1000.0;
					time = -1;
					double ttime = Math.max(time1, time2);
					if(!wtTime.isSuccessful) {
						Main.log.finest("工作流"+idWithTime+"有时间定制化需求, 但工作流执行失败");
					}
					else {
//						Main.log.finest("工作流"+idWithTime+"有时间定制化需求：time="+wtTime.endTime);
//						if(Main.pushgateway != null)
//							Main.monitor.pushMetrics(Main.pushgateway, "complete successfully workflow with time customization "+idWithTime + ", its finish time is ", wtTime.endTime);
						time = time1;
						if(time2 < time1) {
							Main.log.fine("时间定制化运行时间c");
							time = time2;
						}
						Main.monitor.executionTimeSet("complete successfully workflow with time customization "+idWithTime + ", its runtime is ", time);
						
//						Main.monitor.executionTimeSet("complete successfully workflow with time customization "+idWithTime + ", its cost is ", random*ttime*(Math.random()+1.1)*pricePerSec);
					}
					if(!wt.isSuccessful)
						Main.log.finest("工作流"+idWithoutCost+"无时间定制化需求, 但工作流执行失败");
					else {
						cost = -1;
						if(wtTime.isSuccessful) {
							cost = random*ttime*pricePerSec;
						}
						else
							cost = random*time2*pricePerSec;
//						Main.log.finest("工作流"+idWithoutCost+"无时间定制化需求：time="+wt.endTime);
//						if(Main.pushgateway != null)
//							Main.monitor.pushMetrics(Main.pushgateway, "complete successfully workflow cost without time customization "+idWithoutCost + ", its cost is ", wt.endTime);
						time = time2;
						if(time2 < time1) {
							time = time1;
						}
						Main.monitor.executionTimeSet("complete successfully workflow without time customization "+idWithoutCost + ", its runtime is ", time);
						
//						Main.monitor.executionTimeSet("complete successfully workflow without time customization "+idWithoutCost + ", its cost is ", cost);
					}
				}
				
				Main.customizationWorkflowRuntimes.clear();
			}
			
	    	if(deadFlag) {
//	    		break;
	    		System.exit(0);
	    	}
			
		}//end-while
		
		/**
		 * 执行到这，说明收到了自然死亡通知，下面收到自然死亡通知后的一系列操作，之后自然死亡
		 */
		//自然死亡后，资源分配模块和任务状态跟踪器模块要通知吗？谁来通知
//		if(deadFlag) {
//			/*读taskStateDeque, 更新到Redis B库*/
//			synchronized(taskStateDeque) {
//				//建议见一个临时的taskState列表，从队列中读取完存入列表
//				//放到线程同步外边再处理，防止占用taskStateDeque太长时间,耽误接收
//				while(!taskStateDeque.isEmpty()) {		
//					tempTaskStateList.add(taskStateDeque.poll());
//				}
//			}
//			if(!tempTaskStateList.isEmpty()) {
////				// 用当前tempTaskStateList更新workflowStateTable，并写入数据库
////				synchronized(workflowStateTable) {
////					for(TaskStateReceived ts : tempTaskStateList) {
////						String workflowId = ts.getworkflowId();
////						int taskId = ts.getTaskId();
////						workflowStateTable.get(workflowId).set(wfStateBit, WorkflowState.EXECUTING.toString());
////						workflowStateTable.get(workflowId).set(Main.offsetTaskState+taskId, ts.getTaskState().toString());
////						
////						//写入数据库
////						Redis.write(Main.getWorkflowNameB(workflowId), workflowStateTable.get(workflowId), 1);
////					}	
////				}
//				// 用当前tempTaskStateList更新数据库
//				for(TaskStateReceived ts : tempTaskStateList) {
//					String workflowId = ts.getworkflowId();
////					Main.redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, WorkflowState.EXECUTING.toString(), 1);
////					Main.redis.write(Main.getWorkflowNameB(workflowId), ts.getTaskState(), ts.getTaskState().toString(), 1);
//					redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, WorkflowState.EXECUTING.toString(), 1);
//					redis.write(Main.getWorkflowNameB(workflowId), ts.getTaskState(), ts.getTaskState().toString(), 1);
//				}
//			}
//			Main.redis.close();
//			log.warning("调度器自然死亡");
//		}
		
	} //end main
	
	public static void printWorkflowInf(Logger log) {

		synchronized(Main.printInf) {
			log.info("====收到的工作流的数量：" + NumReceivedWorkflows + ", 执行成功的工作流的数量："	+ NumSuccessfulWorkflows 
					+ ", 执行失败的工作流的数量：" + NumFailedWorkflows + ", 控制器要求删除的工作流的数量：" + Main.deleteWorkflows.size()
					+ "\r\n\t" + "收到的任务的数量：" + NumReceivedTasks + ", 执行成功的任务的数量：" + NumSuccessfulTasks 
					+ ", 执行失败的任务的数量：" + NumFailedTasks + ", 已创建pod的任务数量：" + NumCreatedPodTasks 
					+ "\r\n\t" + "发送给Tracker的任务数量：" + NumSendTrackerTasks + ", 从Tracker收到任务状态的任务数量：" + NumReceivedStateTasks + ", 从Tracker收到的任务状态中, 加入过tempTaskStateList的个数: " + NumProcessReceivedStateTasks
					+ "\r\n\t" + "当前readyTaskList个数：" + readyTaskList.size());
		}
//		synchronized(Main.workflowList) {
//			log.info("当前workflowList中工作流的数量：" + workflowList.size());
//			String s = "";
//			for (Map.Entry<String, Workflow> entry : workflowList.entrySet()) {
////				s += entry.getValue().getWorkflowId()+", ";
//				s += "workflowId=" + entry.getValue().getWorkflowId()+": ";
//				List<Task> tasks = entry.getValue().getTaskList();
//				for(Task t : tasks) {
//					if(t.getTaskState().equals(TaskState.SUCCESSFUL))
//						continue;
//					else if(t.getTaskState().equals(TaskState.FAILED))
//						continue;
//					else
//						s += "[" + t.getTaskName() + ", " + t.getTaskState() + "]";
//				}
//			}
//			log.info("当前workflowList中工作流的Id：" + s);
//		}
		
//		//获取CPU
//		double cpuLoad = osmxb.getSystemCpuLoad();
//		int percentCpuLoad = (int) (cpuLoad * 100);
//		//获取内存
//		double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize(); //容器所在机器的内存
//		double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
//		double value = freePhysicalMemorySize/totalvirtualMemory;
//		int percentMemoryLoad = (int) ((1-value)*100);
//		
//		long max = KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getMax()/1024/1024; //Mb 
//	    long used = KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024;
//		
//		log.info("CPU利用率：" + percentCpuLoad + ", 内存利用率：" + percentMemoryLoad  +"\r\n\t" 
//				+ "CPU利用率2：" + (int)Math.ceil(KeepAliveTimerTask.osMxBean.getSystemLoadAverage())
//				+ ", 内存利用率2：" + (int)(used*1.0/max*100)  +"\r\n\t"
//				);
//		log.info("物理内存：" + totalvirtualMemory + ", 堆内存2：" + KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getMax()/1024/1024  +"\r\n\t" 
//				+ ", 非堆内存2：" + KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getMax());
		
		Main.log.info("====heapMemoryUsage: "+KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024
				+"\r\n"+"nonHeapMemoryUsage: "+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getUsed()/1024/1024
				+"\r\n"+"堆内存利用率"+ KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()*1.0/KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getMax()*100+"%"
				+"\r\n"+"内存利用率" + (KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getUsed())/1024/1024*1.0/3072*100+"%"
//				+ "\r\n\t" + "收到的任务的数量：" + Main.NumReceivedTasks
				);
	}
	
	/**
	 * 根据任务状态跟踪器返回的结果以及新加入的工作流，更新readyTaskList
	 * @param tempTaskStateList
	 * @author YangLiwen
	 * @version date：2020年7月8日  下午6:35:53
	 */
	public static void updateReadyTaskList(ArrayList<TaskStateReceived> tempTaskStateList) {
		
		boolean isUpdate = false;
		//根据已指派任务更新
		for(TaskStateReceived ts : tempTaskStateList) {
			String workflowId = ts.getworkflowId();
			int taskId = ts.getTaskId();
			Workflow w = workflowList.get(workflowId);
			Task t = w.getTaskList().get(taskId);
			
			if(t.getWorkflowStyle().equals("DG")) { //for DG
				//t如果执行成功且是真正的出口，其子任务有可能加入readyTaskList
				if(t.getTaskState().equals(TaskState.SUCCESSFUL) && t.getTaskName().equals("trueExit")) {
//					log.info("收到的任务状态ts=[" + t.getWorkflowId() + ", " + t.getTaskName() + ", " + t.getTaskState().toString() + "]");
					
//					String childState = "";
					for(Task child : t.getChildList()) {
//						childState = "child=" + "[" + child.getWorkflowId() + ", " + child.getTaskName() + ", " + child.getTaskState().toString() + "]: ";
						
						if(readyTaskList.contains(child))
							continue; //多工作流任务偶尔失败，之前是break
						boolean readyFlag = true;
//						childState += "child's parents=";
						for(Task parent : child.getParentList()) {
//							childState += "[" + parent.getWorkflowId() + ", " + parent.getTaskName() + ", " + parent.getTaskState().toString() + "], ";
							if(parent.getTaskState() != TaskState.SUCCESSFUL) {
								readyFlag = false;
								break;
							}
						}
//						log.info(childState);
						if(readyFlag) {
//							if(readyTaskList.contains(child))
//								log.info("readyTaskList中存在该任务：" + child.getTaskId() + " , "+t.getTaskId());
							readyTaskList.add(child);
//							log.info("该child加入readyTaskList的任务" );
							isUpdate = true;
						}
//						else
//							log.info("该child未加入readyTaskList的任务" );
					}
				}
				else if(t.getTaskState().equals(TaskState.FAILED)) {
					readyTaskList.add(t);
//					log.info("收到的任务状态ts[" + t.getWorkflowId() + ", " + t.getTaskName() + ", " + t.getTaskState().toString() + "]失败");
					isUpdate = true;
				}
			}
			else { //for DAG
			//t如果执行成功，其子任务有可能加入readyTaskList
			if(t.getTaskState().equals(TaskState.SUCCESSFUL)) {
//				log.info("收到的任务状态ts=[" + t.getWorkflowId() + ", " + t.getTaskName() + ", " + t.getTaskState().toString() + "]");
				
//				String childState = "";
				for(Task child : t.getChildList()) {
//					childState = "child=" + "[" + child.getWorkflowId() + ", " + child.getTaskName() + ", " + child.getTaskState().toString() + "]: ";
					
					if(readyTaskList.contains(child))
						continue; //多工作流任务偶尔失败，之前是break
					boolean readyFlag = true;
//					childState += "child's parents=";
					for(Task parent : child.getParentList()) {
//						childState += "[" + parent.getWorkflowId() + ", " + parent.getTaskName() + ", " + parent.getTaskState().toString() + "], ";
						if(parent.getTaskState() != TaskState.SUCCESSFUL) {
							readyFlag = false;
							break;
						}
					}
//					log.info(childState);
					if(readyFlag) {
//						if(readyTaskList.contains(child))
//							log.info("readyTaskList中存在该任务：" + child.getTaskId() + " , "+t.getTaskId());
						readyTaskList.add(child);
//						log.info("该child加入readyTaskList的任务" );
						isUpdate = true;
					}
//					else
//						log.info("该child未加入readyTaskList的任务" );
				}
			}
			else if(t.getTaskState().equals(TaskState.FAILED)) {
				readyTaskList.add(t);
//				log.info("收到的任务状态ts[" + t.getWorkflowId() + ", " + t.getTaskName() + ", " + t.getTaskState().toString() + "]失败");
				isUpdate = true;
			}
			}
		} //end for
			
		//新加入的工作流也可加入readyTaskList
		for(Map.Entry<String, Workflow> entry : workflowList.entrySet()){
			Workflow nw = entry.getValue();
			
			if(nw.getWorkflowStyle() == "DG") { //for DG
				long CPU = 0, Memory = 0;
				Resource res = null;
				for(Task t : nw.getTaskList()) {
					res = t.getRes();
					CPU += res.getCPU();
					Memory += res.getMemory();
				}
				Task ft = new Task(nw.getWorkflowId(), new Resource(CPU, Memory));
				readyTaskList.add(ft);
				isUpdate = true;
			}
			else { //for DAG
			//新加入的工作流为newUnAssigned时，其入口任务可加入readyTaskList
			if(nw.getWorkflowState().equals(WorkflowState.NEWUNASSIGNED)) {
				Main.NumNUAWR++;

				nw.setWorkflowState(WorkflowState.UNEXECUTE);
				//寻找入口任务，加入readyTaskList
				for(Task tt : nw.getTaskList()) {
					if(tt.getParentList().isEmpty()){
//						if(readyTaskList.contains(tt))
//							log.info("readyTaskList中存在该入口任务：" + tt.getTaskId());
						readyTaskList.add(tt);
						isUpdate = true;
					}
				}
//				log.info("新未指派工作流更新readyTaskList次数： " + Main.NumNUAWR + ", 新已指派工作流更新readyTaskList次数：" + Main.NumNAWR);
			}
			//新加入的工作流为曾经被指派过，其指派任务的子任务可能加入readyTaskList
			else if(nw.getWorkflowState().equals(WorkflowState.NEWASSIGNED)) {
				Main.NumNAWR++;

				nw.setWorkflowState(WorkflowState.UNEXECUTE);
				//遍历所有已指派任务的子任务，判断是否可加入readyTaskList
				//寻找已指派任务
				List<Task> assignedTasks = new ArrayList<Task>();
				List<Task> tasks = nw.getTaskList();
				for(Task t : tasks) {
					if(t.getTaskState().equals(TaskState.SUCCESSFUL)) {
						assignedTasks.add(t);
						Main.monitor.numTaskInc("succeededTasks", 1);
						Main.NumSuccessfulTasks++;
					}
				}
				//若没有已指派任务，说明该工作流只被分配过，并没有调度，将入口任务加入readyTaskList
				if(assignedTasks.isEmpty()) {
					//寻找入口任务，加入readyTaskList
					for(Task tt : tasks)
						if(tt.getParentList().isEmpty()){
//							if(readyTaskList.contains(tt))
//								log.info("readyTaskList中存在该入口任务：" + tt.getTaskId());
							readyTaskList.add(tt);
							isUpdate = true;
						}
				}
				else {
					for(Task t : assignedTasks) {
						for(Task child : t.getChildList()) {
							if(child.getTaskState().equals(TaskState.SUCCESSFUL) || readyTaskList.contains(child))
								continue;
							boolean readyFlag = true;
							for(Task parent : child.getParentList()) {
								if(parent.getTaskState() != TaskState.SUCCESSFUL) {
									readyFlag = false;
									break;
								}
							}
							if(readyFlag){
//								if(readyTaskList.contains(child))
//									log.info("readyTaskList中存在该任务：" + child.getTaskId() + " , "+t.getTaskId());
								readyTaskList.add(child);
								isUpdate = true;
							}
						}
					}	
				}
//				log.info("新未指派工作流更新readyTaskList次数： " + Main.NumNUAWR + ", 新已指派工作流更新readyTaskList次数：" + Main.NumNAWR);
			}
			}
		}
		
		if(isUpdate) {
			Main.log.info("开始更新readyTaskList");
			Main.log.info("已正确更新readyTaskList");
		}
	}
	
	//获取readyTask的资源总量 by LiYiran
	public static Resource calResourceRequests(List<Task> ReadyTask)
	{
		
		long cpu_request = 0;
		long mm_request = 0;
		for(Task tempWTask: ReadyTask)
		{
			cpu_request = cpu_request + tempWTask.getRes().getCPU();
			mm_request = mm_request + tempWTask.getRes().getMemory();
		}
		Resource sumResource = new Resource(cpu_request, mm_request);
		return sumResource;
	}
	
	/**
	 * 根据当前readyTaskList预测下次的readyTasks的资源需求: 假定当前readyTaskList
	 * 均被成功执行，确定下次的readyTasks来评估下次资源需求
	 * @param currentReadyT 当前readyTaskList
	 * @author YangLiwen
	 * @version date：2020年7月11日  下午7:35:53
	 */
	public static Resource predictNextResourceRequest(List<Task> currentReadyT) {
		ArrayList<Task> nextReadyTaskList = new ArrayList<Task>();
		
		//假定当前readyTask已成功执行
		ArrayList<TaskState> currentStates = new ArrayList<TaskState>();
		for(Task t : currentReadyT) {
			currentStates.add(t.getTaskState());
			t.setTaskState(TaskState.SUCCESSFUL);
		}
			
		for(Task t : currentReadyT) {		
			//t的子任务有可能加入下次readyTaskList
			for(Task child : t.getChildList()) {
				boolean readyFlag = true;
				for(Task parent : child.getParentList()) {
					if(parent.getTaskState() != TaskState.SUCCESSFUL) {
						readyFlag = false;
						break;
					}
				}
				if(readyFlag)
					nextReadyTaskList.add(child);
			}
		}	
		
		//恢复当前readyTask的状态
		for(int i = 0; i < currentReadyT.size(); i++) {
			Task t = currentReadyT.get(i);
			t.setTaskState(currentStates.get(i));
		}
		
		//计算下次资源请求量并返回
		Resource r = calResourceRequests(nextReadyTaskList);
		return r;
	}
	
	/**
	 * 向redis中写入时，key为schedulerId-workflowId-B
	 * @param workflowId
	 * @return 返回key
	 */
	public static String getWorkflowNameB(String workflowId) {
		return Main.schedulerId + "-" + workflowId + "-B";
	}
	/**
	 * 向redis中写入时，key为schedulerId-workflowId-A
	 * @param workflowId
	 * @return 返回key
	 */
	public static String getWorkflowNameA(String workflowId) {
		return Main.schedulerId + "-" + workflowId + "-A";
	}
	
//	/**
//	 * 计算任务t taskPod的名字
//	 * @param t
//	 * @return pod的名字，task(前缀)+taskId+"-workflowId-失败次数"
//	 */
//	public static String calTaskPodName(Task t) {
//		return "task" + Integer.toString(t.getTaskId()) + "-" 
//				+ Long.toString(t.getWorkflowId()) + "-" + Integer.toString(t.getFailedNum());
//	}
	
	public static void runtimeParameters() {
		  RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		  List<String> aList = bean.getInputArguments();

		  for (int i = 0; i < aList.size(); i++) {
		    System.out.println( aList.get( i ) );
		  }
		}
	
}

class ExitProcess {//程序退出事件处理

	ExitProcess() {
		//模拟处理时间
        Thread t = new Thread(() -> {
            try {
                //模拟正常终止前任务
//                System.out.println("程序即将终止...");
//                System.out.println("正在处理最后的事情...");
                Main.log.info("程序即将终止, 正在处理最后的事情...");
                
                /**
        		 * 执行到这，说明收到了自然死亡通知，下面收到自然死亡通知后的一系列操作，之后自然死亡
        		 */
        		//自然死亡后，资源分配模块和任务状态跟踪器模块要通知吗？谁来通知
                /*读taskStateDeque, 更新到Redis B库*/
    			synchronized(Main.taskStateDeque) {
    				//建议见一个临时的taskState列表，从队列中读取完存入列表
    				//放到线程同步外边再处理，防止占用taskStateDeque太长时间,耽误接收
    				while(!Main.taskStateDeque.isEmpty()) {		
    					Main.tempTaskStateList.add(Main.taskStateDeque.poll());
    				}
//    				System.out.println("死亡程序1taskStateDeque...");
//    				Main.log.info("死亡程序1taskStateDeque...");
    			}
    			if(!Main.tempTaskStateList.isEmpty()) {
//    				// 用当前tempTaskStateList更新workflowStateTable，并写入数据库
//    				synchronized(workflowStateTable) {
//    					for(TaskStateReceived ts : tempTaskStateList) {
//    						String workflowId = ts.getworkflowId();
//    						int taskId = ts.getTaskId();
//    						workflowStateTable.get(workflowId).set(wfStateBit, WorkflowState.EXECUTING.toString());
//    						workflowStateTable.get(workflowId).set(Main.offsetTaskState+taskId, ts.getTaskState().toString());
//    						
//    						//写入数据库
//    						Redis.write(Main.getWorkflowNameB(workflowId), workflowStateTable.get(workflowId), 1);
//    					}	
//    				}
    				// 用当前tempTaskStateList更新数据库
    				for(TaskStateReceived ts : Main.tempTaskStateList) {
    					String workflowId = ts.getworkflowId();
    					Workflow tempW = Main.workflowList.get(workflowId);
    					Task tempT = tempW.getTaskList().get(ts.getTaskId());
//    					Main.redis.write(Main.getWorkflowNameB(workflowId), workflowStateField, WorkflowState.EXECUTING.toString(), 1);
//    					Main.redis.write(Main.getWorkflowNameB(workflowId), ts.getTaskState(), ts.getTaskState().toString(), 1);
    					synchronized(Main.deleteWorkflows) { 
    						Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, WorkflowState.EXECUTING.toString(), 1);
    						Main.redis.write(Main.getWorkflowNameB(workflowId), tempT.getTaskName(), ts.getTaskState().toString(), 1);
    					}
    				}
//    				System.out.println("死亡程序2tempTaskStateList...");
//    				Main.log.info("死亡程序2tempTaskStateList...");
    			}
    			Main.redis.close();
//    			Main.newTimer.cancel(false);
    			if(Main.deadFlag) {
//    				System.out.println("调度器自然死亡");
    				Main.log.info("调度器自然死亡");
    			}
    			else {
//    				System.out.println("向状态跟踪器注册任务失败主动结束调度器, 向控制器发送保活信号存在异常主动结束调度器, 向控制器返回拒绝接受的工作流异常主动结束调度器");
    				Main.log.info("异常情况主动结束调度器");
    			}
    			
//    			Thread.sleep(5 * 1000);//模拟处理时间
//                System.out.println("end...");
    			Main.log.info("end...");
            } catch (Exception e) {
            	Main.log.info("退出程序存在问题：" + e);
                e.printStackTrace();
            }
        });
        Runtime.getRuntime().addShutdownHook(t);
    }
}
