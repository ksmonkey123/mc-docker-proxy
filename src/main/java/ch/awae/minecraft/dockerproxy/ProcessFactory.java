package ch.awae.minecraft.dockerproxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ProcessFactory {

    @Bean
    public static ProcessWrapper createProcess(ProxyConfig config) throws IOException {
        return new ProcessWrapper(Runtime.getRuntime().exec(config.getCommand()));
    }

}
