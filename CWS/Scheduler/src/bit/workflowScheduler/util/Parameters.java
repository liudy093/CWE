package bit.workflowScheduler.util;

public class Parameters {

    /**
     * WorkflowState工作流状态
     */
    public enum WorkflowState {
    	//执行成功
    	SUCCESSFUL,
    	//执行失败
    	FAILED,
    	//未执行
    	UNEXECUTE,
    	//执行中
    	EXECUTING,
    	
    	/**
    	 * 这两个状态用于更新readyTaskList，内部使用，不是对外的状态
    	 */
    	//新解析且曾经被指派过
    	NEWASSIGNED,
    	//新解析且未指派
    	NEWUNASSIGNED
    }

    /**
     * TastState任务状态
     */
    public enum TaskState {
    	//执行成功
    	SUCCESSFUL,
    	//执行失败
    	FAILED,
    	//未执行
    	UNEXECUTE,
    	//执行中，已启pod
    	EXECUTING,
//    	//父任务已执行完
//    	READY
    }
}