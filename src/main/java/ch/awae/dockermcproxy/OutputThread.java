package ch.awae.dockermcproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class OutputThread extends Thread {

    private final BufferedReader reader;

    OutputThread(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
    }

    @Override
    public void run() {
        Log.proxy("output thread started");
        String line = null;
        try {
            while ((line = reader.readLine()) != null && !Thread.interrupted()) {
                Log.server(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.proxy("output thread terminated");
    }

}
