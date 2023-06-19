package bit.workflowScheduler.problem;


/**
 * 资源类
 * @author Liyiran
 * @version date：2020年6月16日 
 *
 */
public class Resource {

	private long CPU;
 	private long Memory;
 	
 	
 	public Resource(long cpu,long mm) {
 		this.CPU = cpu;
 		this.Memory = mm;	
 	}
    public Resource() {
    	this.CPU = 0;//基本单位millicore(1Core=1000millicore)
        this.Memory = 0;//基本单位Mi,1024*1024byte
    }
    
    //当前Resource-r
    public void subtract(Resource r) {
    	this.CPU -= r.getCPU();
    	this.Memory -= r.getMemory();
    }
  //当前Resource+r
    public void add(Resource r) {
    	this.CPU += r.getCPU();
    	this.Memory += r.getMemory();
    }
    
	public long getCPU() {
		return CPU;
	}
	public void setCPU(long cPU) {
		CPU = cPU;
	}
	
	public long getMemory() {
		return Memory;
	}
	public void setMemory(long memory) {
		Memory = memory;
	}
	
	@Override
	public String toString() {
		return "CPU=" + CPU + ", Mem=" + this.Memory;
	}
}
