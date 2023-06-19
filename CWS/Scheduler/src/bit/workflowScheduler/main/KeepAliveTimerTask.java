package bit.workflowScheduler.main;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.TimerTask;

import bit.workflowScheduler.grpc.scheduler_controller.SchedulerControllerClient;
import bit.workflowScheduler.inerface.CreatTaskPodReceived;


/**
 * 保活信号定时器，WaitSecs间隔发送一次
 * @author YangLiwen
 * @version date：2020年7月17日  上午11:09:20
 *
 */
public class KeepAliveTimerTask extends TimerTask {
	public static OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
	public static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean(); //容器的内存

	@Override
	public void run() {	
		//评估调度器性能，得到下面五个参数，组成保活信号
//		String sid = "1"; 
		int pressure = 1; //当前调度器CPU压力
		int capacity = 100; //当前还可容纳的工作流数量
//		long serialNumber = 3; 
//		String ipv4 = "4";
		
		//打印内存使用情况
//		Main.log.info("heapMemoryUsage: "+KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()/1024/1024
//				+"\r\n"+"nonHeapMemoryUsage: "+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getUsed()/1024/1024
//				+"\r\n"+"堆内存利用率"+ KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()*1.0/KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getMax()*100+"%"
//				+"\r\n"+"内存利用率" + (KeepAliveTimerTask.memoryMXBean.getHeapMemoryUsage().getUsed()+KeepAliveTimerTask.memoryMXBean.getNonHeapMemoryUsage().getUsed())/1024/1024*1.0/3072*100+"%"
//				+ "\r\n\t" + "收到的任务的数量：" + Main.NumReceivedTasks
//				);
		
		//获取当前CPU使用情况
//		OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
		double cpu = osMxBean.getSystemLoadAverage();
//		System.out.println("当前CPU使用率：" + cpu);
		pressure = (int)Math.ceil(cpu);
		if(pressure < 0)
			pressure = 0;
		else if(pressure > 100)
			pressure = 100;
			
		int lastMainCapacity = Main.capacity;
		//获取堆内存使用情况
//		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	    MemoryUsage usage = memoryMXBean.getHeapMemoryUsage();
	    long max = usage.getMax()/1024/1024; //Mb
	    long used = usage.getUsed()/1024/1024; //Mb
	    //获取系统中当前工作流的数量以及任务的数量
	    long numWorkflows = Main.NumReceivedWorkflows-Main.NumSuccessfulWorkflows-Main.NumFailedWorkflows; //系统中目前存在的工作流数量
		if(Main.maxWorkflowNum == 0) {
		    if(used > 0.8*max) { //余下部分内存
				capacity = 0;
				Main.maxWorkflowNum = numWorkflows;
			}
			else if(numWorkflows != 0 && Main.NumTasks != 0 && used !=0) {
		    	capacity = (int)((max-used)*1.0 / (used*1.0/Main.NumTasks) / (Main.NumTasks*1.0/numWorkflows)*0.9);
		    	Main.capacity = (int)((max-used)*1.0 / (used*1.0/Main.NumTasks));
			}
//		    else if(numWorkflows == 0) {
			else {
				capacity = 10;
				Main.capacity = 0;
		    }
//		    else 
//		    	capacity = 10;
		}
		else {
			long num = Main.maxWorkflowNum -(Main.NumSuccessfulWorkflows+Main.NumFailedWorkflows);
			if(num > 0) {
				capacity = (int)num;
				Main.capacity = (int)((max-used)*1.0 / (used*1.0/Main.NumTasks));
			}
			else
				capacity = 0;
		}
//		System.out.println("堆内存Init heap:" + usage.getInit()/1024/1024 + "Mb, " + "Max heap:" + usage.getMax()/1024/1024 + "Mb, "
//	    		 + "Used heap:" + usage.getUsed()/1024/1024 + "Mb" + "可用容量：" + capacity);
		
		//解决在没有工作流执行完的情况下，可容纳工作流由0变为非0
//		if(capacity == 0)
		if(capacity > Main.upperBoundWorkflowPerReceived)
			capacity = Main.upperBoundWorkflowPerReceived;
		
		Main.monitor.schedulerPressureSet("pressure", pressure);
		Main.monitor.schedulerPressureSet("memoryUsedRate", used*1.0/max*100);
		Main.monitor.schedulerPressureSet("capacity", capacity);
//		if(lastMainCapacity < Main.capacity) {
//			Main.log.info("上次次期望的任务数"+ lastMainCapacity + "\r\n"+"下次期望的工作流数: "+capacity+"\r\n"+"下次期望的任务数"+Main.capacity);
//		}
		if(capacity != 0 && !Main.CeShi)
			Main.log.info("本次期望的工作流数"+ capacity+", 当前系统中存在的工作流"+numWorkflows);
//		if(Main.maxWorkflowNum != 0)
//			Main.log.finest("该调度器容纳的工作流量已达到上限：调度器中当前工作流数为"+Main.maxWorkflowNum+", 任务数为" + Main.NumReceivedTasks);
//		else
//			Main.log.finest("该调度器容纳的工作流量未达到上限：调度器中当前工作流数为"+numWorkflows+", 任务数为" + Main.NumReceivedTasks);
//		Main.log.finest("当前调度器的压力为"+pressure+", 当前调度器还可容纳的工作流数量为"+ capacity);
		

		//发送保活信号
		int keepAliveWaitSec = SchedulerControllerClient.sendkeepAlive(Main.schedulerId, pressure, capacity, Main.keepAliveSerialNumber, Main.NodeIP+":"+Main.port, Main.clusterId);
		Main.keepAliveSerialNumber++;
		
		Main.log.finest("发送保活信号："+ Main.schedulerId +", "+ pressure +", "+ capacity + ", " +Main.keepAliveSerialNumber+"; 返回值:" + keepAliveWaitSec);
		
		if(keepAliveWaitSec <= 0) { //收到调度器自然死亡标志
			Main.deadFlag = true;
			Main.log.info("收到调度器死亡通知");
		}
		else {
			//定时发送下次保活信号
//			Main.log.info("发送保活信号成功" + keepAliveWaitSec);
//			Main.timer.schedule(new KeepAliveTimerTask(), new Date(new Date().getTime() + keepAliveWaitSec*60*1000));
//			Main.log.finest("收到下次发送保活信号的时间间隔");
			Main.timer.schedule(new KeepAliveTimerTask(), new Date(new Date().getTime() + keepAliveWaitSec*1000));
		
//			System.out.println("MyTimerTask runs and the time is : "+new Date());
//			Log.info("MyTimerTask runs and the time is : "+new Date());
		}
//        Main.keepAliveWaitSec = 3000;
//        Main.timer.schedule(new KeepAliveTimerTask(), new Date(new Date().getTime() + Main.keepAliveWaitSec));

		
	}

}
