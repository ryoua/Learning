package SingleThreadedExecution;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/5 - 21:31
 **/
public class Test extends Thread{
    public synchronized void a() throws InterruptedException {
        while (true)
            Thread.sleep(100);
    }

    public synchronized void b() {
        System.out.println("b");
    }

    @Override
    public void run() {
        try {
            a();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        b();
    }
}
