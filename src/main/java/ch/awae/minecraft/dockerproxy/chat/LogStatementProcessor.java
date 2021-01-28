package ch.awae.minecraft.dockerproxy.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogStatementProcessor {

    private final Pattern chatPattern;
    private final DiscordBotService bot;

    @Autowired
    public LogStatementProcessor(DiscordBotService bot) {
        this.bot = bot;
        this.chatPattern = Pattern.compile("^(?:\\[.+\\] ){2}\\[[^\\<]+\\]\\: \\<(\\w+)\\> (.*)$");
    }


    public void process(String line) {
        Matcher matcher = chatPattern.matcher(line);
        if (matcher.matches()) {
            String user = matcher.group(1);
            String message = matcher.group(2);

            bot.sendMessage(user, message);
        }
    }



}
