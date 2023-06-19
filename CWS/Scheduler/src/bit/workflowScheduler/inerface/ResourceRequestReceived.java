package bit.workflowScheduler.inerface;

import bit.workflowScheduler.problem.Resource;


/**
 * 从资源分配器分配到的资源，对应proto文件中message ResourceAllocateInfo
 * @author YangLiwen
 * @version date：2020年7月18日  下午11:27:38
 *
 */
public class ResourceRequestReceived {
	private String schedulerId;
	private Resource currentRequest;
	private boolean currentRequestStatus;
	
	public ResourceRequestReceived() {}
	public ResourceRequestReceived(String requestId, Resource currentRequest, boolean currentRequestStatus) {
		this.schedulerId = requestId;
		this.currentRequest = currentRequest;
		this.currentRequestStatus = currentRequestStatus;
	}
	
    public void setRequestId(String schedulerId) {
    	this.schedulerId = schedulerId;
    }
    public String getRequestId() {
    	return this.schedulerId;
    }
    
    public void setCurrentRequest(Resource currentRequest) {
    	this.currentRequest = currentRequest;
    }
    public Resource getCurrentRequest() {
    	return this.currentRequest;
    }
    
    public void setCurrentRequestStatus(boolean currentRequestStatus) {
    	this.currentRequestStatus = currentRequestStatus;
    }
    public boolean getCurrentRequestStatus() {
    	return this.currentRequestStatus;
    }
}
