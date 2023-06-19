package bit.workflowScheduler.grpc.TaskStatusTracker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStatusTrackerGrpc;
import bit.workflowScheduler.main.Main;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TaskStatusTrackerClient {
	
//	public static void main(String[] args) {
//		registerTasksToTST("1", "IP123", TaskStateRequest.NodesState.newBuilder()
//				.setTaskID(12).setTaskPodName("taskPodName").build());
//	}
	
	
	//调度器client-状态跟踪器server，即工作流任务注册：workflowID IP taskID taskPodName
	//taskPodName如果等于Error，则为通知Tracker，无需再返回该工作流中的任务状态
    /**
     * 向任务状态跟踪器注册新的任务
     * @param workflowId workflow(前缀)workflowId
     * @param Ip
     * @param node 
     * @return
     */
    public static int registerTasksToTST(String workflowId, String Ip, TaskStateRequest.NodesState node) {
        ManagedChannel channel = null;
        int accept = 0;
        try {

        	//绑定端口并连接
//            channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext(true).build();
            channel = ManagedChannelBuilder.forAddress("tracker.default", 6060).usePlaintext(true).build();
            TaskStatusTrackerGrpc.TaskStatusTrackerBlockingStub stub = TaskStatusTrackerGrpc.newBlockingStub(channel);

            //发送并得到server的回复
            TaskStateReply response = stub
                    .inputTaskState(TaskStateRequest.newBuilder()
                            .setWorkflowID(workflowId)
                            .setIP(Ip)
                            .addNodeState(node)
                            .build());

            accept = response.getAccept();
            
            if(accept >= 1) { //注册成功再累加
	            synchronized(Main.printInf) {
	            	Main.monitor.numTaskTrackerInc("sendTrackerTasks", 1);
	            	Main.NumSendTrackerTasks++;
	            }
            }
//            System.out.println("向状态跟踪器注册工作流后的返回值：" + response.getAccept());
//            Main.sendTasks2Tracker ++;
//            System.out.println("当前已发送的任务数：" + Main.sendTasks2Tracker + ", " + node.getTaskID());
            return accept;
        } catch (Exception e) {
        	Main.log.warning("向跟踪器注册任务grpc出现问题，重试几次: "+e);
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                	Main.log.warning("向跟踪器注册任务grpc关闭有问题！！！: "+e);
                    e.printStackTrace();
                }
            }
        }
        return accept;
    }
    
    
    /**
     * 向任务状态跟踪器注册新的任务
     * @param workflowId workflow(前缀)workflowId
     * @param Ip
     * @param nodesStateList 
     * @return
     */
    public static int registerTasksToTST(String workflowId, String Ip, List<TaskStateRequest.NodesState> nodesStateList) {
        ManagedChannel channel = null;
        int accept = 0;
        try {
        	
        	//绑定端口并连接
            channel = ManagedChannelBuilder.forAddress("tracker.default", 6060).usePlaintext(true).build();
            TaskStatusTrackerGrpc.TaskStatusTrackerBlockingStub stub = TaskStatusTrackerGrpc.newBlockingStub(channel);

            //发送并得到server的回复
            TaskStateReply response = stub
                    .inputTaskState(TaskStateRequest.newBuilder()
                            .setWorkflowID(workflowId)
                            .setIP(Ip)
                            .addAllNodeState(nodesStateList)
                            .build());

            accept = response.getAccept();
            
            if(accept >= 1) { //注册成功再累加
            synchronized(Main.printInf) {
            	Main.monitor.numTaskTrackerInc("sendTrackerTasks", nodesStateList.size());
            	Main.NumSendTrackerTasks += nodesStateList.size();
//            	Main.log.finest("向状态跟踪器注册批量任务个数为" + nodesStateList.size() + "其中第一个的内容为：[taskId=" 
//            			+ nodesStateList.get(0).getTaskID() + ", taskPodName="+nodesStateList.get(0).getTaskPodName()+"]");
            }
            }
//            System.out.println("向状态跟踪器注册工作流后的返回值："+response.getAccept());
            
            return accept;
        } catch (Exception e) {
        	Main.log.warning("向跟踪器注册任务grpc出现问题，重试几次: "+e);
            e.printStackTrace();
            accept = 0;
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                	Main.log.warning("向跟踪器注册任务grpc关闭有问题！！！: "+e);
                    e.printStackTrace();
                }
            }
        }
        return accept;
    }
}
