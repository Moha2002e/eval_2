package hepl.fead.model.adminclient;

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
    private JLabel statusLabel;
    private JScrollPane scrollPane;

    // Noms des colonnes de la table
    private static final String[] COLUMN_NAMES = {"Adresse IP", "Nom", "Prénom", "ID Patient"};

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
        setTitle("Client Admin - Liste des patients connectés");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Centre la fenêtre

        // Configuration du layout
        setLayout(new BorderLayout());

        // Création des composants
        createTable();
        createButton();
        createStatusLabel();

        // Ajout des composants à la fenêtre
        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        // Configuration initiale
        setConnectionStatus("Prêt - Cliquez sur 'Actualiser' pour charger la liste");
    }

    /**
     * Crée et configure la JTable
     */
    private void createTable() {
        // Création du modèle de table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table en lecture seule
            }
        };

        // Création de la table
        clientTable = new JTable(tableModel);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setRowHeight(25);

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
     * Crée le bouton d'actualisation
     */
    private void createButton() {
        refreshButton = new JButton("Actualiser la liste");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(200, 40));
    }

    /**
     * Crée le label de statut
     */
    private void createStatusLabel() {
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
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
                "Aucun patient connecté" :
                String.format("%d patient(s) connecté(s)", clients.size());
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
     * Active ou désactive le bouton d'actualisation
     * @param enabled true pour activer, false pour désactiver
     */
    public void setRefreshButtonEnabled(boolean enabled) {
        refreshButton.setEnabled(enabled);
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
