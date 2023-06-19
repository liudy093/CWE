package bit.workflowScheduler.main;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.TimerTask;

import bit.workflowScheduler.grpc.scheduler_controller.SchedulerControllerClient;
import bit.workflowScheduler.inerface.CreatTaskPodReceived;


public class PushPromethus extends TimerTask {

	@Override
	public void run() {	
		Main.monitor.timelyPushMetrics(Main.pushgateway, Main.schedulerId+"timelyPushMetrics");
		Main.monitor.pushMetrics(Main.pushgateway, Main.schedulerId+"pushMetrics");
		Main.timer.schedule(new PushPromethus(), new Date(new Date().getTime() + 30*1000)); //每隔半min推送一次
	}

}
