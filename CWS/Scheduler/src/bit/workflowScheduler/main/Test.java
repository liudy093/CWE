package bit.workflowScheduler.main;

import java.io.IOException;
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


import bit.workflowScheduler.grpc.Scheduler.ProtoWF;
import bit.workflowScheduler.grpc.TaskStatusTracker.TaskStatusTrackerClient;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest;
import bit.workflowScheduler.inerface.CreatTaskPodReceived;
import bit.workflowScheduler.inerface.ResourceRequestReceived;
import bit.workflowScheduler.inerface.TaskStateReceived;
import bit.workflowScheduler.problem.Resource;
import bit.workflowScheduler.problem.Task;
import bit.workflowScheduler.problem.Workflow;
import bit.workflowScheduler.scheduleModel.Method1;
import bit.workflowScheduler.util.Parameters.TaskState;
import bit.workflowScheduler.util.Parameters.WorkflowState;
import bit.workflowScheduler.util.RedisTest;
import bit.workflowScheduler.util.WorkflowTime;
import redis.clients.jedis.JedisCluster;

public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		HashMap<String, String> customizationWorkflowRuntimes = new HashMap<String, String>();
		System.out.println(customizationWorkflowRuntimes.size()+", "+customizationWorkflowRuntimes.isEmpty());
		customizationWorkflowRuntimes.put("1", "first");
		System.out.println(customizationWorkflowRuntimes.size()+", "+customizationWorkflowRuntimes.isEmpty());
		customizationWorkflowRuntimes.clear();
		System.out.println(customizationWorkflowRuntimes.size()+", "+customizationWorkflowRuntimes.isEmpty());
		
//		//测试任务排序
//		Task t= new Task();t.setTaskId(0);t.customization=true;t.time_grade=true;t.cost_grade=false;
//		Task t1= new Task();t1.setTaskId(1);t1.customization=true;t1.time_grade=false;t.cost_grade=false;
//		Task t2= new Task();t2.setTaskId(2);t2.customization=true;t2.time_grade=false;t2.cost_grade=true;
//		Task t3= new Task();t3.setTaskId(3);t3.customization=false;t3.time_grade=true;t3.cost_grade=true;
//		Task t4= new Task();t4.setTaskId(4);t4.customization=false;t4.time_grade=false;t4.cost_grade=true;
//		List<Task> taskSet = new ArrayList(); 
//		taskSet.add(t3);taskSet.add(t4);taskSet.add(t1);
//		taskSet.add(t2);taskSet.add(t);
//		for(Task tt:taskSet)
//			System.out.println(tt);
//		Collections.sort(taskSet, new Task.TaskComparator());
//		System.out.println("排序后");
//		for(Task tt:taskSet)
//			System.out.println(tt);
		
//		JedisCluster jedis = RedisTest1.getJedis();
//        jedis.set("cluster","hello world");
//        System.out.println(jedis.get("cluster"));
        
//        RedisTest e = new RedisTest();
//        e.write("cluster", "helloworld1", 4);
////        e.write("cluster1", "field1","1", 4);
////        e.write("cluster1", "field2","2", 4);
//        e.getJedis().del("cluster");
//        e.getJedis().del("cluster");
////        e.getJedis().hdel("cluster1", "field1");
//        System.out.println(e.read("cluster",4));
//        System.out.println(e.read("cluster1","field1",4));
//        System.out.println(e.read("cluster1","field2",4));
		
//		RedisN.write("ylw", "1", 1);
//		System.out.println(RedisN.read("ylw", 1));
		
//		String state = "SUCCESSFUL";
//		TaskState a = TaskState.valueOf(state);
//		if(a.equals( TaskState.SUCCESSFUL)) {
//			System.out.println("1");
//		}
//		else
//			System.out.println("2");
//		
//		state = state + "_";
//		a = TaskState.valueOf(state);
//		if(a != TaskState.SUCCESSFUL) {
//			System.out.println("1");
//		}
//		else
//			System.out.println("2");
		
//		    List<String> strings = new ArrayList<>();
//		    strings.add("a");
//		    strings.add("b");
//		    strings.add("c");
//		    strings.add("a");
//		    strings.add("d");
//		    System.out.println(strings);
//
//		    Iterator<String> iterator = strings.iterator();
//		    while (iterator.hasNext()){
//		        String next = iterator.next();
//		        if(next.equals("a")) {
////		        	Iterator<String> iterator1 = strings.iterator();
////		        	 while (iterator.hasNext()){
////		        		 String next1 = iterator.next();
////		 		        if(next1.equals("a")) 
//		 		        	iterator.remove();
////		        	 }
//		        	
//		        }
//		    }
//
//		    System.out.println(strings);
		
