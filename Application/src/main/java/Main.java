public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <server|client>");
            return;
        }

        if ("server".equalsIgnoreCase(args[0])) {
            behind.GameServer.startServer();
        } else if ("client".equalsIgnoreCase(args[0])) {
            behind.GameClient.startClient();
        } else {
            System.out.println("Argument inconnu. Utilisez 'server' ou 'client'.");
        }
    }
}