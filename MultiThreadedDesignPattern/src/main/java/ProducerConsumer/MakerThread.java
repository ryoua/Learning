package ProducerConsumer;

import java.util.Random;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/7 - 14:36
 **/
public class MakerThread extends Thread{
    private final Random random;
    private final Table table;
    private static int id = 0;

    public MakerThread(String name, long seed, Table table) {
        super(name);
        this.random = new Random(seed);
        this.table = table;
    }

    @Override
    public void run() {
        try {
            while (true){
                String cake = "No." + id;
                id++;
                table.put(cake);
                Thread.sleep(random.nextInt(100));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
