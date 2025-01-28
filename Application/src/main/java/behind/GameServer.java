package behind;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 12345;

    public static void startServer() {
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