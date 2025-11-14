package consultation.client;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.SwingUtilities;

public class ConsultationClient {
    public static void main(String[] args) {
        // Configuration style macOS
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Système Médical");

        // Initialiser FlatLaf en mode SOMBRE AVANT de créer les fenêtres
        FlatMacDarkLaf.setup();

        final String host = "localhost";
        final int port = 9090;

        SwingUtilities.invokeLater(() -> { // Créer et afficher l'interface graphique dans le thread EDT
            LoginFrame frame = new LoginFrame(host, port);
            frame.setVisible(true);
        });
    }
}
