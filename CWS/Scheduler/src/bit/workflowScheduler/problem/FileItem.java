package bit.workflowScheduler.problem;


/**
 * 任务执行依赖的输入文件
 * @author YangLiwen
 * @version date：2020年5月31日  下午8:12:18
 *
 */
public class FileItem {

    private String name;
    private double size;
    //地址属性？？？

    public FileItem(String name, double size) {
        this.name = name;
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public double getSize() {
        return this.size;
    }
}