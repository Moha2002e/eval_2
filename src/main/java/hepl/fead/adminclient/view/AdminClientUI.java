package hepl.fead.adminclient.view;

import com.formdev.flatlaf.FlatClientProperties;
import hepl.fead.adminclient.model.ClientInfo;
import hepl.fead.adminclient.config.AdminClientConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interface graphique Swing pour le Client Admin
 * Affiche la liste des clients connectés dans une JTable
 */
public class AdminClientUI extends JFrame {
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel statusLabel;
    private JScrollPane scrollPane;
    
    /**
     * Constructeur de l'interface
     */
    public AdminClientUI() {
        initializeUI();
    }
    
    /**
     * Initialise tous les composants de l'interface
     */
    private void initializeUI() {
        // Style moderne
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.focusWidth", 0);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", true);

        setTitle(AdminClientConfig.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(AdminClientConfig.WINDOW_WIDTH, AdminClientConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null); // Centre la fenêtre
        
        // Configuration du layout
        setLayout(new BorderLayout(10, 10));

        // Création des composants
        createTable();
        createButtons();
        createStatusLabel();
        
        // Panel principal avec marges
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        // Configuration initiale
        updateConnectionState(false); // État déconnecté par défaut
    }
    
    /**
     * Crée et configure la JTable
     */
    private void createTable() {
        // Création du modèle de table
        tableModel = new DefaultTableModel(AdminClientConfig.COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table en lecture seule
            }
        };
        
        // Création de la table
        clientTable = new JTable(tableModel);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setRowHeight(AdminClientConfig.TABLE_ROW_HEIGHT);
        
        // Style moderne pour la table
        clientTable.putClientProperty(FlatClientProperties.STYLE,
            "rowHeight:32; " +
            "selectionBackground:@accentColor; " +
            "selectionForeground:@foreground;");

        // En-têtes de table plus élégants
        clientTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
            "height:36; " +
            "font:bold;");

        // Configuration des largeurs de colonnes
        clientTable.getColumnModel().getColumn(0).setPreferredWidth(120); // IP
        clientTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Nom
        clientTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Prénom
        clientTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // ID
        
        // Création du scroll pane avec style moderne
        scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
    /**
     * Crée tous les boutons
     */
    private void createButtons() {
        // Bouton de connexion
        connectButton = new JButton(AdminClientConfig.BUTTON_CONNECT_TEXT);
        connectButton.putClientProperty(FlatClientProperties.STYLE,
            "font:+1; borderWidth:1; arc:999;");
        connectButton.setPreferredSize(new Dimension(AdminClientConfig.BUTTON_WIDTH, 36));

        // Bouton de déconnexion
        disconnectButton = new JButton(AdminClientConfig.BUTTON_DISCONNECT_TEXT);
        disconnectButton.putClientProperty(FlatClientProperties.STYLE,
            "font:+1; borderWidth:1; arc:999;");
        disconnectButton.setPreferredSize(new Dimension(AdminClientConfig.BUTTON_WIDTH, 36));
        disconnectButton.setEnabled(false); // Désactivé par défaut
        
        // Bouton d'actualisation
        refreshButton = new JButton(AdminClientConfig.BUTTON_REFRESH_TEXT);
        refreshButton.putClientProperty(FlatClientProperties.STYLE,
            "font:+1; borderWidth:1; arc:999;");
        refreshButton.setPreferredSize(new Dimension(AdminClientConfig.REFRESH_BUTTON_WIDTH, 36));
        refreshButton.setEnabled(false); // Désactivé par défaut
    }
    
    /**
     * Crée le panneau contenant tous les boutons
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }
    
    /**
     * Crée le label de statut
     */
    private void createStatusLabel() {
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.putClientProperty(FlatClientProperties.STYLE, "font:+1;");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Met à jour la liste des clients dans la table
     * @param clients Liste des clients connectés
     */
    public void updateClientList(List<ClientInfo> clients) {
        // Vider la table actuelle
        tableModel.setRowCount(0);
        
        // Ajouter chaque client à la table
        for (ClientInfo client : clients) {
            Object[] row = {
                client.getIpAddress(),
                client.getLastName(),
                client.getFirstName(),
                client.getPatientId()
            };
            tableModel.addRow(row);
        }
        
        // Mettre à jour le statut
        String status = clients.isEmpty() ? 
            AdminClientConfig.STATUS_NO_CLIENTS : 
            String.format(AdminClientConfig.STATUS_CLIENTS_FORMAT, clients.size());
        setConnectionStatus(status);
    }
    
    /**
     * Met à jour le message de statut
     * @param status Message de statut à afficher
     */
    public void setConnectionStatus(String status) {
        statusLabel.setText("État : " + status);
    }
    
    /**
     * Ajoute un listener au bouton d'actualisation
     * @param listener ActionListener à ajouter
     */
    public void addRefreshButtonListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }
    
    /**
     * Ajoute un listener au bouton de connexion
     * @param listener ActionListener à ajouter
     */
    public void addConnectButtonListener(ActionListener listener) {
        connectButton.addActionListener(listener);
    }
    
    /**
     * Ajoute un listener au bouton de déconnexion
     * @param listener ActionListener à ajouter
     */
    public void addDisconnectButtonListener(ActionListener listener) {
        disconnectButton.addActionListener(listener);
    }
    
    /**
     * Active ou désactive le bouton d'actualisation
     * @param enabled true pour activer, false pour désactiver
     */
    public void setRefreshButtonEnabled(boolean enabled) {
        refreshButton.setEnabled(enabled);
    }
    
    /**
     * Active ou désactive le bouton de connexion
     * @param enabled true pour activer, false pour désactiver
     */
    public void setConnectButtonEnabled(boolean enabled) {
        connectButton.setEnabled(enabled);
    }
    
    /**
     * Active ou désactive le bouton de déconnexion
     * @param enabled true pour activer, false pour désactiver
     */
    public void setDisconnectButtonEnabled(boolean enabled) {
        disconnectButton.setEnabled(enabled);
    }
    
    /**
     * Met à jour l'état des boutons selon le statut de connexion
     * @param connected true si connecté, false si déconnecté
     */
    public void updateConnectionState(boolean connected) {
        if (connected) {
            // Connecté : désactiver connexion, activer déconnexion et actualisation
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            refreshButton.setEnabled(true);
            setConnectionStatus(AdminClientConfig.STATUS_CONNECTED);
        } else {
            // Déconnecté : activer connexion, désactiver déconnexion et actualisation
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            refreshButton.setEnabled(false);
            setConnectionStatus(AdminClientConfig.STATUS_DISCONNECTED);
        }
    }
    
    /**
     * Affiche un message d'erreur dans une boîte de dialogue
     * @param title Titre de la boîte de dialogue
     * @param message Message d'erreur à afficher
     */
    public void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Affiche un message d'information dans une boîte de dialogue
     * @param title Titre de la boîte de dialogue
     * @param message Message d'information à afficher
     */
    public void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
