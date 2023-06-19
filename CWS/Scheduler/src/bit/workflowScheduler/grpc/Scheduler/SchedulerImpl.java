package bit.workflowScheduler.grpc.Scheduler;

import bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest;
import bit.workflowScheduler.inerface.TaskStateReceived;
import bit.workflowScheduler.main.Main;
import bit.workflowScheduler.util.DistrIdGenerator2;
import bit.workflowScheduler.util.Parameters.TaskState;
import bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply;
import bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest;
import bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply;
import bit.workflowScheduler.grpc.Scheduler.ProtoWF;
import bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply;
import bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest;
import bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerGrpc.SchedulerImplBase;
import bit.workflowScheduler.grpc.resource_allocator.ResourceRequestServiceClient;
import bit.workflowScheduler.grpc.scheduler_controller.SchedulerControllerClient;
import bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply;
import bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest;
import bit.workflowScheduler.util.WorkflowTime;

import com.google.protobuf.ByteString;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.xerial.snappy.SnappyFramedInputStream;
import org.xerial.snappy.Snappy;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SchedulerImpl extends SchedulerImplBase {

    //状态跟踪器client-调度器server，即任务状态返回：workflowID taskID taskState
  	//状态跟踪器模块有三种状态：Uncompleted、Succeed、Failed，只返回后两种
      public void inputTaskState(TaskStateRequest request, 
      		StreamObserver<TaskStateReply> responseObserver) {
      	
    	Main.log.finest("收到状态跟踪器发送的任务状态请求");  
      	String workflowId = request.getWorkflowID().substring(8); //去掉workflow前缀
      	List<TaskStateReceived> ts = new ArrayList<TaskStateReceived>();
      	int accept = 1;
      	try {
	      	for (TaskStateRequest.NodesState node : request.getNodeStateList()) {
	          	/*TaskStateRequest.NodesState转为TaskStateReceived
	          	也可以直接用第一种，但是可能不方便其他人使用*/
	      		String state = null;
	      		if(node.getTaskState().equals("Succeeded"))
	      			state = TaskState.SUCCESSFUL.toString();
	      		else if(node.getTaskState().equals("Failed"))
	      			state = TaskState.FAILED.toString();
	      		else {
	      			state = TaskState.UNEXECUTE.toString();
//	      			System.out.println("无法识别的任务状态类型");
	      			Main.log.warning("无法识别的任务状态类型");
	      		}
	          	ts.add(new TaskStateReceived(workflowId, node.getTaskID(), 
	          			state));
	      	}
      	}catch(Exception ie) {
      		Main.log.warning("接受任务状态grpc出现问题，向跟踪器返回错误代码: "+ie);
      		ie.printStackTrace();
      		accept = 0;
      	}
      	if(accept == 1) {
	  		/*需线程同步taskStateDeque的代码写在这：
	  		 * 向taskStateDeque中写入*/
	  		synchronized(Main.taskStateDeque) {
	  			for(TaskStateReceived t : ts)
	  				Main.taskStateDeque.add(t);
	  		}
	  		
	  		synchronized(Main.printInf) {
	          	Main.NumReceivedStateTasks += ts.size();
	        }
	  		Main.monitor.numTaskTrackerInc("receivedStateTasks", ts.size());
	  		
	  		Main.log.finest("接收任务状态成功");
      	}
          	
//          //打印	
//          StringBuffer greeting = new  StringBuffer();
//          greeting.append("  workflowId");
//          greeting.append(request.getWorkflowID());
//          greeting.append("  [  ");
//          int i =0;
//          for (TaskStateRequest.NodesState nodesState : request.getNodeStateList()) {
//              i++;
//              greeting.append("  taskID");
//              greeting.append(String.valueOf(i));
//              greeting.append(" : ");
//              greeting.append(nodesState.getTaskID());
////              greeting.append("  taskPodName");
////              greeting.append(String.valueOf(i));
////              greeting.append(" : ");
////              greeting.append(nodesState.getTaskPodName());
//              greeting.append("  taskState");
//              greeting.append(String.valueOf(i));
//              greeting.append(" : ");
//              greeting.append(nodesState.getTaskState());
//          }
//          greeting.append("  ]  ");
//          greeting.toString();
//          System.out.println(greeting);

          TaskStateReply reply = TaskStateReply.newBuilder()
                  .setAccept(accept)
                  .build();

          responseObserver.onNext(reply);
          responseObserver.onCompleted();
      }


    public void workflowTransfer_old(
            WorkflowTransferRequest request, StreamObserver<WorkflowTransferReply> responseObserver) {

//    	System.out.print("当前收到" + request.getWorkflowList().size() + "工作流, 准备接受");
    	
        int i = 0;
        for (ByteString workflow : request.getWorkflowList()) {
        	Main.monitor.numWorkflowInc("recievedWorkflow", 1);
        	
            i++;
//            System.out.print(" 工作流");
//            System.out.print(i);
//            System.out.print(" : ");
            byte[] data_bytes = workflow.toByteArray();
            try {
//            	//测试： 计算工作流文件的hash值
//        		Main.hashs = new BufferedWriter(new FileWriter(".\\hash.txt", true));
//        		Main.hashs.write(MD5Util.md5HashCode32(data_bytes)+"\r\n");
//        		Main.hashs.close();
            	
            	ProtoWF.Workflow gd = get_workflow(data_bytes);
            	
            	//测试
//            	Main.workflow2FileHash.put(gd, MD5Util.md5HashCode32(data_bytes));
            	
        		
            	/*
            	 * 收到工作流，处理，存入Redis的A、B库
            	 */
        		String workflowId = null;
        		byte[] workflowData = null;
                if(gd.getWorkflowName().equals("NoName")) { 
                	//调用雪花算法产生workflowId,并存储
//                	workflowId = "2008131628272740010001";
                	workflowId = DistrIdGenerator2.createDistrId();
                	//测试
//                	workflowId = Main.fileHash2Id.get(Main.workflow2FileHash.get(file)).toString();
                	
                	//将workflowId写入gd的副本，把DAG序列化成byte[ ] ,再一步变成byte[]就可以放入redis A库
                	workflowData = ProtoWF.Workflow.newBuilder(gd).setWorkflowName(workflowId).build().toByteArray();
//                	System.out.println("修改前：" + file.getWorkflowName() + ", " + file.getTopologyCount() + ", " + file.getTopologyList());
//                	ProtoWF.Workflow file1 = ProtoWF.Workflow.newBuilder(file).setWorkflowName(workflowId).build();
////                	ProtoWF.Workflow file1 = ProtoWF.Workflow.newBuilder(file).build();
//                	System.out.println(file1.equals(file));
                	
//                	//写入Redis的B库
//                	List<String> tasksState = new ArrayList<String>();
//            		int taskNum = gd.getTopologyCount() + Main.offsetTaskState;
//    				for(int j = 0; j < taskNum; j++) 
//    					tasksState.add(TaskState.UNEXECUTE.toString());
//    				Redis.write(Main.getWorkflowNameB(workflowId), tasksState, 1);	
                	
                	//写入Redis的B库, 此时只写入工作流状态
//                	Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, "UNEXECUTE", 1);	
                	Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, "UNEXECUTE", 1);
                }
                else 
                	workflowData = gd.toByteArray();
//                Main.redis.write(Main.getWorkflowNameA(workflowId), workflowData.toString(), 1); //写入A库
                Main.redis.write(Main.getWorkflowNameA(workflowId), workflowData.toString(), 1); //写入A库
                                
                
            	/*需线程同步workflowFileDeque的代码写在这：
        		 * 向workflowFileDeque中写入*/
        		synchronized(Main.workflowFileDeque) {
        			Main.workflowFileDeque.put(workflowId, gd);
        			
//        			//测试
//        			Workflow workflow1 = new Workflow();
//        			ParseWorflowByProtoWF.parse(gd, workflow1);
        		}
        		synchronized(Main.printInf) {
        			Main.NumReceivedWorkflows ++;
        			Main.NumReceivedTasks += gd.getTopologyCount();
        			Main.NumTasks++;
        		}
        		
            	
//                System.out.print(gd.toString());
//                System.out.println();
            }catch(IOException ie) {
            	Main.log.severe("grpc: "+ie);
                ie.printStackTrace();
            }
        }
//        System.out.println("本次收到的工作流数量：" + i);
//        Main.log.info("本次收到的工作流数量：" + i);

        int accept = 1;

        WorkflowTransferReply reply = WorkflowTransferReply.newBuilder()
                .setAccept(accept)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
    
  //先接受完再处理、存入队列、写入Redis
    public void workflowTransfer_old1(
            WorkflowTransferRequest request, StreamObserver<WorkflowTransferReply> responseObserver) {

    	int accept = 1;
        ArrayList<ProtoWF.Workflow> recievedWorkflows = new ArrayList<ProtoWF.Workflow>();
        try {
	        for (ByteString workflow : request.getWorkflowList()) {
	            byte[] data_bytes = workflow.toByteArray();
	            ProtoWF.Workflow gd = get_workflow(data_bytes);
	            recievedWorkflows.add(gd);
	        }
        }catch(IOException | Error ie) {
        	Main.log.severe("接受工作流grpc: "+ie);
            ie.printStackTrace();
            accept = 0;
        }
        
        if(accept == 1) { //接受完成，没有问题
//        	Main.log.info("本次收到的任务工作流数"+recievedWorkflows.size());
        	for(ProtoWF.Workflow gd : recievedWorkflows) {
	        	Main.monitor.numWorkflowInc("recievedWorkflow", 1);
	        	/*
	        	 * 收到工作流，处理，存入Redis的A、B库
	        	 */
	    		String workflowId = null;
	    		byte[] workflowData = null;
	            if(gd.getWorkflowName().equals("NoName")) { 
	            	//调用雪花算法产生workflowId,并存储
	//            	workflowId = "2008131628272740010001";
	            	workflowId = DistrIdGenerator2.createDistrId();
	            	//测试
	//            	workflowId = Main.fileHash2Id.get(Main.workflow2FileHash.get(file)).toString();
	            	
	            	//将workflowId写入gd的副本，把DAG序列化成byte[ ] ,再一步变成byte[]就可以放入redis A库
	            	workflowData = ProtoWF.Workflow.newBuilder(gd).setWorkflowName(workflowId).build().toByteArray();
	//            	System.out.println("修改前：" + file.getWorkflowName() + ", " + file.getTopologyCount() + ", " + file.getTopologyList());
	//            	ProtoWF.Workflow file1 = ProtoWF.Workflow.newBuilder(file).setWorkflowName(workflowId).build();
	////            	ProtoWF.Workflow file1 = ProtoWF.Workflow.newBuilder(file).build();
	//            	System.out.println(file1.equals(file));
	            	
	//            	//写入Redis的B库
	//            	List<String> tasksState = new ArrayList<String>();
	//        		int taskNum = gd.getTopologyCount() + Main.offsetTaskState;
	//				for(int j = 0; j < taskNum; j++) 
	//					tasksState.add(TaskState.UNEXECUTE.toString());
	//				Redis.write(Main.getWorkflowNameB(workflowId), tasksState, 1);	
	            	
	            	//写入Redis的B库, 此时只写入工作流状态
//	            	Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, "UNEXECUTE", 1);	
	            	Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, "UNEXECUTE", 1);
	            }
	            else 
	            	workflowData = gd.toByteArray();
//	            Main.redis.write(Main.getWorkflowNameA(workflowId), workflowData.toString(), 1); //写入A库
	            Main.redis.write(Main.getWorkflowNameA(workflowId), workflowData.toString(), 1); //写入A库
	            
	            //写入<customerId, workflowId>到Redis C库
	            Main.redis.write(gd.getCustomId(), gd.getWorkflowName(), 1);
	                            
	            /*需线程同步workflowFileDeque的代码写在这：
	    		 * 向workflowFileDeque中写入*/
	    		synchronized(Main.workflowFileDeque) {
	    			Main.workflowFileDeque.put(workflowId, gd);
	    			
	//    			//测试
	//    			Workflow workflow1 = new Workflow();
	//    			ParseWorflowByProtoWF.parse(gd, workflow1);
	    		}
	    		synchronized(Main.printInf) {
	    			Main.monitor.numTaskInc("recievedTasks", gd.getTopologyCount());
	    			
	    			Main.NumReceivedWorkflows ++;
	    			Main.NumReceivedTasks += gd.getTopologyCount();
	    			Main.NumTasks += gd.getTopologyCount();
	    		}
        	}
	            	
        	double aa =0;
        	for(ProtoWF.Workflow gd : recievedWorkflows) {
        		aa+=gd.getTopologyCount();
        	}
        	Main.log.info("本次收到的任务数量"+aa);
        }

        recievedWorkflows = null;

        WorkflowTransferReply reply = WorkflowTransferReply.newBuilder()
                .setAccept(accept)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
    //先接受完再处理、存入队列、写入Redis
    public void workflowTransfer(
            WorkflowTransferRequest request, StreamObserver<WorkflowTransferReply> responseObserver) {

    	// ctx has all the values as the current context, but
    	// won't be cancelled
    	Context ctx = Context.current().fork();
    	
    	int accept = 1;
        ArrayList<ProtoWF.Workflow> recievedWorkflows = new ArrayList<ProtoWF.Workflow>();
        try {
	        for (ByteString workflow : request.getWorkflowList()) {
	            byte[] data_bytes = workflow.toByteArray(); 
	            ProtoWF.Workflow gd = get_workflow(data_bytes);
	            recievedWorkflows.add(gd);
	        }
        }catch(IOException | Error ie) {
        	Main.log.warning("接受工作流grpc出现问题，向控制器返回错误代码: "+ie);
            ie.printStackTrace();
            accept = 0;
        }
        
//        boolean failed = false;
        if(accept == 1) { //接受完成，没有问题
//        	Main.log.info("本次收到的任务工作流数"+recievedWorkflows.size());
        	int currentRecievedTasks = 0;
        	ArrayList<ByteString> refusedWorkflows = new ArrayList<ByteString>();
        	int i = 0;
        	for(ProtoWF.Workflow gd : recievedWorkflows) {
	        	
	        	/*
	        	 * 收到工作流，处理，存入Redis的A、B库
	        	 */
	    		String workflowId = null;
	    		byte[] workflowData = null;
//	    		ByteString workflowData = null;
	            if(gd.getWorkflowName().equals("NoName")) { 
	            	//调用雪花算法产生workflowId,并存储
	//            	workflowId = "2008131628272740010001";
	            	Main.log.finest("为工作流指定唯一的ID");
	            	workflowId = DistrIdGenerator2.createDistrId();
	            	Main.log.finest("为工作流指定唯一的ID成功");
	            	//测试
	//            	workflowId = Main.fileHash2Id.get(Main.workflow2FileHash.get(file)).toString();
	            	
	            	//将workflowId写入gd的副本，把DAG序列化成byte[ ] ,再一步变成byte[]就可以放入redis A库
	            
//	            	workflowData = ProtoWF.Workflow.newBuilder(gd).setWorkflowName(workflowId).build().toByteArray();
	            	workflowData = ProtoWF.Workflow.newBuilder(gd).setWorkflowName(workflowId).build().toByteArray(); //20210625ylw
	//            	System.out.println("修改前：" + file.getWorkflowName() + ", " + file.getTopologyCount() + ", " + file.getTopologyList());
	//            	ProtoWF.Workflow file1 = ProtoWF.Workflow.newBuilder(file).setWorkflowName(workflowId).build();
	////            	ProtoWF.Workflow file1 = ProtoWF.Workflow.newBuilder(file).build();
	//            	System.out.println(file1.equals(file));
	            	
	//            	//写入Redis的B库
	//            	List<String> tasksState = new ArrayList<String>();
	//        		int taskNum = gd.getTopologyCount() + Main.offsetTaskState;
	//				for(int j = 0; j < taskNum; j++) 
	//					tasksState.add(TaskState.UNEXECUTE.toString());
	//				Redis.write(Main.getWorkflowNameB(workflowId), tasksState, 1);	
	            	
	            	//写入Redis的B库, 此时只写入工作流状态
//	            	Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, "UNEXECUTE", 1);	
	            	Main.redis.write(Main.getWorkflowNameB(workflowId), Main.workflowStateField, "UNEXECUTE", 1);
	            }
	            else 
//	            	workflowData = gd.toByteArray();
	            	workflowData = gd.toByteArray();
	            
//	            try {
//	            Main.redis.write(Main.getWorkflowNameA(workflowId), workflowData.toString(), 1); //写入A库
	            Main.redis.write(Main.getWorkflowNameA(workflowId), workflowData, 1); //写入A库
	            
	            //写入<customerId, workflowId>到Redis C库
	            Main.redis.write(gd.getCustomId(), workflowId, 1);
	            Main.log.info(gd.getCustomId() + ", " + workflowId);
//	            }catch(Exception e) {
//	            	Main.log.warning("接收的工作流写入redis失败，返回失败的工作流给控制器，再正常退出" + e);
//	            	e.printStackTrace();
//	            	failed = true;
//	            	refusedWorkflows.add(request.getWorkflowList().get(i));
//	            	continue;
//	            }
	                            
	            
	            currentRecievedTasks += gd.getTopologyCount();
	            if(Main.capacity == 0 || currentRecievedTasks <= Main.capacity) { //没有超过接受的容量
            		//推送收到工作流的时间
//	            	if(Main.pushgateway != null)
//	            		Main.monitor.pushMetrics(Main.pushgateway, "receive workflow "+workflowId, new Date().getTime());
	            	Main.monitor.executionTimeSet("receive workflow "+workflowId, new Date().getTime());
	            	if(gd.getCustomization()) { //存储收到定制工作流的时间
	            		WorkflowTime wt = new WorkflowTime(workflowId, true, gd.getTimeGrade(), gd.getCostGrade());
	            		wt.startTime = new Date().getTime();
	            		Main.customizationWorkflowRuntimes.put(workflowId, wt);
	            	}
	            	
	            	/*需线程同步workflowFileDeque的代码写在这：
    	    		 * 向workflowFileDeque中写入*/
    	    		synchronized(Main.workflowFileDeque) {
    	    			Main.workflowFileDeque.put(workflowId, gd);
    	    			Main.log.finest("从控制器收到工作流" + workflowId);
    	    			
    	//    			//测试
    	//    			Workflow workflow1 = new Workflow();
    	//    			ParseWorflowByProtoWF.parse(gd, workflow1);
    	    		}
    	    		synchronized(Main.printInf) {
    	    			Main.monitor.numTaskInc("recievedTasks", gd.getTopologyCount());
    	    			Main.monitor.numWorkflowInc("recievedWorkflows", 1);
    	    			Main.NumReceivedWorkflows ++;
    	    			Main.NumReceivedTasks += gd.getTopologyCount();
    	    			Main.NumTasks += gd.getTopologyCount();
    	    		}
//    	    		Main.log.finest("工作流已全部接收");
            	}
            	else { //超过接受的容量, 暂时存放，准备返回给控制器
            		refusedWorkflows.add(request.getWorkflowList().get(i));
//            		Main.log.finest("超过调度器的容量，将工作流返回给控制器");
            	}
	            i++;
            }
        	// Set ctx as the current context within the Runnable
        	ctx.run(() -> {
        	    // Can start asynchronous work here that will not
        	    // be cancelled when myRpcMethod returns
        		if(!refusedWorkflows.isEmpty()) {
            		int result = 0;
//            		Main.log.finest("将工作流返回给控制器");
            		while(result == 0)
            			result = SchedulerControllerClient.inputWorkflow(refusedWorkflows);
            		if(!Main.CeShi)
            			Main.log.info("向控制器返回工作流数量："+refusedWorkflows.size());
            		Main.log.finest("已将工作流返回给控制器, 数量"+refusedWorkflows.size());
            	}
        	});
        		
        }
        

        recievedWorkflows = null;
        Main.log.finest("从控制器接收工作流成功，返回状态码"+accept);

        WorkflowTransferReply reply = WorkflowTransferReply.newBuilder()
                .setAccept(accept)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
    
    public void deleteWorkflow(
    		DeleteWorkflowRequest request, StreamObserver<DeleteWorkflowReply> responseObserver) {

    	int accept = 1;
    	Main.log.info("收到控制器删除工作流请求");
    	String workflowId = request.getWorkflowId();
    	synchronized(Main.deleteWorkflows) {
    		Main.deleteWorkflows.add(workflowId);
	    	int deleteWorkflowNamespaceState = 0;
	    	Main.log.finest("向资源分配器请求在后台删除工作流对应的命名空间workflowNamespace: workflow" + workflowId);
			while(deleteWorkflowNamespaceState != 1) { //直到删除成功才跳出循环
				deleteWorkflowNamespaceState = ResourceRequestServiceClient.deleteWorkflowNamespace("workflow" + workflowId);
			}
			Main.log.finest("后台删除workflowNamespace成功： " + "workflow" + workflowId);
			try {
				Main.redis.delete(Main.getWorkflowNameA(workflowId), 1); //删除A库
				Main.redis.delete(Main.getWorkflowNameB(workflowId), 1); //删除B库
				Main.redis.delete(request.getCustomId(), 1); //删除C库
			}catch(Exception ie) {
	        	Main.log.warning("删workflowId"+ workflowId+ "在redis中的记录出现问题, 返回错误代码："+ie);
	            ie.printStackTrace();
	            accept = 0;
			}
    	}
		if(accept == 1)
			Main.log.info("收到控制器请求后，删除工作流成功workflow" + workflowId);
		else
			Main.log.info("收到控制器请求后，删除工作流失败workflow" + workflowId);

		DeleteWorkflowReply reply = DeleteWorkflowReply.newBuilder()
                .setResult(accept)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public ProtoWF.Workflow get_workflow_old(byte[] data_bytes) throws IOException{
        SnappyFramedInputStream sis = new SnappyFramedInputStream(new ByteArrayInputStream(data_bytes));
        byte[] buffer = new byte[1024 * 1024 * 8];
        int len = 0;
        String data_uncompressed = "";
        while ((len = sis.read(buffer)) != -1) {
            data_uncompressed = new String(buffer, 0, len, "ISO-8859-1");
        }
        ProtoWF.Workflow gd = null;
        gd = ProtoWF.Workflow.parseFrom(data_uncompressed.getBytes( "ISO-8859-1"));
        return gd;
    }

	public ProtoWF.Workflow get_workflow(byte[] data_bytes) throws IOException{
		SnappyFramedInputStream sis = new SnappyFramedInputStream(new ByteArrayInputStream(data_bytes));
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		sis.transferTo(os);
        return ProtoWF.Workflow.parseFrom(os.toByteArray());
    }
}