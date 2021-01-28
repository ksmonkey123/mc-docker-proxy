package ch.awae.minecraft.dockerproxy;

import ch.awae.minecraft.dockerproxy.threads.InputThread;
import ch.awae.minecraft.dockerproxy.threads.OutputThread;
import ch.awae.minecraft.dockerproxy.threads.WatchdogTimerThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerIntegrationRunner implements CommandLineRunner {

    private final InputThread inputProxy;
    private final List<OutputThread> outputThreads;
    private final WatchdogTimerThread watchdog;
    private final ProcessWrapper process;

    @Autowired
    public ServerIntegrationRunner(
            InputThread inputThread,
            List<OutputThread> outputThreads,
            WatchdogTimerThread watchdogTimerThread,
            ProcessWrapper processWrapper) {
        this.inputProxy = inputThread;
        this.outputThreads = outputThreads;
        this.watchdog = watchdogTimerThread;
        this.process = processWrapper;
    }

    @Override
    public void run(String... args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!process.isAlive())
                return;
            Log.proxy("received shutdown signal");
            for (int i = 10; i > 0; i--) {
                String c = String.format("say server stopping in %2d seconds", i);
                Log.proxy(c);
                inputProxy.relay(c);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.proxy("stop");
            inputProxy.relay("stop");
            try {
                int exit = process.waitFor();
                Log.proxy("server exited with status " + exit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        outputThreads.forEach(t -> t.setDaemon(true));
        inputProxy.setDaemon(true);
        watchdog.setDaemon(true);

        outputThreads.forEach(Thread::start);
        inputProxy.start();
        watchdog.start();

        new Thread(() -> {
            int exit = 0;
            try {
                exit = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(exit);
            Log.proxy("server exited with status " + exit);
        }).start();
    }
}
