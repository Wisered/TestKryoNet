import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <host|join>");
            return;
        }

        if ("host".equalsIgnoreCase(args[0])) {
            new Thread(() -> behind.GameServer.startServer()).start();
            new Thread(() -> behind.GameClient.startClient("localhost")).start();
        } else if ("join".equalsIgnoreCase(args[0])) {
            behind.GameClient.discoverAndJoinServer();
        } else {
            System.out.println("Argument inconnu. Utilisez 'host' ou 'join'.");
        }
    }
}