//		Monitoring moni = new Monitoring();
//		try {
//			moni.setHttpPort(8080);
//		} catch (IOException e) {
//			Main.log.severe("prometheus: "+e);
//			e.printStackTrace();
//		}
//		int readyTNum = 10;
//		moni.numReadyTasksChange(readyTNum);
//		moni.numReadyTasksChange(12);
////		moni.tasksScheduleTime_tic("workflow0");
//		Thread.sleep(1000);
////		moni.tasksScheduleTime_toc("workflow0");
//		int workflowNum = 100;
//		int taskNum = 2000;
//		moni.countChange("worflowNum", 100);
//		moni.countChange("taskNum", 200);
//		moni.countChange("taskNum", 2);
		
		
		
		//测试gRPC：保活信号,主要需要增加定时器，WaitSecs间隔发送一次
//		String sid = "1"; 
//		int pressure = 1;
//		int capacity = 2;
//		long serialNumber = 3; 
//		String ipv4 = "4";
//		SchedulerControlClient.send(sid, pressure, capacity, serialNumber, ipv4);
//		System.out.println(keepAliveWaitSec);		
		
//		//测试定时器：启动保活信号定时器, 并发送保活信号
//		timer.schedule(new KeepAliveTimerTask(), new Date());
//		while(true);
		
	//	//测试资源请求
	//	Resource currentResReq = new Resource(1, 1);
	//	Resource nextResReq = new Resource(2, 2);
	//	Resource allTasksResReq = new Resource(4, 4);
	//	String requestId = "1"; //资源请求id
	//	long timeStamp = 2; //时间戳
	//	ResourceRequestReceived rrd = ResourceRequestServiceClient.sendResourceRequest(requestId, timeStamp, 
	//			currentResReq, nextResReq, allTasksResReq);
	//
	//	//测试创建taskPod
	//	String workflowName = "";
	//	String taskName = "";
	//	workflowName = "workflow" + Long.toString(1);
	//	taskName = "task" + Integer.toString(1) + "_" + 1;
	//	ArrayList<String> ss = new ArrayList<String>();
	//    for(int i = 0; i<4; i++) 
	//    	ss.add("环境"+i);
	//	CreatTaskPodReceived ctpd = ResourceRequestServiceClient.creatTaskPod(
	//			workflowName, taskName, "image", 2, 3, ss);
		
	//	//测试向任务状态跟踪器注册新任务
	//	String workflowName = "workflow" + 2;
	////	List<NodeStateTaskStateTracker> nodes = new ArrayList<NodeStateTaskStateTracker>();
	//	List<TaskStateRequest.NodesState> nodesStateList = new ArrayList<TaskStateRequest.NodesState>();
	//	for(int i = 0; i<4; i++) {
	////		nodes.add(new NodeStateTaskStateTracker(i, "taskpodname" + i, "successful"));
	////		System.out.println("task" + i);
	//
	//		nodesStateList.add(TaskStateRequest.NodesState.newBuilder()
	//				.setTaskID(i).setTaskPodName("taskpodname" + i)
	//				.setTaskState("successful").build());
	//	}
	//	TaskStatusTrackerClient.registerTasksToTST(workflowName, nodesStateList);
	
//	//测试enum
//	TaskState tempT = TaskState.valueOf("FAILED");
//	if(tempT ==TaskState.FAILED) {
//		System.out.println(tempT);
//		System.out.println(tempT.toString());
//	}
		
//	    Iterator<String> iterator = strings.iterator();
//	    while (iterator.hasNext()){
//	        String next = iterator.next();
//	        if(next.equals("a")) {
////	        	Iterator<String> iterator1 = strings.iterator();
////	        	 while (iterator.hasNext()){
////	        		 String next1 = iterator.next();
////	 		        if(next1.equals("a")) 
//	 		        	iterator.remove();
////	        	 }
//	        	
//	        }
//	    }
		
//		Map<String, String> wor = new HashMap<String, String>();
//		wor.put("1", "a");wor.put("3", "c");wor.put("2", "b");wor.put("4", "d");
//		for(Map.Entry<String, String> entry : wor.entrySet()) {
//			System.out.println(entry);
//		}
//		
//		Iterator<Map.Entry<String, String>> iter = wor.entrySet().iterator();
//		while(iter.hasNext()) {
//			Map.Entry<String, String> entry = iter.next();
//			if(entry.getKey().equals("3"))
//				iter.remove();
//		}
//		for(Map.Entry<String, String> entry : wor.entrySet()) {
//			System.out.println(entry);
//		}
		
		
}

}
