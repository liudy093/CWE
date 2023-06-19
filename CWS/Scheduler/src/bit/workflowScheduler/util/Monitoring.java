package bit.workflowScheduler.util;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.exporter.PushGateway;

import java.io.IOException;
import java.util.HashMap;

import bit.workflowScheduler.main.Main;

public class Monitoring {
//    //一个调度器内每一组readyTask的分配总时间
//    private static final Histogram tasksScheduleTime = Histogram.build().name("tasksScheduleTime").help("ReadyTaskScheduleTime").register();
//    //一个调度器内工作流从开始执行到完成的执行时间统计
//    private static final Histogram workflowTime = Histogram.build().name("WorkflowTime").help("Time of workflows").register();

//    //tasksScheduleTime的Timer字典
//    private static HashMap<String, Histogram.Timer> taskTimer = new HashMap<String, Histogram.Timer>();
//    //WorkflowTime的Timer字典
//    private static HashMap<String, Histogram.Timer> workflowTimer = new HashMap<String, Histogram.Timer>();
	
//	//一个调度器内每一组readyTask的分配总时间 ???不知道怎么统计, set
//	private static final Gauge TimeReadyTasks = Gauge.build().name("TimeReadyTasks").help("每一组readyTask分配过程的总时间T= "
//			+ "背包算法执行时间+与资源模块通信时间+与状态跟踪模块通信时间+与工作流状态数据库通信时间").labelNames("type").register();
//	//???一个工作流一个Gauge, set
//	private static final Gauge TimeWorkflow = Gauge.build().name("TimeWorkflow").help("某个工作流的完成时间").labelNames("type").register();
//	
//	//一个调度器内ReadyTask的个数, set
//	private static final Gauge numReadyTasks = Gauge.build().name("numReadyTasks").help("Numbers of Readytasks").register();
	
	//正常定时push的指标的注册地点
    private static CollectorRegistry registry_regular = new CollectorRegistry();

    //事件驱动指标的注册地点
    private static CollectorRegistry registry_event = new CollectorRegistry();
	
	//一个调度器内的任务数量, 递增inc
    private static final Gauge numTask = Gauge.build().name("numTask").help("接受到的任务数量recievedTasks"
    		+ "+执行成功的任务数量succeededTasks+执行失败的任务数量failedTasks").labelNames("type").register(registry_regular);
    
    //一个调度器内的工作流数量, 递增inc
    private static final Gauge numWorkflow = Gauge.build().name("numWorkflow").help("接受到的工作流数量recievedWorkflows"
    		+ "+执行成功的工作流数量succeededWorkflows+执行失败的工作流数量failedWorkflows")
    		.labelNames("type")
    		.register(registry_regular);
    
    //一个调度器内的创建pod的任务数量, 递增inc
    private static final Gauge numTaskPod = Gauge.build().name("numTaskPod").help("请求创建任务pod的数量requestCreatedPodTasks"
    		+ "+已创建任务pod的数量createdPodTasks")
    		.labelNames("type")
    		.register(registry_regular);
    
    //一个调度器内的与Tracker交互的任务数量, 递增inc
    private static final Gauge numTaskTracker = Gauge.build().name("numTaskTracker").help("发送给Tracker的任务数量sendTrackerTasks"
    		+ "+从Tracker收到任务状态的任务数量receivedStateTasks")
    		.labelNames("type")
    		.register(registry_regular);
    
    //调度器压力, set
    private static final Gauge schedulerPressure = Gauge.build().name("schedulerPressure").help("当前调度器压力pressure"
    		+ "+当前内存使用率memoryUsedRate + 当前调度器还可容纳的工作流数量capacity")
    		.labelNames("type")
    		.register(registry_regular);
    

    //调度器单位时间执行的任务数和工作流数(是执行过的，有可能执行失败也有可能执行成功), set
    private static final Gauge schedulerEfficiency = Gauge.build().name("schedulerEfficiency").help("调度器执行效率: 单位时间执行的工作流数numFinishedWorkflows"
    		+ "+单位时间执行的任务数numFinishedTasks")
    		.labelNames("type")
    		.register(registry_regular);
    //调度器内调度算法的效率, set
    private static final Gauge methodEfficiency = Gauge.build().name("methodEfficiency").help("调度算法的效率: 对申请资源的CPU利用率methodCPURate"
    		+ "+对申请资源的内存利用率methodMemoryRate")
    		.labelNames("type")
    		.register(registry_regular);
    
