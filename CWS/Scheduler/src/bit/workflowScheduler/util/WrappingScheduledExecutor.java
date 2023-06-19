package bit.workflowScheduler.util;



import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import bit.workflowScheduler.main.NewKeepAlive;

//NewKeepAlive
public class WrappingScheduledExecutor extends ScheduledThreadPoolExecutor {

    public WrappingScheduledExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(wrapRunnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(wrapRunnable(command), initialDelay, delay, unit);
    }

    private Runnable wrapRunnable(Runnable command) {
        return new LogOnExceptionRunnable(command);
    }

    private class LogOnExceptionRunnable implements Runnable {
        private Runnable theRunnable;

        public LogOnExceptionRunnable(Runnable theRunnable) {
            super();
            this.theRunnable = theRunnable;
        }

        public void run() {
            try {
                theRunnable.run();
            } catch (Throwable t) {
                System.err.println("error in executing: " + theRunnable + ", the error = " + t);
                /**
                 *重要，如果你选择继续向上抛出异常，则在外部必须能够catch住这个异常，否则还是会造成后续任务不会执行
                 * IMPORTANT: if you thrown exception. then in the out, you have to try/catch the exception,
                 * otherwise, the executor will stop
                 */
                // throw new RuntimeException(e);
            }
        }
    }

//    public static void main(String[] args) {
//        new WrappingScheduledExecutor(1).scheduleAtFixedRate(new NewKeepAlive(), 0, 1, TimeUnit.SECONDS);
//    }
}

