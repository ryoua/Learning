package ThreadPerMessage;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/8 - 15:49
 **/
public class Helper {
    public void handle(int count, char c) {
        System.out.println("    handle(" + count + ", " + c + ") begin");
        for (int i = 0; i < count; i++) {
            slowly();
            System.out.println(c);
        }
        System.out.println("");
        System.out.println("    handle(" + count + ", " + c + ") end");
    }

    public void slowly() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
    }
}
