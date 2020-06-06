package Immutable;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 10:09
 **/
public class PrintPersonThread extends Thread{
    private Person person;

    public PrintPersonThread(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        while (true)
            System.out.println(Thread.currentThread().getName() + " print: " + person);
    }
}
