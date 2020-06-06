package GuardedSuspension;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 13:20
 **/
public class Main {
    public static void main(String[] args) {
        RequestQueue requestQueue = new RequestQueue();
        new ClientThread(requestQueue, "Alice", 3132342).start();
        new ServerThread(requestQueue, "Bobby", 1231231).start();
    }
}
