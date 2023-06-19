package bit.workflowScheduler.grpc.Scheduler;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.lang.Thread;

import bit.workflowScheduler.main.Main;

public class SchedulerServer {
    public static void setup() {
        try {

            int port = 6060;
            final Server server = ServerBuilder.forPort(port)
                    .addService(new SchedulerImpl())
                    .maxInboundMessageSize(524288000)
                    .build()
                    .start();

            Main.grpcServerStartUpFlag = true;
//            System.out.println("1SchedulerServer started, listening on " + port);
            server.awaitTermination();
//            System.out.println("2SchedulerServer started, listening on " + port);

        } catch (Exception e) {
        	Main.log.warning("监听grpc出现问题，正常退出: "+e);
            e.printStackTrace();
            System.exit(0);
        }
    }
}
