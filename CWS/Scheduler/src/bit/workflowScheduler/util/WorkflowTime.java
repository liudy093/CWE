package bit.workflowScheduler.util;

public class WorkflowTime {
	public String workflowId = "";
	public boolean customization = false; //判断是够为定制工作流, false非定制，true定制 
	public String time_grade = ""; // 时间等级(A,B,C,D), A最高等级
	public String cost_grade = ""; // 花费（资金）等级(A,B,C,D)
	public boolean isSuccessful = false; //工作流是否执行成功
	public long startTime = -1;
	public long endTime = -1; //!=-1，表示工作流执行已经完成，但有可能失败也有可能成功
	double runtime = -1;
	double cost = -1;
	public WorkflowTime(String workflowId, boolean customization, String time_grade, String cost_grade) {
		this.workflowId = workflowId;
		this.customization = customization;
		this.time_grade = time_grade;
		this.cost_grade = cost_grade;
	}
}
