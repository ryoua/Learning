package ch01;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @Author: RyouA
 * @Date: 2020/5/14 - 3:58 下午
 **/
public class Main {
    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();

        ThreadFactory factory = Executors.defaultThreadFactory();
        factory.newThread(new Printer("a")).start();

        for (int i = 0; i < 1000; i++)
            System.out.println(i);
    }
}
