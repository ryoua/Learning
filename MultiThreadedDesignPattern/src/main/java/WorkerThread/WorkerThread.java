package WorkerThread;

import java.util.Random;

public class WorkerThread extends Thread {
    private final Channel channel;
    private Random random = new Random();

    public WorkerThread(String name, Channel channel) {
        super(name);
        this.channel = channel;
    }

    @Override
    public void run() {
        while (true) {
            Request request = channel.takeRequest();
            request.execute();
        }
    }
}
