package GuardedSuspension;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 13:19
 **/
public class Request {
    private final String name;

    public Request(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                '}';
    }
}
