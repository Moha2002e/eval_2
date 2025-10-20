package hepl.fead.model.adminclient;

import javax.swing.SwingUtilities;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur pour gérer la communication réseau avec le serveur C
 * Gère les connexions TCP et le parsing des réponses
 */
public class AdminClientController {
    private static final String COMMAND_LIST_CLIENTS = "LIST_CLIENTS#";
    private static final int CONNECTION_TIMEOUT = 100; // 10 secondes
    private static final int READ_TIMEOUT = 300; // 30 secondes

    private String serverHost;
    private int serverPort;
    private AdminClientUI ui;

    /**
     * Constructeur du contrôleur
     * @param serverHost Adresse du serveur C
     * @param serverPort Port du serveur C
     * @param ui Interface utilisateur à contrôler
     */
    public AdminClientController(String serverHost, int serverPort, AdminClientUI ui) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.ui = ui;
    }

    /**
     * Actualise la liste des clients connectés
     * Cette méthode est appelée quand l'utilisateur clique sur "Actualiser"
     */
    public void refreshClientList() {
        // Désactiver le bouton pendant la requête
        ui.setRefreshButtonEnabled(false);
        ui.setConnectionStatus("Connexion au serveur...");

        // Exécuter la requête dans un thread séparé pour éviter le blocage de l'UI
        SwingUtilities.invokeLater(() -> {
            try {
                List<ClientInfo> clients = fetchClientListFromServer();
                ui.updateClientList(clients);
            } catch (Exception e) {
                handleError("Erreur de communication", e.getMessage());
            } finally {
                // Réactiver le bouton
                ui.setRefreshButtonEnabled(true);
            }
        });
    }

    /**
     * Récupère la liste des clients depuis le serveur
     * @return Liste des clients connectés
     * @throws Exception En cas d'erreur de communication
     */
    private List<ClientInfo> fetchClientListFromServer() throws Exception {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Établir la connexion
            ui.setConnectionStatus("Connexion au serveur " + serverHost + ":" + serverPort + "...");
            System.out.println("Tentative de connexion à " + serverHost + ":" + serverPort);
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(serverHost, serverPort), CONNECTION_TIMEOUT);
            System.out.println("Connexion établie avec succès !");
            socket.setSoTimeout(READ_TIMEOUT);

            // Créer les flux d'entrée/sortie
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Envoyer la commande
            ui.setConnectionStatus("Envoi de la commande LIST_CLIENTS...");
            out.println(COMMAND_LIST_CLIENTS);

            // Lire la réponse
            ui.setConnectionStatus("Lecture de la réponse du serveur...");
            String response = readServerResponse(in);

            // Parser la réponse
            List<ClientInfo> clients = parseClientList(response);

            ui.setConnectionStatus("Données reçues avec succès");
            return clients;

        } catch (SocketTimeoutException e) {
            throw new Exception("Timeout de connexion au serveur (" + CONNECTION_TIMEOUT + "ms)");
        } catch (java.net.ConnectException e) {
            throw new Exception("Impossible de se connecter au serveur. Vérifiez que le serveur C est démarré sur " + serverHost + ":" + serverPort);
        } catch (IOException e) {
            throw new Exception("Erreur de communication avec le serveur : " + e.getMessage());
        } finally {
            // Fermer proprement les ressources
            closeResources(socket, out, in);
        }
    }

    /**
     * Lit la réponse complète du serveur
     * @param in BufferedReader pour lire la réponse
     * @return Réponse complète du serveur
     * @throws IOException En cas d'erreur de lecture
     */
    private String readServerResponse(BufferedReader in) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;

        // Lire la première ligne (qui contient toute la réponse)
        if ((line = in.readLine()) != null) {
            response.append(line);
            System.out.println("Ligne reçue du serveur : " + line);
        }

        // Essayer de lire d'autres lignes si elles existent
        try {
            while ((line = in.readLine()) != null) {
                response.append("\n").append(line);
                System.out.println("Ligne supplémentaire reçue : " + line);
            }
        } catch (IOException e) {
            // Fin de flux, c'est normal
            System.out.println("Fin de flux détectée");
        }

        return response.toString().trim();
    }

    /**
     * Parse la réponse du serveur en liste de ClientInfo
     * Format du serveur C : "LIST_CLIENTS#ok#IP;Nom;Prénom;ID\nIP;Nom;Prénom;ID\n..."
     * @param response Réponse brute du serveur
     * @return Liste des clients connectés
     * @throws Exception En cas d'erreur de parsing
     */
    private List<ClientInfo> parseClientList(String response) throws Exception {
        List<ClientInfo> clients = new ArrayList<>();

        if (response == null || response.trim().isEmpty()) {
            return clients; // Liste vide si pas de réponse
        }

        System.out.println("Réponse brute du serveur : " + response);

        // Parser le format du serveur C : "LIST_CLIENTS#ok#données"
        String[] parts = response.split("#", 3);
        if (parts.length < 3) {
            throw new Exception("Format de réponse invalide du serveur : " + response);
        }

        String commande = parts[0].trim();
        String status = parts[1].trim();
        String data = parts[2].trim();

        System.out.println("Commande : " + commande);
        System.out.println("Status : " + status);
        System.out.println("Données : " + data);

        if (!"LIST_CLIENTS".equals(commande)) {
            throw new Exception("Commande inattendue : " + commande);
        }

        if (!"ok".equals(status)) {
            throw new Exception("Erreur du serveur : " + status);
        }

        // Parser les données : "IP;Nom;Prénom;ID\nIP;Nom;Prénom;ID\n..."
        String[] lines = data.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || "Aucun client connecté".equals(line)) {
                continue; // Ignorer les lignes vides ou le message d'absence
            }

            try {
                ClientInfo client = parseClientLine(line);
                if (client != null) {
                    clients.add(client);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing de la ligne : " + line + " - " + e.getMessage());
                // Continuer avec les autres lignes même si une ligne est malformée
            }
        }

        return clients;
    }

    /**
     * Parse une ligne de réponse en objet ClientInfo
     * @param line Ligne à parser (format : "IP;Nom;Prénom;ID")
     * @return ClientInfo créé ou null si la ligne est invalide
     * @throws Exception En cas d'erreur de parsing
     */
    private ClientInfo parseClientLine(String line) throws Exception {
        String[] parts = line.split(";");

        if (parts.length != 4) {
            throw new Exception("Format de ligne invalide. Attendu : IP;Nom;Prénom;ID");
        }

        String ipAddress = parts[0].trim();
        String lastName = parts[1].trim();
        String firstName = parts[2].trim();

        // Validation de l'IP (basique)
        if (ipAddress.isEmpty()) {
            throw new Exception("Adresse IP vide");
        }

        // Validation des noms
        if (lastName.isEmpty() || firstName.isEmpty()) {
            throw new Exception("Nom ou prénom vide");
        }

        // Parsing de l'ID patient
        int patientId;
        try {
            patientId = Integer.parseInt(parts[3].trim());
            if (patientId <= 0) {
                throw new Exception("ID patient invalide : " + patientId);
            }
        } catch (NumberFormatException e) {
            throw new Exception("ID patient non numérique : " + parts[3]);
        }

        return new ClientInfo(ipAddress, lastName, firstName, patientId);
    }

    /**
     * Ferme proprement les ressources réseau
     * @param socket Socket à fermer
     * @param out PrintWriter à fermer
     * @param in BufferedReader à fermer
     */
    private void closeResources(Socket socket, PrintWriter out, BufferedReader in) {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture du BufferedReader : " + e.getMessage());
        }

        try {
            if (out != null) out.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du PrintWriter : " + e.getMessage());
        }

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture du Socket : " + e.getMessage());
        }
    }

    /**
     * Gère les erreurs en affichant un message à l'utilisateur
     * @param title Titre de l'erreur
     * @param message Message d'erreur
     */
    private void handleError(String title, String message) {
        ui.setConnectionStatus("Erreur : " + message);
        ui.showErrorDialog(title, message);
    }

    /**
     * Met à jour la configuration du serveur
     * @param serverHost Nouvelle adresse du serveur
     * @param serverPort Nouveau port du serveur
     */
    public void updateServerConfig(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Obtient l'adresse du serveur configurée
     * @return Adresse du serveur
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * Obtient le port du serveur configuré
     * @return Port du serveur
     */
    public int getServerPort() {
        return serverPort;
    }
}
