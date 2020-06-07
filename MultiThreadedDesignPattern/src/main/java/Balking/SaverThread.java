package Balking;

import java.io.IOException;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 15:13
 **/
public class SaverThread extends Thread{
    private final Data data;

    public SaverThread(String name, Data data) {
        super(name);
        this.data = data;
    }

    @Override
    public void run() {
        try {
            while (true) {
                data.save();
                Thread.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
