package ThreadPerMessage;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/8 - 15:49
 **/
public class Main {
    public static void main(String[] args) {
        System.out.println("main begin");
        Host host = new Host();
        host.request(10, 'A');
        host.request(20, 'B');
        host.request(30, 'C');
        System.out.println("main end");
    }
}
