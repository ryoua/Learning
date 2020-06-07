package Balking;

import java.util.concurrent.TimeoutException;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/7 - 14:00
 **/
public class Host {
    private final long timeout;
    private boolean ready = false;

    public Host(long timeout) {
        this.timeout = timeout;
    }

    public synchronized void setExecutable(boolean on) {
        ready = on;
        notifyAll();
    }

    public synchronized void execute() throws TimeoutException, InterruptedException {
        long start = System.currentTimeMillis();
        while (!ready) {
            long now = System.currentTimeMillis();
            long rest = timeout - (now - start);
            if (rest <= 0) {
                throw new TimeoutException("now - start=" + (now - start) + ", timeout = " + timeout);
            }
            wait(rest);
        }
        doExecute();
    }

    public void doExecute() {
        System.out.println(Thread.currentThread().getName() + " calls doExecute");
    }
}
