package ProducerConsumer;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/7 - 14:36
 **/
public class Main {
    public static void main(String[] args) {
        Table table = new Table(3);
        new MakerThread("MakerThread-1", 312412 ,table).start();
        new MakerThread("MakerThread-2", 22323, table).start();
        new MakerThread("MakerThread-3", 123213, table).start();
        new EaterThread("EaterThread-1", 123123, table).start();
        new EaterThread("EaterThread-2", 123123, table).start();
        new EaterThread("EaterThread-3", 123123, table).start();
    }
}
