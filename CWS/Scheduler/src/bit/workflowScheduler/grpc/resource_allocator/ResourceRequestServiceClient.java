package bit.workflowScheduler.grpc.resource_allocator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest;
import bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse;
import bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest;
import bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse;
import bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo;
import bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest;
import bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequestServiceGrpc;
import bit.workflowScheduler.inerface.CreatTaskPodReceived;
import bit.workflowScheduler.inerface.ResourceRequestReceived;
import bit.workflowScheduler.main.Main;
import bit.workflowScheduler.problem.Resource;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ResourceRequestServiceClient {  
	
//    /**
//	 * 向资源分配器发送调度器Id
//	 * @param schedulerId
//	 * @param aliveStatus
//	 * @return CreatTaskPodReceived与创建taskPod返回信息格式相同
//	 */
/*    public static CreatTaskPodReceived sendSchedulerId(String schedulerId, boolean aliveStatus) {
        ManagedChannel channel = null;
        CreatTaskPodReceived re = new CreatTaskPodReceived();
        try {
        	
            channel = ManagedChannelBuilder.forAddress("localhost", 50050).usePlaintext(true).build();
            ResourceRequestServiceGrpc.ResourceRequestServiceBlockingStub stub = ResourceRequestServiceGrpc.newBlockingStub(channel);

            UpdateSchedulerAliveStatusResponse response = stub
            		.updateSchedulerAliveStatus(UpdateSchedulerAliveStatusRequest.newBuilder()
                    		.setSchedulerId(schedulerId)
            				.setAliveStatus(aliveStatus)
                            .build());

            re.setResult(response.getResult());
            re.setErrNo(response.getErrNo());
            
            String greeting = new StringBuilder()
                    .append("   result ")
                    .append(response.getResult())
                    .append("   err_no ")
                    .append(response.getErrNo())
                    .toString();
            System.out.println(greeting);
            
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return re;
    }*/
    
	
	/**
     * 向资源分配器请求创建TaskPod
     * @param workflowId	workflow的ID,pod的namespace
     * @param taskName	pod的名字，task(前缀)+taskId+"-workflowId-失败次数"
     * @param image	任务镜像
     * @param cpu
     * @param mem
     * @param env	需要输入给 POD 的环境变量
     * @return CreatTaskPodReceive
     */
    public static int deleteWorkflowNamespace(String workflowId) {
        ManagedChannel channel = null;
        int state = 0;
        try {
        	
//            channel = ManagedChannelBuilder.forAddress("localhost", 50054).usePlaintext(true).build();
            channel = ManagedChannelBuilder.forAddress("grpc-resource-allocator.default", 6060).usePlaintext(true).build();
            ResourceRequestServiceGrpc.ResourceRequestServiceBlockingStub stub = ResourceRequestServiceGrpc.newBlockingStub(channel);          
            
            DeleteWorkflowNamespaceResponse response = stub
            		.deleteWorkflowNamespace(DeleteWorkflowNamespaceRequest.newBuilder()
                    		.setWorkflowId(workflowId)
                            .build());
            
            state = response.getResult();
            
            return state;
        } catch (Exception e) {
        	Main.log.warning("向资源分配器申请删除工作流grpc, 将重试直到成功: "+e);
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                	Main.log.severe("！！！向资源分配器申请删除工作流grpc关闭有问题: "+e);
                    e.printStackTrace();
                }
            }
        }
        return state;
    }
    
    /**
     * 向资源分配器请求创建TaskPod
     * @param workflowId	workflow的ID,pod的namespace
     * @param taskName	pod的名字，task(前缀)+taskId+"-workflowId-失败次数"
     * @param image	任务镜像
     * @param cpu
     * @param mem
     * @param env	需要输入给 POD 的环境变量
     * @return CreatTaskPodReceive
     */
    public static CreatTaskPodReceived creatTaskPod(String workflowId, String taskName, String image, 
    		long cpu, long mem, Map<String, String> env, List<String> inFiles, List<String> outFiles, boolean customization, boolean cost_grade) {
        ManagedChannel channel = null;
        CreatTaskPodReceived re = new CreatTaskPodReceived();
        try {
        	Main.log.finest("向资源分配器请求pod函数调用creatTaskPod，进入");
//            channel = ManagedChannelBuilder.forAddress("localhost", 50054).usePlaintext(true).build();
            channel = ManagedChannelBuilder.forAddress("grpc-resource-allocator.default", 6060).usePlaintext(true).build();
            ResourceRequestServiceGrpc.ResourceRequestServiceBlockingStub stub = ResourceRequestServiceGrpc.newBlockingStub(channel);  

//            CreateTaskPodRequest.Builder b = CreateTaskPodRequest.newBuilder();
//            b.setWorkflowId("0")
//    		.setTaskName("1_1")
//            .setImage("1")
//            .setCpu(2)
//            .setMem(3);
//            //通过for循环添加repeated对象
//            for(int i = 0; i<4; i++) 
//            	b.addEnv("环境"+i);
//            //通过list添加repeated对象
//            ArrayList<String> ss = new ArrayList<String>();
//            for(int i = 0; i<4; i++) 
//            	ss.add("环境"+i);
//            b.addAllEnv(ss); 
//            
//            CreateTaskPodResponse createTaskPodResponse = stub
//            		.createTaskPod(b.build());
            Main.log.finest("向资源分配器请求pod函数调用creatTaskPod，等待资源分配器返回状态");
            CreateTaskPodResponse createTaskPodResponse = stub
            		.createTaskPod(CreateTaskPodRequest.newBuilder()
                    		.setWorkflowId(workflowId)
                    		.setTaskName(taskName)
                            .setImage(image)
                            .setCpu(cpu)
                            .setMem(mem)
                            .putAllEnv(env)
                            .addAllInputVector(inFiles)
                            .addAllOutputVector(outFiles)
                            .setCustomization(customization)
                            .setCostGrade(cost_grade)
                            .build());
            Main.log.finest("向资源分配器请求pod函数调用creatTaskPod，状态已返回");
            
            re.setResult(createTaskPodResponse.getResult());
            re.setErrNo(createTaskPodResponse.getErrNo());

//            System.out.println("请求创建pod");
//            String greeting = new StringBuilder()
//                    .append("   result ")
//                    .append(createTaskPodResponse.getResult())
//                    .append("   err_no ")
//                    .append(createTaskPodResponse.getErrNo())
//                    .toString();
//            System.out.println(greeting);
            
            return re;
        } catch (Exception e) {
        	Main.log.warning("向资源分配器请求创建任务Podgrpc出现问题，将重试几次: "+e);
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                	Main.log.severe("！！！向资源分配器请求创建任务Podgrpc关闭有问题:"+e);
                    e.printStackTrace();
                }
            }
        }
        return re;
    }
 
    /**
     * 向资源分配器请求资源
     * @param schedulerId	调度器id
     * @param timeStamp	时间戳
     * @param currentResReq	当前批次ready_tasks资源请求量
     * @param nextResReq	下一批次ready_tasks资源请求量
     * @param allTasksResReq	调度器所有任务资源请求总量
     * @return ResourceRequestReceived 请求到的资源
     */
    public static ResourceRequestReceived sendResourceRequest(String schedulerId, long timeStamp, Resource currentResReq, 
    		Resource nextResReq, Resource allTasksResReq, boolean customization, boolean cost_grade) {
        ManagedChannel channel = null;
        ResourceRequestReceived re = new ResourceRequestReceived();
        try {
        	
        	//绑定连接端口
            channel = ManagedChannelBuilder.forAddress("grpc-resource-allocator.default", 6060).usePlaintext(true).build();   
            ResourceRequestServiceGrpc.ResourceRequestServiceBlockingStub stub = ResourceRequestServiceGrpc.newBlockingStub(channel);

            //client端发送并得到server端的回复
            ResourceAllocateInfo response = stub
                    .getResourceAllocateInfo(ResourceRequest.newBuilder()
                            .setSchedulerId(schedulerId)
                            .setTimeStamp(timeStamp)
                            .setCustomization(customization)
                            .setCostGrade(cost_grade)
                            .setCurrentRequest(ResourceRequest.resourceDemand.newBuilder().setCpu(currentResReq.getCPU()).setMem(currentResReq.getMemory()).build())
                            .setNextRequest(ResourceRequest.resourceDemand.newBuilder().setCpu(nextResReq.getCPU()).setMem(nextResReq.getMemory()).build())
                            .setAllTasksRequest(ResourceRequest.resourceDemand.newBuilder().setCpu(allTasksResReq.getCPU()).setMem(allTasksResReq.getMemory()).build())
                            .build());
            
            //对server端回复的信息进行处理
            Resource r = new Resource(response.getCurrentRequest().getCpu(), 
            		response.getCurrentRequest().getMem());
//            Main.resourceRequestReceived
            re = new ResourceRequestReceived(response.getSchedulerId(),
            		r, response.getCurrentRequestStatus());

//            String greeting = new StringBuilder()
//            		.append("请求的资源： ")
//                    .append("   schedulerId ")
//                    .append(response.getSchedulerId())
//                    .append("   currentRequest:cpu ")
//                    .append(response.getCurrentRequest().getCpu())
//                    .append("   currentRequest:mem ")
//                    .append(response.getCurrentRequest().getMem())
//                    .append("   currentRequestStatus ")
//                    .append(response.getCurrentRequestStatus())
//                    .toString();
//            System.out.println(greeting);
            
            //返回server端的回复信息
            return re;
        } catch (Exception e) {
        	Main.log.warning("向资源分配器请求资源grpc出现问题，不重试，等下次循环: "+e);
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                	Main.log.severe("！！！向资源分配器请求资源grpc关闭有问题！！！: "+e);
                    e.printStackTrace();
                }
            }
        }      
        //返回server端的回复信息
        return re;
    }
}
