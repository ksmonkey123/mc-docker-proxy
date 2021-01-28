package ch.awae.minecraft.dockerproxy.chat;

import ch.awae.minecraft.dockerproxy.api.InputRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomingChatMessageHandler {

    private final InputRelay relay;

    @Autowired
    public IncomingChatMessageHandler(InputRelay relay) {
        this.relay = relay;
    }

    @PostMapping("/message")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void incomingMessage(@RequestBody MessageRequest request) {
        relay.relay("say ["+ request.user + "] " + request.message);
    }

    static class MessageRequest {
        public String user;
        public String message;
    }

}
