package ch.awae.minecraft.dockerproxy.threads;

import ch.awae.minecraft.dockerproxy.Log;
import ch.awae.minecraft.dockerproxy.ProxyConfig;
import ch.awae.minecraft.dockerproxy.api.InputRelay;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

public class InputThread extends Thread implements InputRelay {

    private final String path;
    private final PrintWriter writer;

    public InputThread(ProxyConfig config, OutputStream stream) {
        this.path = config.getInputFile();
        System.out.println(this.path);
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
                    for (String line : lines) {
                        Log.proxy(line);
                        relay(line);
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

    @Override
    public void relay(String line) {
        writer.println(line);
        writer.flush();
    }
}
