package bit.workflowScheduler.grpc.scheduler_controller;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest;
import bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply;
import bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest;
import bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStatusTrackerGrpc;
import bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply;
import bit.workflowScheduler.grpc.scheduler_controller.gencode.SchedulerControllerGrpc;
import bit.workflowScheduler.main.Main;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.*;

public class SchedulerControllerClient {
//    public static void main(String[] args) {
//        SchedulerControllerClient test = new SchedulerControllerClient();
////        test.keepAlive();
//        test.inputWorlkflower();
//    }
	public static int inputWorkflow(List<ByteString> workflows) {
		ManagedChannel channel = null;
        int accept = 0;
        boolean except = false;
        int tryTimes = 5;
		while(tryTimes > 0) {
	        try {
	            channel = ManagedChannelBuilder.forAddress(Main.grpcSCServer, Main.grpcSCPort).usePlaintext(true).build();
	//            channel = ManagedChannelBuilder.forAddress("scheduler-controller-service.default", 6060).usePlaintext(true).build();
	
	            SchedulerControllerGrpc.SchedulerControllerBlockingStub stub = SchedulerControllerGrpc.newBlockingStub(channel);

	            InputWorkflowRequest request = InputWorkflowRequest.newBuilder().addAllWorkflow(workflows).build();
	            InputWorkflowReply workflowTransferResponse = stub
	                    .inputWorkflow(request);
	            accept = workflowTransferResponse.getAccept();
	            
	            return accept;
	        } 
	        catch (Exception e) {
	        	Main.log.warning("返给控制器工作流grpc出现问题，将重试几次: "+e);
	            e.printStackTrace();
	            except = true;
	        } finally {
	            if (channel != null) {
	                try {
	                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	                } catch (Exception e) {
	                	Main.log.warning("返给控制器工作流grpc关闭有问题！！！: "+e);
	                    e.printStackTrace();
	                }
	            }
	        }
	        tryTimes--;
	        if(!except) 
	        	break;
		}
		if(except) {
			Main.log.warning("向控制器返回拒绝的工作流grpc异常，重试后还是失败，正常退出");
			System.exit(0);
		}
        return accept;
    }
    

	/**
	 * @param sid	调度器id
	 * @param pressure	调度器自测压力值（0~100，整数）
	 * @param capacity	调度器承载力（还可以承载/接收多少工作流，单位：个，整数）
	 * @param serial_number	保活序号（单调增，整数，从1开始）
	 * @param ipv4	ipv4地址(ip:port)
	 * @param clusterId	调度器pod所在集群的id
	 * @return 下次发送保活信号的间隔
	 */
    public static int sendkeepAlive(String sid, int pressure, int capacity, long serialNumber, String ipv4, String clusterId) {
        ManagedChannel channel = null;
        int keepAliveWaitSec = 0;
        boolean except = false;
        int tryTimes = 0;
		while(tryTimes < 5) {
	        try {
	            channel = ManagedChannelBuilder.forAddress(Main.grpcSCServer, Main.grpcSCPort).usePlaintext(true).build();
	//            channel = ManagedChannelBuilder.forAddress("scheduler-controller-service.default", 6060).usePlaintext(true).build();
	
	            SchedulerControllerGrpc.SchedulerControllerBlockingStub stub = SchedulerControllerGrpc.newBlockingStub(channel);
	
	            KeepAliveReply keepAliveResponse = stub
	                    .keepAlive(KeepAliveRequest.newBuilder().setSid(sid).setPressure(pressure).setCapacity(capacity).setSerialNumber(serialNumber).setIpv4(ipv4).setClusterId(clusterId).build());
	            
	            keepAliveWaitSec = keepAliveResponse.getWaitSecs();
	//            System.out.println("返回的下次发送保活信号间隔：" + keepAliveWaitSec);
	//            Log.info("返回的下次发送保活信号间隔：" + keepAliveWaitSec);
	            
	            return keepAliveWaitSec;
	        } 
	        catch (Exception e) {
	        	Main.log.warning("发送保活信号grpc出现问题，将重试几次: "+e);
	            e.printStackTrace();
	            except = true;
	        } finally {
	            if (channel != null) {
	                try {
	                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	                } catch (Exception e) {
	                	Main.log.warning("发送保活信号grpc关闭有问题！！！: "+e);
	                    e.printStackTrace();
	                }
	            }
	        }
	        if(!except) 
	        	break;
	        tryTimes++;
	        try {
				Thread.currentThread().sleep(1000*tryTimes);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//毫秒
		}
		if(except) {
			Main.log.warning("向控制器发送保活信号grpc存在异常，重试后还是失败，正常退出");
			System.exit(0);
		}
        return keepAliveWaitSec;
    }

    public byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

}
