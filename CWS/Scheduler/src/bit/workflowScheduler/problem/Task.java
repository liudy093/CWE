package bit.workflowScheduler.problem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bit.workflowScheduler.util.Parameters.TaskState;


/**
 * 任务类
 * @author YangLiwen
 * @version date：2020年5月31日  下午7:57:15
 *
 */

public class Task {
	private int taskId;
	private String workflowId;   //任务所在工作流的ID
	private String taskName;
//	private double size; //待定
//	private String template;
	private String phase; //用来兼容中期那个系统的，目前用不到了
	private String node_info; //这个也是兼容用的，从k8s里读pod状态，本代码中未使用
//	private int cpu;
//	private long memory;
	private List<Task> parentList; 
	private List<Task> childList;
	private Map<String, String> env;
	private List<String> inFileList; //输入文件列表
	private List<String> outFileList; //输出文件列表
	private TaskState tastState;
	private Resource res; //资源类,lyr.0616改
	private long taskfinishTime;//任务的完成时间 ,lyr.0616改
	private long taskEalierStartTime;//任务的理论上最早开始时间 ,lyr.0616改，由于thread4的执行周期，有可能造成延后
	private String image; //对应proto中的template
	private String podName; //pod的名字，task-taskId-workflowId-失败次数
	private int failedNum = 0; //任务执行失败的次数
	public boolean customization = false; //判断是否属于定制工作流, false非定制，true定制 
	public boolean time_grade = false; // 是否有时间等级
	public boolean cost_grade = false; // 是否有花费（资金）
	public int isdelay = 0; //判断该任务是否延迟过一次，延迟过的立即调度，否则至少等待一个循环。延迟某个工作流的执行，其任务延迟一个调度循环
							//是否延迟过固定的次数
	
	//for DG
	private boolean isTrueTask; //true 工作流中真正的任务；false 代表DG工作流的整个资源需求，不是真正的工作流
	private String workflowStyle;
	
	
    public Task() {
    	this.env = new HashMap<String, String>();
    	this.parentList = new ArrayList<>();
        this.childList = new ArrayList<>();
        this.inFileList = new ArrayList<>();
        this.inFileList = new ArrayList<>();
        this.tastState = TaskState.UNEXECUTE;
        this.taskfinishTime = 20500101000000L;
        this.taskEalierStartTime = 20500101000000L;
        this.failedNum = 0;
        isTrueTask = true;
        workflowStyle = "DAG";
    }
    
    //for DG
    public Task(String workflowId, Resource res) {
    	this.workflowId = workflowId;
    	this.res = res;
    	this.isTrueTask = false;
    }

    /**设置任务的ID*/
    public void setTaskId(int taskId) {
    	this.taskId = taskId;
    }
    /**获取任务的ID*/
    public int getTaskId() {
    	return this.taskId;
    }

    /**设置任务的工作流ID*/
    public void setWorkflowId(String workflowId) {
    	this.workflowId = workflowId;
    }
    /**获取任务的工作流ID*/
    public String getWorkflowId() {
    	return this.workflowId;
    }
    
    /**设置任务的名称*/
    public void setTaskName(String taskName) {
    	this.taskName = taskName;
    }
    /**获取任务的名称*/
    public String getTaskName() {
    	return this.taskName;
    }
    
//    /**设置任务的大小*/
//    public void setTaskSize(double taskSize) {
//    	this.size = taskSize;
//    }
//    /**获取任务的大小*/
//    public double getTaskSize() {
//    	return this.size;
//    }    
    
//	/**设置任务的template*/
//	public void setTaskTemplate(String template) {
//		this.template = template;
//	}
//	/**获取任务的template*/
//	public String getTaskTemplate() {
//		return template;
//	}
	
	/**设置任务的phase*/
	public void setTaskPhase(String phase) {
		this.phase = phase;
	}
	/**获取任务的phase*/
	public String getTaskPhase() {
		return phase;
	}
	
	/**设置任务的node_info*/
	public void setTaskNodeInfo(String node_info) {
		this.node_info = node_info;
	}
	/**获取任务的node_info*/
	public String getTaskNodeInfo() {
		return node_info;
	}
	
//	/**设置任务的cpu*/
//	public void setTaskCpu(int cpu) {
//		this.cpu = cpu;
//	}
//	/**获取任务的cpu*/
//	public int getTakeCpu() {
//		return cpu;
//	}
	
//	/**设置任务的memory*/
//	public void setTaskMemory(long memory) {
//		this.memory = memory;
//	}
//	/**获取任务的memory*/
//	public long getTakeMemory() {
//		return memory;
//	}
    