    //调度器工作流解析成功率, set
    private static final Gauge schedulerParseEfficiency = Gauge.build().name("schedulerParseEfficiency").help("调度器工作流解析成功率parseSuccessfulRate")
    		.labelNames("type")
    		.register(registry_regular);
    

    
    private static final Gauge executionTime = Gauge.build().name("executionTime").help("工作流、任务的执行开始、结束时间")
    		.labelNames("type").register(registry_event);
    
    //设置HTTP暴露端口
    public void setHttpPort(int port) throws IOException {
        HTTPServer server = new HTTPServer(port);
    }

//    //numReadyTasks改变的值
//    public void numReadyTasksSet(int num) {
//        numReadyTasks.set(num);
//    }
    
//    //numWorkflowAndTasks改变的值
//    public void numWorkflowAndTasksSet(String label, int num) {
//    	numWorkflowAndTasks.labels(label).set(num);
//    }
    //numTask增加的值
    public void numTaskInc(String label, int num) {
    	numTask.labels(label).inc(num);
    }
    //numWorkflow增加的值
    public void numWorkflowInc(String label, int num) {
    	numWorkflow.labels(label).inc(num);
    }
    //numTaskPod增加的值
    public void numTaskPodInc(String label, int num) {
    	numTaskPod.labels(label).inc(num);
    }
    //numTaskTracker增加的值
    public void numTaskTrackerInc(String label, int num) {
    	numTaskTracker.labels(label).inc(num);
    }
    //schedulerPressure改变的值
    public void schedulerPressureSet(String label, double num) {
		 schedulerPressure.labels(label).set(num);
	}
    //schedulerEfficiency改变的值
    public void schedulerEfficiencySet(String label, double num) {
    	schedulerEfficiency.labels(label).set(num);
	}
    //methodEfficiency改变的值
    public void methodEfficiencySet(String label, double num) {
    	methodEfficiency.labels(label).set(num);
	}
    //schedulerParseEfficiency改变的值
    public void schedulerParseEfficiencySet(String label, double num) {
    	schedulerParseEfficiency.labels(label).set(num);
	}
    //executionTime push的值
    public void executionTimeSet(String label, long date) {
    	executionTime.labels(label).set(date);
	}
    //executionTime push的值
    public void executionTimeSet(String label, double date) {
    	executionTime.labels(label).set(date);
	}
    
//    //tasksScheduleTime计时启停
//    public void tasksScheduleTime_tic(String workflowID) {
//        taskTimer.put(workflowID, tasksScheduleTime.startTimer());
//    }
//
//    public void tasksScheduleTime_toc(String workflowID) {
//        taskTimer.get(workflowID).observeDuration();
//        taskTimer.remove(workflowID);
//    }
//
//    //workflowTime计时启停
//    public void workflowTime_tic(String workflowID) {
//        workflowTimer.put(workflowID, workflowTime.startTimer());
//    }
//
//    public void workflowTime_toc(String workflowID) {
//        workflowTimer.get(workflowID).observeDuration();
//        workflowTimer.remove(workflowID);
//    }
    
    //事件驱动推送：向pushgateway推送指标<lable, date>
//    public void pushMetrics(PushGateway pushgateway, String lable, long date) {
//    	executionTimeSet(lable, date);
//    	try {
//			pushgateway.pushAdd(registry_event, lable + date);
//		} catch (Exception e) {
//			Main.log.warning("prometheus：事件驱动的push出现问题，正常退出: "+e);
//			e.printStackTrace();
//			System.exit(0);
//		}
//    }
    public void pushMetrics(PushGateway pushgateway, String jobname) {
    	try {
			pushgateway.pushAdd(registry_event, jobname);
		} catch (Exception e) {
			Main.log.warning("prometheus：事件驱动的push出现问题，正常退出: "+e);
			e.printStackTrace();
			System.exit(0);
		}
    }

    //定时推送
    public void timelyPushMetrics(PushGateway pushgateway, String jobname) {
        try {
            pushgateway.pushAdd(registry_regular, jobname);

        } catch (Exception e) {
            Main.log.warning("prometheus：周期的push出现问题，正常退出: "+e);
            e.printStackTrace();
            System.exit(0);
        }
    }

}