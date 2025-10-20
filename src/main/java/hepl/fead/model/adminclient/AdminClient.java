package hepl.fead.model.adminclient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Point d'entrée principal du Client Admin Java
 * Lance l'application Swing et configure les interactions
 */
public class AdminClient {
    // Configuration par défaut du serveur
    private static final String DEFAULT_SERVER_HOST = "192.168.129.163";
    private static final int DEFAULT_SERVER_PORT = 8081;

    private AdminClientUI ui;
    private AdminClientController controller;

    /**
     * Constructeur principal
     */
    public AdminClient() {
        initializeApplication();
    }

    /**
     * Initialise l'application complète
     */
    private void initializeApplication() {
        try {
            // Configurer le Look and Feel système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossible de définir le Look and Feel système : " + e.getMessage());
            // Continuer avec le Look and Feel par défaut
        }

        // Créer l'interface utilisateur
        ui = new AdminClientUI();

        // Créer le contrôleur
        controller = new AdminClientController(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT, ui);

        // Configurer les interactions
        setupEventHandlers();

        // Afficher la fenêtre
        ui.setVisible(true);

        System.out.println("Client Admin démarré - Serveur : " + DEFAULT_SERVER_HOST + ":" + DEFAULT_SERVER_PORT);
    }

    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Gestionnaire pour le bouton "Actualiser"
        ui.addRefreshButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton 'Actualiser' cliqué");
                controller.refreshClientList();
            }
        });

        // Gestionnaire pour la fermeture de la fenêtre
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Gestionnaire pour les touches (optionnel)
        setupKeyboardShortcuts();
    }

    /**
     * Configure les raccourcis clavier (optionnel)
     */
    private void setupKeyboardShortcuts() {
        // Raccourci F5 pour actualiser
        ui.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refresh");
        ui.getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Raccourci F5 - Actualisation");
                controller.refreshClientList();
            }
        });

        // Raccourci Ctrl+Q pour quitter
        ui.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl Q"), "quit");
        ui.getRootPane().getActionMap().put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Raccourci Ctrl+Q - Fermeture");
                System.exit(0);
            }
        });
    }

    /**
     * Méthode principale - point d'entrée de l'application
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Vérifier les arguments de ligne de commande
        final String serverHost = (args.length >= 1) ? args[0] : DEFAULT_SERVER_HOST;

        final int serverPort;
        if (args.length >= 2) {
            int parsedPort;
            try {
                parsedPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide : " + args[1] + ". Utilisation du port par défaut : " + DEFAULT_SERVER_PORT);
                parsedPort = DEFAULT_SERVER_PORT;
            }
            serverPort = parsedPort;
        } else {
            serverPort = DEFAULT_SERVER_PORT;
        }

        // Afficher les informations de démarrage
        System.out.println("========================================");
        System.out.println("    CLIENT ADMIN - HÔPITAL");
        System.out.println("========================================");
        System.out.println("Serveur : " + serverHost + ":" + serverPort);
        System.out.println("Protocole : ACBP (Admin Consultation Booking Protocol)");
        System.out.println("Commande : LIST_CLIENTS");
        System.out.println("========================================");

        // Lancer l'application dans l'Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    AdminClient app = new AdminClient();
                    // Mettre à jour la configuration du serveur si spécifiée
                    if (!serverHost.equals(DEFAULT_SERVER_HOST) || serverPort != DEFAULT_SERVER_PORT) {
                        app.controller.updateServerConfig(serverHost, serverPort);
                        System.out.println("Configuration serveur mise à jour : " + serverHost + ":" + serverPort);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du démarrage de l'application : " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Erreur lors du démarrage de l'application :\n" + e.getMessage(),
                            "Erreur de démarrage",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        });
    }

    /**
     * Obtient l'interface utilisateur (pour les tests)
     * @return Interface utilisateur
     */
    public AdminClientUI getUI() {
        return ui;
    }

    /**
     * Obtient le contrôleur (pour les tests)
     * @return Contrôleur
     */
    public AdminClientController getController() {
        return controller;
    }
}
