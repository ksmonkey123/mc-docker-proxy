package ch.awae.minecraft.dockerproxy.chat;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class LogStatementProcessorTest {

    @Test
    public void patternMatches() {
        Pattern compile = Pattern.compile("^(?:\\[.+\\] ){2}\\[[^\\<]+\\]\\: \\<(\\w+)\\> (.*)$");
        Matcher matcher = compile.matcher("[11:46:28] [Server thread/INFO] [minecraft/DedicatedServer]: <ksmonkey123> [asfd]: <judos_ch> hi there");
    }

}