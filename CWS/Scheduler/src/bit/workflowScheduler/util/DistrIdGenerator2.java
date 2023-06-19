package bit.workflowScheduler.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * Filename: DistrIdGenerator2.java

 * @author:liukai
 * @function:

 */
public class DistrIdGenerator2 {
    public static String LOCAL_IP = null;
    private static String LOCAL_IP_Q4 = null;
    private static Integer index = 0;  // 当前的序号
    static {
        // 获取本机ip
        LOCAL_IP = initLocalAddress();
        // 获取本机ip第四位
        LOCAL_IP_Q4 = formatNumber(Integer.valueOf(LOCAL_IP.split("\\.")[3]), 3);
    }

    /**
     * 获取服务器IP地址
     * 
     * @return
     */
    private static String initLocalAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if ((ip != null) && ((ip instanceof Inet4Address))) {
                        return ip.getHostAddress();
                    }
                }
            }
            if (ip == null) {
                ip = InetAddress.getLocalHost();
            }
            return ip.getHostAddress();
        } catch (Exception e) {
            System.out.println("We got unexpected:" + e.getMessage());
        }
        return "000.000.000.000";
    }

    /**
     * 格式化数字，前置位不够补0
     *
     * @param num
     *            : 数值
     * @param p
     *            ：位数,最多支持6位
     * @return
     */
    private static String formatNumber(int num, int p) {
        StringBuffer result = new StringBuffer();

        int value = num;
        for (int i = 0; i < p; i++) {
            result.insert(0, value % 10);
            value = value / 10;
        }
        return result.toString();
    }//数值格式转换


    
    /**
     * 生成随机ID入口方法
     * 规则：年月日时分秒毫秒（去掉前两位）+服务器IP（第四位，长度为三位，若不足三位，则在前面补0）+4位递增序列数（1—9999，若不足4位，后面补0）
     * 特征：共22位，满足分布式，线程安全，不会重复
     * 
     * @return
     */
    public synchronized static String createDistrId() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String formDate = sdf.format(date);
        String dateStr = formDate.substring(2);
        if (index >= 9999) {
            index = 1;
        } else {
            index++;
        }
        String id = dateStr + LOCAL_IP_Q4 +  formatNumber(index, 4);//最终格式
        return id;
    }

    /**
     * 获取随机数,若不够位数，则在末尾补0
     * 
     * @param min
     * @param max
     * @param n
     * @return
     */
    public static int randomCommon(int min, int max, int n) {
        int num = (int) (Math.random() * (max - min)) + min;
        if ((n - String.valueOf(num).length()) > 0) {
            int base = (int) Math.pow(10, (n - String.valueOf(num).length()));
            int numNew = num * base;
            return numNew;
        }
        return num;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10100; i++) {
            String id= DistrIdGenerator2.createDistrId();
            System.out.println(" id is  : " + id);
//            if(id.length()!=22){
             System.out.println(" i is  : " + i+" , id is : "+id+"  =======");//例如2006111312255610010085 20年6.11 13:12:25 561ms ip:001 085
 //               System.out.println("  id is : "+ id+"  =======");
//            }
        }
    }

}
