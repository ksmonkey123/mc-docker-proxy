package ch.awae.minecraft.dockerproxy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("proxy")
public class ProxyConfig {

    private String inputFile;
    private String command;
    private DiscordBotConfig discordBot;

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public DiscordBotConfig getDiscordBot() {
        return discordBot;
    }

    public void setDiscordBot(DiscordBotConfig discordBot) {
        this.discordBot = discordBot;
    }
}
