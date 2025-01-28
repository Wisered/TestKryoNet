package behind;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 12345;
    private static final int DISCOVERY_PORT = 12346;
    private static final String DISCOVERY_MESSAGE = "GameServer";

    public static void startServer() {
        new Thread(GameServer::sendDiscoveryMessages).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de jeu lancé sur le port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendDiscoveryMessages() {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = DISCOVERY_MESSAGE.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
            while (true) {
                socket.send(packet);
                Thread.sleep(5000); // Envoie un message toutes les 5 secondes
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu : " + message);
                // Traiter le message et envoyer des réponses si nécessaire
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}