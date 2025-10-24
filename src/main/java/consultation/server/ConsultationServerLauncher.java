package consultation.server;

public class ConsultationServerLauncher {
    public static void main(String[] args) {
        String configFile = "./src/main/resources/server.properties";
        if (args.length > 0) {
            configFile = args[0];
        }
        try {
            ConsultationServerConfig config = new ConsultationServerConfig(configFile);
            ConsultationServer server = new ConsultationServer(config);
            server.run();
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
        }
    }
}
