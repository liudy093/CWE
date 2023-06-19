package bit.workflowScheduler.problem;

import java.util.ArrayList;
import java.util.List;

import bit.workflowScheduler.util.Parameters.WorkflowState;


/**
 * Workflow工作流类
 * @author YangLiwen
 * @version date：2020年5月23日  下午2:54:09
 *
 */
public class Workflow {
	private String workflowId; 
	private String workflowName; //暂时未用
	private String style; //工作流类型：DAG or DG
	//暂时作为workflow+workflowID，发送给资源分配模块与任务状态模块
//	private double deadline; //QoS指标
	//注意！！！task在workflow中的index与其taskID是相同的，方便根据taskId读取task
	private List<Task> taskList; 
//	private double	registerTime; //时间格式？？？来自调度器控制器
//	private int finishTime;       //？？？工作流的完成时间,结合注册时间计算makespan
    private WorkflowState workflowState;
    public boolean customization = false; //判断是够为定制工作流, false非定制，true定制 
	public String time_grade = ""; // 时间等级(A,B,C,D), A最高等级
	public String cost_grade = ""; // 花费（资金）等级(A,B,C,D)
	
    public Workflow() {
    	this.taskList = new ArrayList<>();
    	this.workflowState = WorkflowState.UNEXECUTE;
    }
    
	/**设置工作流的ID*/
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	/**获取工作流的ID*/
	public String getWorkflowId() {
		return this.workflowId;
	}
	
	/**设置工作流的名字*/
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	/**获取工作流的名字*/
	public String getWorkflowName() {
		return workflowName;
	}
	
	/**设置工作流的类型*/
	public void setWorkflowStyle(String style) {
		this.style = style;
	}
	/**获取工作流的名字*/
	public String getWorkflowStyle() {
		return style;
	}
	
	
//	/**设置工作流的截止期*/
//	public void setDeadline(double deadline) {
//		this.deadline = deadline;
//	}
//	/**获取工作流的截止期*/
//	public double getDeadline() {
//		return deadline;
//	}
	
	/**设置该工作流的任务集合*/
	public void setTaskList(List<Task> list) {
		this.taskList = list;
	}
	public void addTaskList(Task task) {
		this.taskList.add(task);
	}
	/**获取该工作流的任务集合*/
	public List<Task> getTaskList() {
		return taskList;
	}
	
//	/**设置工作流的注册时间*/
//	public void setRegisterTime(double registerTime) {
//		this.registerTime = registerTime;
//	}
//	/**获取工作流的注册时间*/
//	public double getRegisterTime() {
//		return registerTime;
//	}
//
//	/**设置工作流的完成时间*/
//	public void setFinishTime(int finishTime) {
//		this.finishTime = finishTime;
//	}
//	/**获取工作流的完成时间*/
//	public int getFinishTime() {
//		return finishTime;
//	}

	/**设置工作流的状态*/
	public void setWorkflowState(WorkflowState workflowState) {
		this.workflowState = workflowState;
	}
	/**获取工作流的状态*/
	public WorkflowState getWorkflowState() {
		return workflowState;
	}
	
	/**获取工作流中任务的个数*/
	public int getTasksNum() {
		return this.taskList.size();
	}
}