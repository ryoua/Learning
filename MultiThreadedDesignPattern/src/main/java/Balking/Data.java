package Balking;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * * @Author: RyouA
 * * @Date: 2020/6/6 - 15:08
 **/
public class Data {
    private final String filename;
    private String content;
    private boolean changed;

    public Data(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.changed = true;
    }

    public synchronized void change(String content) {
        this.content = content;
        this.changed = true;
    }

    public synchronized void save() throws IOException {
        if (!changed)
            return;
        doSave();
        changed = false;
    }

    public void doSave() throws IOException {
        System.out.println(Thread.currentThread().getName() + " calls doSave(), content = "  + content);
        Writer writer = new FileWriter(filename);
        writer.write(content);
        writer.close();
    }
}
