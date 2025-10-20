package hepl.fead.adminclient.view;

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
        setTitle(AdminClientConfig.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(AdminClientConfig.WINDOW_WIDTH, AdminClientConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null); // Centre la fenêtre
        
        // Configuration du layout
        setLayout(new BorderLayout());
        
        // Création des composants
        createTable();
        createButtons();
        createStatusLabel();
        
        // Ajout des composants à la fenêtre
        add(scrollPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
        
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
        
        // Configuration des largeurs de colonnes
        clientTable.getColumnModel().getColumn(0).setPreferredWidth(120); // IP
        clientTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Nom
        clientTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Prénom
        clientTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // ID
        
        // Création du scroll pane
        scrollPane = new JScrollPane(clientTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
    /**
     * Crée tous les boutons
     */
    private void createButtons() {
        // Bouton de connexion
        connectButton = new JButton(AdminClientConfig.BUTTON_CONNECT_TEXT);
        connectButton.setFont(new Font(AdminClientConfig.FONT_NAME, Font.PLAIN, AdminClientConfig.BUTTON_FONT_SIZE));
        connectButton.setPreferredSize(new Dimension(AdminClientConfig.BUTTON_WIDTH, AdminClientConfig.BUTTON_HEIGHT));
        
        // Bouton de déconnexion
        disconnectButton = new JButton(AdminClientConfig.BUTTON_DISCONNECT_TEXT);
        disconnectButton.setFont(new Font(AdminClientConfig.FONT_NAME, Font.PLAIN, AdminClientConfig.BUTTON_FONT_SIZE));
        disconnectButton.setPreferredSize(new Dimension(AdminClientConfig.BUTTON_WIDTH, AdminClientConfig.BUTTON_HEIGHT));
        disconnectButton.setEnabled(false); // Désactivé par défaut
        
        // Bouton d'actualisation
        refreshButton = new JButton(AdminClientConfig.BUTTON_REFRESH_TEXT);
        refreshButton.setFont(new Font(AdminClientConfig.FONT_NAME, Font.PLAIN, AdminClientConfig.BUTTON_FONT_SIZE));
        refreshButton.setPreferredSize(new Dimension(AdminClientConfig.REFRESH_BUTTON_WIDTH, AdminClientConfig.BUTTON_HEIGHT));
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
        statusLabel.setFont(new Font(AdminClientConfig.FONT_NAME, Font.PLAIN, AdminClientConfig.STATUS_FONT_SIZE));
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
