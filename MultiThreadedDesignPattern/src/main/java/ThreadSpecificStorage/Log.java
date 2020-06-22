package ThreadSpecificStorage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/15
 **/
public class Log {
    private static PrintWriter printWriter = null;

    static {
        try {
            printWriter = new PrintWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void println(String s) {
        printWriter.println(s);
    }

    public static void close() {
        printWriter.println("====end====");
        printWriter.close();
    }
}
