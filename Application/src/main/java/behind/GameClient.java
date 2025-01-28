package behind;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameClient {
    private static final int DISCOVERY_PORT = 12346;
    private static final int SERVER_PORT = 12345;

    public static void discoverAndJoinServer() {
        List<String> servers = discoverServers();
        if (servers.isEmpty()) {
            System.out.println("Aucun serveur trouvé.");
            return;
        }

        System.out.println("Serveurs disponibles :");
        for (int i = 0; i < servers.size(); i++) {
            System.out.println((i + 1) + ". " + servers.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Choisissez un serveur à rejoindre (1-" + servers.size() + "): ");
        int choice = scanner.nextInt();
        if (choice < 1 || choice > servers.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        String serverAddress = servers.get(choice - 1);
        startClient(serverAddress);
    }

    private static List<String> discoverServers() {
        List<String> servers = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            socket.setSoTimeout(5000); // Timeout de 5 secondes pour la découverte
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Recherche de serveurs...");
            while (true) {
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if (message.equals("GameServer")) {
                        String serverAddress = packet.getAddress().getHostAddress();
                        if (!servers.contains(serverAddress)) {
                            servers.add(serverAddress);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break; // Arrêter la recherche après le timeout
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return servers;
    }

    public static void startClient(String serverAddress) {
        try (Socket socket = new Socket(serverAddress, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}