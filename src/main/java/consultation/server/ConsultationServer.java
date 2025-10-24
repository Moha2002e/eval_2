package consultation.server;

import hepl.fead.model.config.DatabaseConfig;
import hepl.fead.model.dao.DAOFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConsultationServer implements Runnable {
    private final ConsultationServerConfig config;
    private final ClientThreadPool threadPool;
    private final ConnectionQueue connectionQueue;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public ConsultationServer(ConsultationServerConfig config) {
        this.config = config;
        this.connectionQueue = new ConnectionQueue();
        DatabaseConfig.init(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
        DAOFactory daoFactory = new DAOFactory();
        this.threadPool = new ClientThreadPool(config.getServerThreads(), connectionQueue,
                () -> new consultation.server.protocol.CAPProtocol(daoFactory));
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket ss = new ServerSocket(config.getServerPort())) {
            this.serverSocket = ss;
            System.out.printf("Serveur Consultation démarré sur le port %d avec %d threads.%n",
                    config.getServerPort(), config.getServerThreads());
            threadPool.start();
            while (running) {
                try {
                    Socket client = ss.accept();
                    connectionQueue.addConnection(client);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erreur lors de l'acceptation d'une connexion: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Impossible de démarrer le serveur: " + e.getMessage());
        } finally {
            threadPool.stop();
            DatabaseConfig.close();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignore) {
        }
        threadPool.stop();
    }
}
