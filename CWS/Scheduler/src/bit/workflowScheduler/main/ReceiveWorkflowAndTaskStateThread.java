package bit.workflowScheduler.main;

import bit.workflowScheduler.grpc.Scheduler.SchedulerServer;

/**
 * 接收工作流线程和任务状态
 * @author YangLiwen
 * @version date：2020年10月9日  下午10:17:59
 *
 */
public class ReceiveWorkflowAndTaskStateThread implements Runnable {

//	private Deque<ProtoWF.Workflow> workflowFileDeque;
//	
//	public ReceiveWorkflowThread(Deque<ProtoWF> workflowFileDeque) {
//	     this.workflowFileDeque = workflowFileDeque;
//	}
	
	@Override
	public void run() {
		
		/*接收工作流文件，解压缩，并存入Main.workflowFileDeque*/
		/*接收任务状态文件, 格式转换，并将其存入Main.taskStateDeque*/
		SchedulerServer.setup();
		
		
		/*需线程同步workflowFileDeque的代码写在这：
		 * 向workflowFileDeque中写入*/
//		synchronized(this.workflowFileDeque) {
//			this.workflowFileDeque.add(workflow);
//		}
	}

}
