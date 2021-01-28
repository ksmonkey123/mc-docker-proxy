package ch.awae.minecraft.dockerproxy.threads;

import ch.awae.minecraft.dockerproxy.ProcessWrapper;
import ch.awae.minecraft.dockerproxy.ProxyConfig;
import ch.awae.minecraft.dockerproxy.api.InputRelay;
import ch.awae.minecraft.dockerproxy.api.WatchdogTimer;
import ch.awae.minecraft.dockerproxy.chat.LogStatementProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadFactory {

    private final ProxyConfig config;
    private final ProcessWrapper processWrapper;

    @Autowired
    public ThreadFactory(ProxyConfig config, ProcessWrapper processWrapper) {
        this.config = config;
        this.processWrapper = processWrapper;
    }

    @Bean("inputThread")
    public InputThread createInputThread() {
        return new InputThread(config, processWrapper.getOutputStream());
    }

    @Bean("watchdogTimer")
    public WatchdogTimerThread createWatchdog(InputRelay relay) {
        return new WatchdogTimerThread(relay, processWrapper);
    }

    @Bean("outputThread")
    public OutputThread createOutputThread(WatchdogTimer watchdog, LogStatementProcessor processor) {
        return new OutputThread(processWrapper.getInputStream(), watchdog, processor);
    }

    @Bean("errorThread")
    public OutputThread createErrorThread(WatchdogTimer watchdogTimer) {
        return new OutputThread(processWrapper.getErrorStream(), watchdogTimer, null);
    }

}
