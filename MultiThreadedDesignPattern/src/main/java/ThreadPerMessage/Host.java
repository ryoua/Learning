package ThreadPerMessage;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/8 - 15:49
 **/
public class Host {
    private final Helper helper = new Helper();
    public void request(final int count, final  char c) {
        System.out.println("    request(" + count + ", " + c + ") begin");
        new Thread() {
            @Override
            public void run() {
                helper.handle(count, c);
            }
        }.start();
    }
}
