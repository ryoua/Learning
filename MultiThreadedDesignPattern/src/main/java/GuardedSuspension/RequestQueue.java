package GuardedSuspension;

import java.util.LinkedList;
import java.util.Queue;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 13:20
 **/
public class RequestQueue {
    private final Queue<Request> queue = new LinkedList<Request>();

    public synchronized Request getRequest() {
        while (queue.peek() == null) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        return queue.remove();
    }


    public synchronized void putRequest(Request request) {
        queue.offer(request);
        notifyAll();
    }
}
