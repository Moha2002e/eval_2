package consultation.client;

import javax.swing.SwingUtilities;

public class ConsultationClient {
    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 9090;
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(host, port);
            frame.setVisible(true);
        });
    }
}
