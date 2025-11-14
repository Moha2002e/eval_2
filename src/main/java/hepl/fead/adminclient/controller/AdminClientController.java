package hepl.fead.adminclient.controller;

import hepl.fead.adminclient.view.AdminClientUI;
import hepl.fead.adminclient.model.ClientInfo;
import hepl.fead.adminclient.config.AdminClientConfig;

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
    
    private String serverHost;
    private int serverPort;
    private AdminClientUI ui;
    private boolean isConnected = false;
    
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
     * Se connecte au serveur
     */
    public void connectToServer() {
        if (isConnected) {
            ui.showInfoDialog("Information", AdminClientConfig.INFO_ALREADY_CONNECTED);
            return;
        }
        
        ui.setConnectionStatus(AdminClientConfig.STATUS_CONNECTING);
        
        // Exécuter la connexion dans un thread séparé
        SwingUtilities.invokeLater(() -> {
            try {
                // Test de connexion simple
                testConnection();
                isConnected = true;
                ui.updateConnectionState(true);
                ui.showInfoDialog("Connexion réussie", String.format(AdminClientConfig.INFO_CONNECTION_SUCCESS, serverHost, serverPort));
                System.out.println(String.format(AdminClientConfig.LOG_CONNECTION_ESTABLISHED, serverHost, serverPort));
            } catch (Exception e) {
                handleError(AdminClientConfig.ERROR_CONNECTION_TITLE, e.getMessage());
                isConnected = false;
                ui.updateConnectionState(false);
            }
        });
    }
    
    /**
     * Se déconnecte du serveur
     */
    public void disconnectFromServer() {
        if (!isConnected) {
            ui.showInfoDialog("Information", AdminClientConfig.INFO_ALREADY_DISCONNECTED);
            return;
        }
        
        isConnected = false;
        ui.updateConnectionState(false);
        ui.updateClientList(new ArrayList<>()); // Vider la liste des clients
        ui.showInfoDialog("Déconnexion", AdminClientConfig.INFO_DISCONNECTION_SUCCESS);
        System.out.println(AdminClientConfig.LOG_DISCONNECTED);
    }
    
    /**
     * Actualise la liste des clients connectés
     * Cette méthode est appelée quand l'utilisateur clique sur "Actualiser"
     */
    public void refreshClientList() {
        if (!isConnected) {
            ui.showErrorDialog("Erreur", AdminClientConfig.ERROR_NOT_CONNECTED);
            return;
        }
        
        // Désactiver le bouton pendant la requête
        ui.setRefreshButtonEnabled(false);
        ui.setConnectionStatus(AdminClientConfig.STATUS_REFRESHING);

        SwingUtilities.invokeLater(() -> {
            try {
                List<ClientInfo> clients = fetchClientListFromServer();
                ui.updateClientList(clients);
            } catch (Exception e) {
                handleError(AdminClientConfig.ERROR_COMMUNICATION_TITLE, e.getMessage());
                // En cas d'erreur, considérer comme déconnecté
                isConnected = false;
                ui.updateConnectionState(false);
            } finally {
                // Réactiver le bouton
                ui.setRefreshButtonEnabled(true);
            }
        });
    }
    
    /**
     * Teste la connexion au serveur
     * @throws Exception En cas d'erreur de connexion
     */
    private void testConnection() throws Exception {
        Socket socket = null;
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("🔌 TEST DE CONNEXION AU SERVEUR C");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("📍 Hôte    : " + serverHost);
        System.out.println("🔌 Port    : " + serverPort);
        System.out.println("⏱️  Timeout : " + AdminClientConfig.CONNECTION_TIMEOUT + " ms");
        System.out.println("───────────────────────────────────────────────────────────");

        try {
            socket = new Socket();
            System.out.println("⏳ Tentative de connexion en cours...");
            long startTime = System.currentTimeMillis();
            socket.connect(new java.net.InetSocketAddress(serverHost, serverPort), AdminClientConfig.CONNECTION_TIMEOUT);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("✅ Connexion établie avec succès en " + duration + " ms");
            socket.close();
            System.out.println("═══════════════════════════════════════════════════════════");
        } catch (SocketTimeoutException e) {
            System.err.println("❌ TIMEOUT : Le serveur n'a pas répondu dans le délai imparti");
            System.err.println("───────────────────────────────────────────────────────────");
            System.err.println("💡 Vérifiez que :");
            System.err.println("   1. Le serveur C est bien démarré sur " + serverHost + ":" + serverPort);
            System.err.println("   2. L'adresse IP " + serverHost + " est correcte");
            System.err.println("   3. Le port " + serverPort + " n'est pas bloqué par un firewall");
            System.err.println("   4. Vous pouvez pinguer l'adresse : ping " + serverHost);
            System.err.println("═══════════════════════════════════════════════════════════");
            throw new Exception(String.format(AdminClientConfig.ERROR_CONNECTION_TIMEOUT, AdminClientConfig.CONNECTION_TIMEOUT));
        } catch (java.net.ConnectException e) {
            System.err.println("❌ CONNEXION REFUSÉE : " + e.getMessage());
            System.err.println("───────────────────────────────────────────────────────────");
            System.err.println("💡 Causes possibles :");
            System.err.println("   1. Le serveur C n'est PAS démarré");
            System.err.println("   2. Le serveur écoute sur un autre port");
            System.err.println("   3. L'adresse IP est incorrecte");
            System.err.println("═══════════════════════════════════════════════════════════");
            throw new Exception(String.format(AdminClientConfig.ERROR_CONNECTION_REFUSED, serverHost, serverPort));
        } catch (java.net.UnknownHostException e) {
            System.err.println("❌ HÔTE INCONNU : " + serverHost);
            System.err.println("───────────────────────────────────────────────────────────");
            System.err.println("💡 L'adresse IP ou le nom d'hôte est invalide");
            System.err.println("   Vérifiez l'orthographe et que le serveur existe");
            System.err.println("═══════════════════════════════════════════════════════════");
            throw new Exception("Hôte inconnu : " + serverHost);
        } catch (IOException e) {
            System.err.println("❌ ERREUR I/O : " + e.getMessage());
            System.err.println("═══════════════════════════════════════════════════════════");
            throw new Exception(String.format(AdminClientConfig.ERROR_COMMUNICATION, e.getMessage()));
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println(String.format(AdminClientConfig.LOG_CLOSE_ERROR, "socket de test", e.getMessage()));
                }
            }
        }
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
            System.out.println(String.format(AdminClientConfig.LOG_CONNECTION_ATTEMPT, serverHost, serverPort));
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(serverHost, serverPort), AdminClientConfig.CONNECTION_TIMEOUT);
            System.out.println(AdminClientConfig.LOG_CONNECTION_SUCCESS);
            socket.setSoTimeout(AdminClientConfig.READ_TIMEOUT);
            
            // Créer les flux d'entrée/sortie
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Envoyer la commande
            ui.setConnectionStatus("Envoi de la commande LIST_CLIENTS...");
            out.println(AdminClientConfig.COMMAND_LIST_CLIENTS);
            
            // Lire la réponse
            ui.setConnectionStatus("Lecture de la réponse du serveur...");
            String response = readServerResponse(in);
            
            // Parser la réponse
            List<ClientInfo> clients = parseClientList(response);
            
            ui.setConnectionStatus("Données reçues avec succès");
            return clients;
            
        } catch (SocketTimeoutException e) {
            throw new Exception(String.format(AdminClientConfig.ERROR_CONNECTION_TIMEOUT, AdminClientConfig.CONNECTION_TIMEOUT));
        } catch (java.net.ConnectException e) {
            throw new Exception(String.format(AdminClientConfig.ERROR_CONNECTION_REFUSED, serverHost, serverPort));
        } catch (IOException e) {
            throw new Exception(String.format(AdminClientConfig.ERROR_COMMUNICATION, e.getMessage()));
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
            System.out.println(String.format(AdminClientConfig.LOG_SERVER_RESPONSE, line));
        }
        
        // Essayer de lire d'autres lignes si elles existent
        try {
            while ((line = in.readLine()) != null) {
                response.append(AdminClientConfig.LINE_SEPARATOR).append(line);
                System.out.println(String.format(AdminClientConfig.LOG_ADDITIONAL_LINE, line));
            }
        } catch (IOException e) {
            // Fin de flux, c'est normal
            System.out.println(AdminClientConfig.LOG_END_OF_STREAM);
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
        
        System.out.println(String.format(AdminClientConfig.LOG_RAW_RESPONSE, response));
        
        // Parser le format du serveur C : "LIST_CLIENTS#ok#données"
        String[] parts = response.split(AdminClientConfig.COMMAND_SEPARATOR, 3);
        if (parts.length < 3) {
            throw new Exception(String.format(AdminClientConfig.ERROR_INVALID_RESPONSE, response));
        }
        
        String commande = parts[0].trim();
        String status = parts[1].trim();
        String data = parts[2].trim();
        
        System.out.println(String.format(AdminClientConfig.LOG_COMMAND_PARSED, commande));
        System.out.println(String.format(AdminClientConfig.LOG_STATUS_PARSED, status));
        System.out.println(String.format(AdminClientConfig.LOG_DATA_PARSED, data));
        
        if (!AdminClientConfig.EXPECTED_COMMAND.equals(commande)) {
            throw new Exception(String.format(AdminClientConfig.ERROR_UNEXPECTED_COMMAND, commande));
        }
        
        if (!AdminClientConfig.SUCCESS_STATUS.equals(status)) {
            throw new Exception(String.format(AdminClientConfig.ERROR_SERVER_ERROR, status));
        }
        
        // Parser les données : "IP;Nom;Prénom;ID\nIP;Nom;Prénom;ID\n..."
        String[] lines = data.split(AdminClientConfig.LINE_SEPARATOR);
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || AdminClientConfig.EMPTY_CLIENT_MESSAGE.equals(line)) {
                continue; // Ignorer les lignes vides ou le message d'absence
            }
            
            try {
                ClientInfo client = parseClientLine(line);
                if (client != null) {
                    clients.add(client);
                }
            } catch (Exception e) {
                System.err.println(String.format(AdminClientConfig.LOG_PARSING_ERROR, line, e.getMessage()));
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
        String[] parts = line.split(AdminClientConfig.DATA_SEPARATOR);
        
        if (parts.length != AdminClientConfig.EXPECTED_COLUMNS) {
            throw new Exception(AdminClientConfig.ERROR_INVALID_LINE_FORMAT);
        }
        
        String ipAddress = parts[0].trim();
        String lastName = parts[1].trim();
        String firstName = parts[2].trim();
        
        // Validation de l'IP (basique)
        if (ipAddress.isEmpty()) {
            throw new Exception(AdminClientConfig.ERROR_EMPTY_IP);
        }
        
        // Validation des noms
        if (lastName.isEmpty() || firstName.isEmpty()) {
            throw new Exception(AdminClientConfig.ERROR_EMPTY_NAME);
        }
        
        // Parsing de l'ID patient
        int patientId;
        try {
            patientId = Integer.parseInt(parts[3].trim());
            if (patientId < AdminClientConfig.MIN_PATIENT_ID) {
                throw new Exception(String.format(AdminClientConfig.ERROR_INVALID_PATIENT_ID, patientId));
            }
        } catch (NumberFormatException e) {
            throw new Exception(String.format(AdminClientConfig.ERROR_NON_NUMERIC_PATIENT_ID, parts[3]));
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
            System.err.println(String.format(AdminClientConfig.LOG_CLOSE_ERROR, "BufferedReader", e.getMessage()));
        }
        
        try {
            if (out != null) out.close();
        } catch (Exception e) {
            System.err.println(String.format(AdminClientConfig.LOG_CLOSE_ERROR, "PrintWriter", e.getMessage()));
        }
        
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println(String.format(AdminClientConfig.LOG_CLOSE_ERROR, "Socket", e.getMessage()));
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
    
    /**
     * Vérifie si le client est connecté au serveur
     * @return true si connecté, false sinon
     */
    public boolean isConnected() {
        return isConnected;
    }
}
