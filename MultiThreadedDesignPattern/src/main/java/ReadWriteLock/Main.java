package ReadWriteLock;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/7 - 15:38
 **/
public class Main {
    public static void main(String[] args) {
        Data data = new Data(10);
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new WriterThread(data, "ABCDEFGOQIWJEQWJEQIWEJOQJWE").start();
        new WriterThread(data, "asdasdaswoiqwueoqwuoeuqwuie").start();
    }
}
