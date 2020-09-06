package Balking;

import java.io.IOException;
import java.util.Random;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 15:13
 **/
public class ChangeThread extends Thread{
    private final Data data;
    private final Random random = new Random();

    public ChangeThread(String name, Data data) {
        super(name);
        this.data = data;
    }


    @Override
    public void run() {
        try {
            for (int i = 0; true; i++) {
                data.change("No." + i);
                Thread.sleep(random.nextInt(1000));
                data.save();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
