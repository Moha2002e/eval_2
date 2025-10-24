package consultation.client;

import consultation.server.protocol.Requete;
import consultation.server.protocol.ReponseTraitee;
import consultation.server.protocol.RequeteLogout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkManager {
    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    public NetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
    }
    public synchronized ReponseTraitee sendRequest(Requete req) throws IOException, ClassNotFoundException {
        output.writeObject(req);
        output.flush();
        Object resp = input.readObject();
        if (resp instanceof ReponseTraitee) {
            return (ReponseTraitee) resp;
        }
        throw new IOException("Réponse inconnue reçue du serveur");
    }
    public void close() {
        try {
            if (output != null) {
                output.writeObject(new RequeteLogout());
                output.flush();
            }
        } catch (IOException ignore) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignore) {
        }
    }
}
