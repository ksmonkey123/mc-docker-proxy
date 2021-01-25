package ch.awae.minecraft.dockerproxy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

class InputThread extends Thread {

    private final String path;
    private final PrintWriter writer;

    InputThread(String path, OutputStream stream) {
        this.path = path;
        this.writer = new PrintWriter(stream);
    }

    @Override
    public void run() {
        Log.proxy("input thread started");
        while (!Thread.interrupted()) {
            File f = new File(path);
            if (f.exists() && f.canRead() && f.isFile()) {
                try {
                    List<String> lines = Files.readAllLines(f.toPath());
                    for (String string : lines) {
                        Log.proxy(string);
                        writer.println(string);
                        writer.flush();
                    }
                    f.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        Log.proxy("input thread terminated");
    }

    void relay(String line) {
        writer.println(line);
        writer.flush();
    }
}
