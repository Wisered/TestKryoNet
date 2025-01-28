package behind;

import java.io.*;
import java.net.*;

public class GameClient {
    private static final String SERVER_ADDRESS = "192.168.1.2"; // Adresse IP du serveur
    private static final int PORT = 12345;

    public static void startClient() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("Bonjour, serveur !");
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Réponse du serveur : " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}