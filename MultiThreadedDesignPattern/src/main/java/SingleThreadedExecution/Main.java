package SingleThreadedExecution;


import java.util.Random;
import java.util.concurrent.Semaphore;

class BoundedResource {
    private final Semaphore semaphore;
    private final int permits;
    private final static Random random = new Random(34159);

    public BoundedResource(int permits) {
        this.semaphore = new Semaphore(permits);
        this.permits = permits;
    }

    public void use() throws InterruptedException {
        semaphore.acquire();
        try {
            doUse();
        } finally {
            semaphore.release();
        }
    }

    public void doUse() throws InterruptedException {
        System.out.println("begin: " + (permits - semaphore.availablePermits()));
        Thread.sleep(random.nextInt(500));
        System.out.println("end: " + (permits - semaphore.availablePermits()));
    }
}

class UserThread2 extends Thread {
    private final static Random random = new Random(12345);
    private  BoundedResource resource;

    public UserThread2(BoundedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        try {

            while (true) {
                resource.use();
                Thread.sleep(random.nextInt(3000));
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}

public class Main {
    public static void main(String[] args) {
//        Gate gate = new Gate();
//        new UserThread("alice", "alisanda", gate).start();
//        new UserThread("bob", "berkle", gate).start();
//        new UserThread("cla", "clair", gate).start();
        BoundedResource resource = new BoundedResource(3);
        for (int i = 0; i < 10; i++) {
            new UserThread2(resource).start();
        }
    }
}
