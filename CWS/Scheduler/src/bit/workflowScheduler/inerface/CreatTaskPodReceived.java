package bit.workflowScheduler.inerface;


/**
 * 向资源分配器请求taskPod得到的回复，对应proto文件中message CreateTaskPodResponse
 * @author YangLiwen
 * @version date：2020年7月18日  下午11:27:38
 *
 */
public class CreatTaskPodReceived {

	//成功创建 pod 的状态码（成功>=1，失败置为0）
	private int result; 
	//在失败状态下，可以设置状态码
    //成功状态（result>=1），客户端不关系此字段，置为 0 即可
	private int err_no; 
	
	public CreatTaskPodReceived() {}
	public CreatTaskPodReceived(int result, int err_no) {
		this.result = result;
		this.err_no = err_no;
	}
	
    public void setResult(int result) {
    	this.result = result;
    }
    public int getResult() {
    	return this.result;
    }
    
    public void setErrNo(int err_no) {
    	this.err_no = err_no;
    }
    public int getErrNo() {
    	return this.err_no;
    }
}
