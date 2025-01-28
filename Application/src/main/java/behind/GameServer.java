package behind;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void startServer() {
        new Thread(GameServer::sendDiscoveryMessages).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de jeu lancé sur le port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendDiscoveryMessages() {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = "GameServer".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 12346);
            while (true) {
                socket.send(packet);
                Thread.sleep(5000); // Envoie un message toutes les 5 secondes
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        String fullMessage = sender.getClientAddress() + ": " + message;
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(fullMessage);
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Message reçu de " + getClientAddress() + ": " + message);
                    GameServer.broadcastMessage(message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(this);
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        public String getClientAddress() {
            return clientSocket.getInetAddress().getHostAddress();
        }
    }
}