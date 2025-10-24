package consultation.client;

import consultation.server.protocol.ReponseTraitee;
import consultation.server.protocol.RequeteLogin;
import hepl.fead.model.entity.Doctor;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private final NetworkManager networkManager;
    private final JTextField loginField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    public LoginFrame(String host, int port) {
        this.networkManager = new NetworkManager(host, port);
        setTitle("Connexion médecin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 180);
        setLocationRelativeTo(null);
        initComponents();
    }
    private void initComponents() {
        JLabel loginLabel = new JLabel("Login:");
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JButton loginButton = new JButton("Connexion");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(loginLabel, gbc);
        gbc.gridx = 1;
        panel.add(loginField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        getContentPane().add(panel);
    }
    private void performLogin() {
        final String login = loginField.getText().trim();
        final String password = new String(passwordField.getPassword());
        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer vos identifiants", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(() -> {
            try {
                networkManager.connect();
                ReponseTraitee resp = networkManager.sendRequest(new RequeteLogin(login, password));
                if (resp.isSuccess()) {
                    Doctor doctor = (Doctor) resp.getData();
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        MainFrame mainFrame = new MainFrame(networkManager, doctor);
                        mainFrame.setVisible(true);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, resp.getMessage(), "Connexion échouée", JOptionPane.ERROR_MESSAGE);
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this, "Erreur lors de la connexion au serveur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}
