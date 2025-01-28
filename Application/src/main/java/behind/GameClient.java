package behind;

import java.io.*;
import java.net.*;

public class GameClient {
    private static final int DISCOVERY_PORT = 12346;
    private static final int SERVER_PORT = 12345;

    public static void startClient() {
        new Thread(GameClient::discoverServers).start();
    }

    private static void discoverServers() {
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Recherche de serveurs...");
            while (true) {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                if (message.equals("GameServer")) {
                    String serverAddress = packet.getAddress().getHostAddress();
                    System.out.println("Serveur trouvé à l'adresse : " + serverAddress);
                    connectToServer(serverAddress);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToServer(String serverAddress) {
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