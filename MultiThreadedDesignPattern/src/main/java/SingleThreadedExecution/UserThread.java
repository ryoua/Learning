package SingleThreadedExecution;

public class UserThread extends Thread{
    private final String name;
    private final String address;
    private final Gate gate;

    public UserThread(String name, String address, Gate gate) {
        this.name = name;
        this.address = address;
        this.gate = gate;
    }

    @Override
    public void run() {
        System.out.println(name + "begin");
        while (true)
            gate.pass(name, address);
    }
}
