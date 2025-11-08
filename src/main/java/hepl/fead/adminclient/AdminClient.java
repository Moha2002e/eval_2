package hepl.fead.adminclient;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import hepl.fead.adminclient.controller.AdminClientController;
import hepl.fead.adminclient.view.AdminClientUI;
import hepl.fead.adminclient.config.AdminClientConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Point d'entrée principal du Client Admin Java
 * Lance l'application Swing et configure les interactions
 */
public class AdminClient {
    
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
        // Configuration style macOS
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Admin Client");

        // Initialiser FlatLaf en mode SOMBRE
        FlatMacDarkLaf.setup();

        // Créer l'interface utilisateur
        ui = new AdminClientUI();
        
        // Créer le contrôleur
        controller = new AdminClientController(AdminClientConfig.DEFAULT_SERVER_HOST, AdminClientConfig.DEFAULT_SERVER_PORT, ui);
        
        // Configurer les interactions
        setupEventHandlers();
        
        // Afficher la fenêtre
        ui.setVisible(true);
        
        System.out.printf((AdminClientConfig.LOG_CLIENT_STARTED) + "%n", AdminClientConfig.DEFAULT_SERVER_HOST, AdminClientConfig.DEFAULT_SERVER_PORT);
    }
    
    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Gestionnaire pour le bouton "Se connecter"
        ui.addConnectButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_BUTTON_CLICKED, AdminClientConfig.BUTTON_CONNECT_TEXT));
                controller.connectToServer();
            }
        });
        
        // Gestionnaire pour le bouton "Se déconnecter"
        ui.addDisconnectButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_BUTTON_CLICKED, AdminClientConfig.BUTTON_DISCONNECT_TEXT));
                controller.disconnectFromServer();
            }
        });
        
        // Gestionnaire pour le bouton "Actualiser"
        ui.addRefreshButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_BUTTON_CLICKED, AdminClientConfig.BUTTON_REFRESH_TEXT));
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
        // Raccourci Ctrl+C pour se connecter
        ui.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(AdminClientConfig.SHORTCUT_CONNECT), "connect");
        ui.getRootPane().getActionMap().put("connect", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_SHORTCUT_USED, AdminClientConfig.SHORTCUT_CONNECT, "Connexion"));
                controller.connectToServer();
            }
        });
        
        // Raccourci Ctrl+D pour se déconnecter
        ui.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(AdminClientConfig.SHORTCUT_DISCONNECT), "disconnect");
        ui.getRootPane().getActionMap().put("disconnect", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_SHORTCUT_USED, AdminClientConfig.SHORTCUT_DISCONNECT, "Déconnexion"));
                controller.disconnectFromServer();
            }
        });
        
        // Raccourci F5 pour actualiser
        ui.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(AdminClientConfig.SHORTCUT_REFRESH), "refresh");
        ui.getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_SHORTCUT_USED, AdminClientConfig.SHORTCUT_REFRESH, "Actualisation"));
                controller.refreshClientList();
            }
        });
        
        // Raccourci Ctrl+Q pour quitter
        ui.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(AdminClientConfig.SHORTCUT_QUIT), "quit");
        ui.getRootPane().getActionMap().put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(String.format(AdminClientConfig.LOG_SHORTCUT_USED, AdminClientConfig.SHORTCUT_QUIT, "Fermeture"));
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
        final String serverHost = (args.length >= 1) ? args[0] : AdminClientConfig.DEFAULT_SERVER_HOST;
        
        final int serverPort;
        if (args.length >= 2) {
            int parsedPort;
            try {
                parsedPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide : " + args[1] + ". Utilisation du port par défaut : " + AdminClientConfig.DEFAULT_SERVER_PORT);
                parsedPort = AdminClientConfig.DEFAULT_SERVER_PORT;
            }
            serverPort = parsedPort;
        } else {
            serverPort = AdminClientConfig.DEFAULT_SERVER_PORT;
        }
        
        // Afficher les informations de démarrage
        System.out.println(AdminClientConfig.STARTUP_BANNER);
        System.out.println(AdminClientConfig.STARTUP_TITLE);
        System.out.println(AdminClientConfig.STARTUP_BANNER);
        System.out.println(String.format(AdminClientConfig.STARTUP_SERVER_INFO, serverHost, serverPort));
        System.out.println(AdminClientConfig.STARTUP_PROTOCOL);
        System.out.println(AdminClientConfig.STARTUP_COMMAND);
        System.out.println(AdminClientConfig.STARTUP_BANNER);
        
        // Lancer l'application dans l'Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    AdminClient app = new AdminClient();
                    // Mettre à jour la configuration du serveur si spécifiée
                    if (!serverHost.equals(AdminClientConfig.DEFAULT_SERVER_HOST) || serverPort != AdminClientConfig.DEFAULT_SERVER_PORT) {
                        app.controller.updateServerConfig(serverHost, serverPort);
                        System.out.println(String.format(AdminClientConfig.STARTUP_CONFIG_UPDATE, serverHost, serverPort));
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
