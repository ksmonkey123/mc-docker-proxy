package ch.awae.minecraft.dockerproxy;

class Log {

    static void server(String line) {
        System.out.println("[server] " + line);
    }

    static void proxy(String line) {
        System.out.println("[proxy ] " + line);
    }

}
