package consultation.server;

import consultation.server.protocol.CAPProtocol;
import consultation.server.protocol.ReponseTraitee;
import consultation.server.protocol.Requete;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionWorker implements Runnable {
    private final ConnectionQueue queue;
    private final CAPProtocol protocol;
    private volatile boolean running;
    public ConnectionWorker(ConnectionQueue queue, CAPProtocol protocol) {
        this.queue = queue;
        this.protocol = protocol;
        this.running = true;
    }
    @Override
    public void run() {
        while (running) {
            Socket clientSocket = null;
            try {
                clientSocket = queue.getConnection();
                handleClient(clientSocket);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Erreur de traitement client: " + e.getMessage());
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }
    private void handleClient(Socket socket) {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            while (running) {
                try {
                    Object obj = ois.readObject();
                    // Vérifier que l'objet est une requête valide
                    if (!(obj instanceof Requete)) {
                        System.err.println("Objet reçu inconnu: " + obj);
                        break;
                    }
                    Requete req = (Requete) obj;
                    ReponseTraitee resp = protocol.traiter(req);
                    oos.writeObject(resp);
                    oos.flush();
                    if (req.isLogout()) {
                        break;
                    }
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur de communication avec le client: " + e.getMessage());
        }
    }
    public void stop() {
        running = false;
    }
}
