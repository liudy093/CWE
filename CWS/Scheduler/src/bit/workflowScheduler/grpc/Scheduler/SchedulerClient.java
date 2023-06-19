package bit.workflowScheduler.grpc.Scheduler;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply;
import bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest;
import bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply;
import bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest ;
import bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerGrpc;

import io.grpc.Grpc;
import org.xerial.snappy.SnappyFramedInputStream;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.*;

public class SchedulerClient {
    public static void main(String[] args) {
        SchedulerClient test = new SchedulerClient();
        test.registerTasksToTST();
//        test.workflowTransfer();
    }

    public void registerTasksToTST() {
        ManagedChannel channel = null;
        int accept = 0;
        try {

            channel = ManagedChannelBuilder.forAddress("tracker", 6060).usePlaintext(true).build();
            SchedulerGrpc.SchedulerBlockingStub stub = SchedulerGrpc.newBlockingStub(channel);

            String workflowId = "3120190902";
            String Ip = "1007";
            TaskStateRequest.NodesState node = TaskStateRequest.NodesState.newBuilder().setTaskID(1).setTaskPodName("Pod 1").setTaskState("Pending").build();
            //发送并得到server的回复
            TaskStateReply response = stub
                    .inputTaskState(TaskStateRequest.newBuilder()
                            .setWorkflowID(workflowId)
                            .setIP(Ip)
                            .addNodeState(node)
                            .build());

            accept = response.getAccept();

            System.out.println("向状态跟踪器注册工作流后的返回值：" + response.getAccept());

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
    }

    public void workflowTransfer(){
        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forAddress("localhost", 6060).usePlaintext(true).build();

            SchedulerGrpc.SchedulerBlockingStub stub = SchedulerGrpc.newBlockingStub(channel);

            String sid = "sid Who knows what this is for";
            List<byte[]> data_list = new ArrayList();
            List<ByteString> data_bytestring_list = new ArrayList();
            for(int i = 0; i < 2; i++ ) {
                String fileName = "./TestData/" + i + ".data.snappy";
                byte[] workflow_bytes = getContent(fileName);
                data_list.add(workflow_bytes);
            }
            for (byte[] data_bytes : data_list){
                ByteString data_bytestring = com.google.protobuf.ByteString.copyFrom(data_bytes);
                data_bytestring_list.add(data_bytestring);
            }


            WorkflowTransferRequest request = WorkflowTransferRequest.newBuilder().setSid(sid).addAllWorkflow(data_bytestring_list).build();
            WorkflowTransferReply workflowTransferResponse = stub
                    .workflowTransfer(request);

            System.out.println(workflowTransferResponse.getAccept());
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