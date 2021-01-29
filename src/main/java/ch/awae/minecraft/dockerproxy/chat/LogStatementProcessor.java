package ch.awae.minecraft.dockerproxy.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
public class LogStatementProcessor {

    private final DiscordBotService bot;
    private final ExecutorService service;

    @Autowired
    public LogStatementProcessor(DiscordBotService bot, ExecutorService service) {
        this.bot = bot;
        this.service = service;
    }

    public void process(String line, boolean isError) {
        service.submit(() -> bot.sendLogLine(line, isError));
    }

}
