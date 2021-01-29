package ch.awae.minecraft.dockerproxy.chat;

import ch.awae.minecraft.dockerproxy.DiscordBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscordBotService {

    private final RestTemplate http;
    private final DiscordBotConfig config;

    @Autowired
    public DiscordBotService(RestTemplate http, DiscordBotConfig config) {
        this.http = http;
        this.config = config;
    }

    public void sendLogLine(String line, boolean error) {
        http.postForObject(config.getUrl() + "/log", new LogRequest(line, config.getToken(), error), Object.class);
    }

    static class LogRequest {
        public String message;
        public String token;
        public boolean error;

        public LogRequest(String message, String token, boolean error) {
            this.message = message;
            this.token = token;
            this.error = error;
        }
    }

}
