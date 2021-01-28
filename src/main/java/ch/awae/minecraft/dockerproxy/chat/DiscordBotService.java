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

    public void sendMessage(String user, String message) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.token = config.getToken();
        messageRequest.user = user;
        messageRequest.message = message;
        http.postForObject(config.getUrl(), messageRequest, Object.class);
    }

    static class MessageRequest {
        public String user;
        public String message;
        public String token;
    }

}
