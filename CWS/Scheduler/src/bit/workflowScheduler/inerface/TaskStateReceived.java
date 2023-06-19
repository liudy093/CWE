package bit.workflowScheduler.inerface;


/**
 * 从任务状态跟踪器接收的任务状态
 * @author YangLiwen
 * @version date：2020年6月3日  下午9:59:59
 *
 */
public class TaskStateReceived {
	private String workflowId;
	private int taskId;
	private String taskState;
	private long taskfinishTime;
	
	public TaskStateReceived() {}
	public TaskStateReceived(String workflowId, int taskId, String taskState) {
		this.workflowId = workflowId;
		this.taskId = taskId;
		this.taskState = taskState;
	}
	
    public void setworkflowId(String workflowId) {
    	this.workflowId = workflowId;
    }
    public String getworkflowId() {
    	return this.workflowId;
    }
    
    public void setTaskId(int taskId) {
    	this.taskId = taskId;
    }
    public int getTaskId() {
    	return this.taskId;
    }
    
    public void setTaskState(String taskState) {
    	this.taskState = taskState;
    }
    public String getTaskState() {
    	return this.taskState;
    }
    
	public long getTaskfinishTime() {
		return taskfinishTime;
	}
	public void setTaskfinishTime(long taskfinishTime) {
		this.taskfinishTime = taskfinishTime;
	}
}
