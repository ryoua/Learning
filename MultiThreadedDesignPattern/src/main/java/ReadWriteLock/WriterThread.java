package ReadWriteLock;

import java.util.Random;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/7 - 15:38
 **/
public class WriterThread extends Thread{
    private static final Random random = new Random();
    private final Data data;
    private final String filter;
    private int index = 0;

    public WriterThread(Data data, String filter) {
        this.data = data;
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char c = nextChar();
                data.write(c);
                Thread.sleep(random.nextInt(500));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private char nextChar() {
        char c = filter.charAt(index);
        index++;
        if (index > filter.length())
            index = 0;
        return c;
    }
}
