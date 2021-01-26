package ch.awae.minecraft.dockerproxy.threads;

import ch.awae.minecraft.dockerproxy.Log;
import ch.awae.minecraft.dockerproxy.api.InputRelay;
import ch.awae.minecraft.dockerproxy.api.WatchdogTimer;

public class WatchdogTimerImpl extends Thread implements WatchdogTimer {

    private final InputRelay relay;
    private final Process process;

    private final Object LOCK = new Object();

    public WatchdogTimerImpl(InputRelay relay, Process process) {
        this.relay = relay;
        this.process = process;
    }

    @Override
    public void refresh() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    @Override
    public void run() {
        int missedDeadlines = 0;
        while (true) {
            synchronized (LOCK) {
                try {
                    long start = System.currentTimeMillis();
                    LOCK.wait(100000);
                    long end = System.currentTimeMillis();

                    if (end - start >= 100000) {
                        Log.proxy("watchdog triggered");
                        relay.relay(".");
                        missedDeadlines++;
                        if (missedDeadlines >= 3) {
                            Log.proxy("server missed 3 deadlines and did not respond to test signal");
                            Log.proxy("killing server forcefully");
                            process.destroyForcibly();
                            return;
                        }
                    } else {
                        if (missedDeadlines > 0) {
                            Log.proxy("watchdog reset");
                        }
                        missedDeadlines = 0;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