    /**设置任务的父任务列表*/
    public void setParentList(List<Task> list) {
        this.parentList = list;
    }
    public void addParent(Task task) {
        this.parentList.add(task);
    }
    /**获取任务的父任务列表*/
    public List<Task> getParentList() {
        return this.parentList;
    }
    
    /**设置任务的子任务列表*/
    public void setChildList(List<Task> list) {
        this.childList = list;
    }
    public void addChild(Task task) {
        this.childList.add(task);
    }
    /**获取任务的子任务列表*/
    public List<Task> getChildList() {
        return this.childList;
    }

    /**设置任务的输入文件*/
    public void setInFileList(List<String> list) {
        this.inFileList = list;
    }
    public void addInFile(String file) {
        this.inFileList.add(file);
    }
    /**获取任务的输入文件*/
    public List<String> getInFileList() {
        return this.inFileList;
    }
    
    /**设置任务的输出文件*/
    public void setOutFileList(List<String> list) {
        this.outFileList = list;
    }
    public void addOutFile(String file) {
        this.outFileList.add(file);
    }
    /**获取任务的输出文件*/
    public List<String> getOutFileList() {
        return this.outFileList;
    }

    /**设置任务的状态*/
    public void setTaskState(TaskState taskState) {
    	this.tastState = taskState;
    }
    /**获取任务的状态*/
    public TaskState getTaskState() {
    	return this.tastState;
    }
    
	public Resource getRes() {
		return res;
	}

	public void setRes(Resource res) {
		this.res = res;
	}
    
    public long getTaskfinishTime() {
		return taskfinishTime;
	}

	public void setTaskfinishTime(long taskfinishTime) {
		this.taskfinishTime = taskfinishTime;
	}

	public long getTaskEalierStartTime() {
		return taskEalierStartTime;
	}

	public void setTaskEalierStartTime(long taskEalierStartTime) {
		this.taskEalierStartTime = taskEalierStartTime;
	}
	

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }
    public void putEnv(String key, String val) {
        this.env.put(key, val);
    }
    public Map<String, String> getEnv() {
        return this.env;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    public String getImage() {
        return this.image;
    }
    
    public void setPodName(String podName) {
        this.podName = podName;
    }
    public String getPodName() {
    	//计算任务t taskPod的名字 pod的名字，task-taskId-workflowId-失败次数
    	return "task-" + this.taskId + "-" + this.workflowId + "-" + this.failedNum;
    }
    
    public void setFailedNum(int failedNum) {
        this.failedNum = failedNum;
    }
    public void addFailedNum() {
        this.failedNum++;
    }
    public int getFailedNum() {
        return this.failedNum;
    }
    
    public void setIsTrueTask(boolean t) {
        this.isTrueTask = t;
    }
    public boolean getIsTrueTask() {
        return this.isTrueTask;
    }
    
    public void setWorkflowStyle(String workflowStyle) {
        this.workflowStyle = workflowStyle;
    }
    public String getWorkflowStyle() {
        return this.workflowStyle;
    }
    
    
    @Override
	public String toString() {
		return "Task [id=" + this.taskId + ", workflowId=" + this.workflowId + "]";
	}
    
  //-------------------------------------comparators--------------------------------
  	public static class TaskComparator implements Comparator<Task>{
  		public int compare(Task o1, Task o2) {
  			if(o1.customization==true && o2.customization==false) //定制在前	
  				return -1;
  			else if(o1.customization==false && o2.customization==true)
  				return 1;
  			else if(o1.customization==true && o2.customization==true){
  				if(o1.time_grade==true && o2.time_grade==false)	//均定制，有time的在前
  	  				return -1;
  	  			else if(o1.time_grade==false && o2.time_grade==true) //均定制，有time的在前
  	  				return 1;
  	  			else if(o1.time_grade==true && o2.time_grade==true) //均定制且有time
	  	  			return 0;
  	  			else { //均定制，无time
	  	  			if(o1.cost_grade==true && o2.cost_grade==false)	//均定制且无time，无cost的在前
	  	  				return 1;
	  	  			else if(o1.cost_grade==false && o2.cost_grade==true) //均定制且无time，无cost的在前
	  	  				return -1;
	  	  			else //均定制,无time,cost都有或无
	  	  				return 0;
  	  			}
  			}
  			else
  				return 0;
  		}
  	}
}






