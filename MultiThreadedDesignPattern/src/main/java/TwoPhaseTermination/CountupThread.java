package TwoPhaseTermination;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/15
 **/
public class CountupThread extends Thread{
    private long counter = 0;

    private volatile boolean shutdownRequested = false;

    private void shutdownRequest() {
        this.shutdownRequested = true;
        interrupt();
    }

    public boolean isShutdownRequested() {
        return this.shutdownRequested;
    }

    @Override
    public void run() {
        try {
            while (!isShutdownRequested())
                doWork();
        } catch (InterruptedException e) {}
        finally {
            doShutDown();
        }
    }

    public void doWork() throws InterruptedException {
        counter++;
        System.out.println("counter: " + counter);
        Thread.sleep(500);
    }

    public void doShutDown() {
        System.out.println("shutdown - counter: " + counter);
    }
}
