package bit.workflowScheduler.scheduleModel;

import java.util.ArrayList;
import java.util.List;

import bit.workflowScheduler.main.Main;
import bit.workflowScheduler.problem.Resource;
import bit.workflowScheduler.problem.Task;

public class Method1
{

	//优先调度最早加入readyTaskList中的任务
	public static ArrayList<Task> run(List<Task> taskList, Resource resource_ava)
	{
		ArrayList<Task> ScheduleTask = new ArrayList<Task>();
		long cpu = 0;
		long mm = 0;
		for(Task task: taskList) {
			cpu = cpu + task.getRes().getCPU();
			mm = mm + task.getRes().getMemory();	
			if((cpu <= resource_ava.getCPU()) && (mm <= resource_ava.getMemory())) {
				ScheduleTask.add(task);
			}
			else {
				cpu = cpu - task.getRes().getCPU();
				mm = mm - task.getRes().getMemory();
//				break;
			}
		}
		Main.resourcesURate = (cpu*1.0/resource_ava.getCPU()+mm*1.0/resource_ava.getMemory())/2;
		Main.monitor.methodEfficiencySet("methodCPURate", cpu*1.0/resource_ava.getCPU()*100);
		Main.monitor.methodEfficiencySet("methodMemoryRate", mm*1.0/resource_ava.getMemory()*100);
		return ScheduleTask;
	}
}