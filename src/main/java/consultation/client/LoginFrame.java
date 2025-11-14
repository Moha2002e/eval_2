package consultation.client;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import consultation.server.protocol.ReponseTraitee;
import consultation.server.protocol.RequeteLogin;
import hepl.fead.model.entity.Doctor;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final NetworkManager networkManager;
    private final JTextField loginField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Connexion");
    private final JLabel errorLabel = new JLabel(" ");

    public LoginFrame(String host, int port) {
        super("Connexion");

        this.networkManager = new NetworkManager(host, port);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(360, 240));
        setLocationRelativeTo(null);

        initComponents();
        pack();
    }

    private void initComponents() {
        // Petit tuning "mac like"
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Component.focusWidth", 0);
        UIManager.put("ScrollBar.showButtons", false);

        // Placeholders
        loginField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Identifiant");
        loginField.putClientProperty(FlatClientProperties.STYLE, "showClearButton:true;");
        loginField.setPreferredSize(new Dimension(200, 36));

        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mot de passe");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true;");
        passwordField.setPreferredSize(new Dimension(200, 36));

        // Bouton un peu "mac"
        loginButton.putClientProperty(
                FlatClientProperties.STYLE,
                "font:+1; borderWidth:1; arc:999;"
        );
        loginButton.setPreferredSize(new Dimension(200, 36));

        // Layout compact
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.gridx = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        gbc.gridy = y++; panel.add(new JLabel("Espace Médecin", SwingConstants.CENTER), gbc);
        gbc.gridy = y++; panel.add(loginField, gbc);
        gbc.gridy = y++; panel.add(passwordField, gbc);
        gbc.gridy = y++; panel.add(loginButton, gbc);

        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#ff6b6b;");
        gbc.gridy = y++; panel.add(errorLabel, gbc);

        setContentPane(panel);

        loginButton.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void setBusy(boolean busy) {
        loginButton.setEnabled(!busy);
        loginField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        loginButton.setText(busy ? "Connexion..." : "Connexion");
    }

    private void showError(String msg) {
        errorLabel.setText(msg == null || msg.isBlank() ? " " : msg);
    }

    private void performLogin() {
        final String login = loginField.getText().trim();
        final String password = new String(passwordField.getPassword());

        showError(null);

        if (login.isEmpty() || password.isEmpty()) {
            showError("Veuillez entrer vos identifiants.");
            return;
        }

        setBusy(true);

        // SwingWorker pour ne pas bloquer l'EDT
        // il sert a faire du traitement en arriere plan
        new SwingWorker<ReponseTraitee, Void>() {
            private Exception error;

            @Override
            protected ReponseTraitee doInBackground() {
                try {
                    networkManager.connect();
                    return networkManager.sendRequest(new RequeteLogin(login, password));
                } catch (Exception ex) {
                    error = ex;
                    return null;
                }
            }
            // il sert a faire du traitement une fois le traitement en arriere plan terminer
            @Override
            protected void done() {
                setBusy(false);
                if (error != null) {
                    showError("Erreur de connexion au serveur : " + error.getMessage());
                    return;
                }
                try {
                    ReponseTraitee resp = get();
                    if (resp != null && resp.isSuccess()) {
                        Doctor doctor = (Doctor) resp.getData();
                        dispose();
                        MainFrame mainFrame = new MainFrame(networkManager, doctor);
                        mainFrame.setVisible(true);
                    } else {
                        showError(resp != null ? resp.getMessage() : "Réponse invalide du serveur.");
                    }
                } catch (Exception ex) {
                    showError("Erreur inattendue : " + ex.getMessage());
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        // Conseils "mac like"
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Système Médical");

        FlatMacDarkLaf.setup();
        // (Optionnel) si tu veux pousser un accent color :
        // FlatLaf.setGlobalExtraDefaults(Map.of("accentColor", "#007AFF"));

        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame("127.0.0.1", 5000);
            lf.setVisible(true);
        });
    }
}

