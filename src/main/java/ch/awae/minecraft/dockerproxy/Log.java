package ch.awae.minecraft.dockerproxy;

public class Log {

    public static void server(String line) {
        System.out.println("[server] " + line);
    }

    public static void proxy(String line) {
        System.out.println("[proxy ] " + line);
    }

}
