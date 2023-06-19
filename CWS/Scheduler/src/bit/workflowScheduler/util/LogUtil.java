package bit.workflowScheduler.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import bit.workflowScheduler.main.Main;


/**
 * 默认将log文件输出到.\Logs\xxxx年\x月\xxxx-xx-xx.log
 * 路径不存在的话会自动创建
 * 可通过修改getLogFilePath修改生成的log路径
 * 
 * @author YangLiwen
 * @version date：2020年8月11日  下午4:20:12
 *
 */
public class LogUtil {

    private static Calendar now = Calendar.getInstance();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final int year = now.get(Calendar.YEAR);

    private static final int month = now.get(Calendar.MONTH) + 1;

//    private static final String LOG_FOLDER_NAME = "Logs";
    private static final String LOG_FOLDER_NAME = "SchedulerLogs";

    private static final String LOG_FILE_SUFFIX = ".log";

    private static Logger logger = Logger.getLogger("MyLogger");

    //使用唯一的fileHandler，保证当天的所有日志写在同一个文件里
    private static FileHandler fileHandler = null; //getFileHandler();
    private static ConsoleHandler consoleHandler = new ConsoleHandler();

    private static MyLogFormatter myLogFormatter = new MyLogFormatter();

    private synchronized static String getLogFilePath() {
        StringBuffer logFilePath = new StringBuffer();
//        logFilePath.append(System.getProperty("user.home"));
//        logFilePath.append("C:\\Logs");
        
        logFilePath.append(".\\"); //当前路径
//        logFilePath.append(Main.volumePath); //k8s路径
        logFilePath.append(File.separatorChar);
        logFilePath.append(LOG_FOLDER_NAME);
        logFilePath.append(File.separatorChar);
        logFilePath.append(year);
        logFilePath.append(File.separatorChar);
        logFilePath.append(month);

        File dir = new File(logFilePath.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        logFilePath.append(File.separatorChar);
        logFilePath.append(sdf.format(new Date()));
        logFilePath.append(LOG_FILE_SUFFIX);

//        System.out.println(logFilePath.toString());
        return logFilePath.toString();
    }

    private static FileHandler getFileHandler() {
        FileHandler fileHandler = null;
        boolean APPEND_MODE = true;
        try {
            //文件日志内容标记为可追加
            fileHandler = new FileHandler(getLogFilePath(), APPEND_MODE);
            return fileHandler;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized static Logger setLoggerHanlder() {
        return setLoggerHanlder(Level.ALL);
    }
    
    public synchronized static Logger setLoggerHanlder(Level level) {

        try {
            //以文本的形式输出
//            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setFormatter(myLogFormatter);

            logger.addHandler(fileHandler);
            logger.setLevel(level);
        } catch (SecurityException e) {
            logger.severe(populateExceptionStackTrace(e));
        }
        return logger;
    }

    /**
     * 不同的调度器的日志放在不同的文件夹中，依schedulerId区分
     */
    
    
    private synchronized static String getLogFilePath(String schedulerId) {
        StringBuffer logFilePath = new StringBuffer();
//        logFilePath.append(System.getProperty("user.home"));
//        logFilePath.append("C:\\Logs");
//        logFilePath.append(".\\"); //当前路径
        logFilePath.append(Main.volumePath); //k8s路径
        logFilePath.append(File.separatorChar);
        logFilePath.append(LOG_FOLDER_NAME);
        logFilePath.append(File.separatorChar);
        logFilePath.append(year);
        logFilePath.append(File.separatorChar);
        logFilePath.append(month);
        logFilePath.append(File.separatorChar);
        logFilePath.append(schedulerId);

        File dir = new File(logFilePath.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        logFilePath.append(File.separatorChar);
        logFilePath.append(sdf.format(new Date()));
        logFilePath.append(LOG_FILE_SUFFIX);

//        System.out.println(logFilePath.toString());
        return logFilePath.toString();
    }

    private static FileHandler getFileHandler(String schedulerId) {
        FileHandler fileHandler = null;
        boolean APPEND_MODE = true;
        try {
            //文件日志内容标记为可追加
            fileHandler = new FileHandler(getLogFilePath(schedulerId), APPEND_MODE);
            return fileHandler;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //    SEVERE > WARNING > INFO > CONFIG > FINE > FINER > FINESET
    //加上调度器Id
    public synchronized static Logger setLoggerHanlder(Level level, String schedulerId ) {

        try {
        	//logger设置不使用父logger的handler，不然日志会重复记录。此处后面会讲
            logger.setUseParentHandlers(false);
            
            //以文本的形式输出
//            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler = getFileHandler(schedulerId);
            fileHandler.setFormatter(myLogFormatter);
            consoleHandler.setFormatter(myLogFormatter);
            
            fileHandler.setLevel(level);
            consoleHandler.setLevel(level);
            
            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);
            logger.setLevel(level);
        } catch (SecurityException e) {
            logger.severe(populateExceptionStackTrace(e));
        }
        return logger;
    }

    private synchronized static String populateExceptionStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement elem : e.getStackTrace()) {
            sb.append("\tat ").append(elem).append("\n");
        }
        return sb.toString();
    }
    
  public static void main(String [] args) {
  Logger logger = LogUtil.setLoggerHanlder(Level.INFO, "12");
  logger.info("Hello, world!");
  logger.severe("What are you doing?");
  logger.warning("Warning !");
//
//  for(Handler h : logger.getHandlers()) {
//      h.close();   //must call h.close or a .LCK file will remain.
//  }
}
    
}