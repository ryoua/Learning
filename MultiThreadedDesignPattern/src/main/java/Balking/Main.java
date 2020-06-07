package Balking;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 15:08
 **/
public class Main {
    public static void main(String[] args) {
        Data data = new Data("data.txt", "()");
        new ChangeThread("ChangeThread", data).start();
        new SaverThread("SaverThread", data).start();
    }
}
