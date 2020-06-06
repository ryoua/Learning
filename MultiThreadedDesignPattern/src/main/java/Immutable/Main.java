package Immutable;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 10:09
 **/
public class Main {
    public static void main(String[] args) {
        Person alice = new Person("alice", "atelanda");
        new PrintPersonThread(alice).start();
        new PrintPersonThread(alice).start();
        new PrintPersonThread(alice).start();
    }
}
