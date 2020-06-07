package ProducerConsumer;

import java.util.Random;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/7 - 14:36
 **/
public class EaterThread extends Thread{
    private final Table table;
    private final Random random;

    public EaterThread(String name, long seed, Table table) {
        super(name);
        this.table = table;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        try {
            while (true) {
                table.take();
                Thread.sleep(random.nextInt(100));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
