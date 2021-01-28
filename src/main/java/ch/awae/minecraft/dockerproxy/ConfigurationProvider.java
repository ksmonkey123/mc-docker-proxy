package ch.awae.minecraft.dockerproxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationProvider {

    @Bean
    public DiscordBotConfig getDiscordBotConfig(ProxyConfig config) {
        return config.getDiscordBot();
    }

}
