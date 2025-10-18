package hepl.fead.model.bd;

import hepl.fead.model.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectBD {

    private static Connection conn;
    private static boolean databaseInitialized = false;

    public static Connection getConnection() {
        // Initialiser la base de données au premier appel
        if (!databaseInitialized) {
            DatabaseInitializer.initialize();
            databaseInitialized = true;
        }

        if (conn == null) {
            try {
                String sCon = DatabaseConfig.getUrl();
                String sUser = DatabaseConfig.getUser();
                String sPass = DatabaseConfig.getPassword();

                // Charger le driver
                Class.forName(DatabaseConfig.getDriver());
                conn = DriverManager.getConnection(sCon, sUser, sPass);

            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
            }
        }
        return conn; //  connexion réussis,  du return
    }

    public static void closeConnection() throws SQLException {
        if (conn != null&& !conn.isClosed()) {
            try {
                conn.close();
            } catch (SQLException e) {}
        }
    }
    private static boolean isConnected() {
        if (conn == null) {
            return false;
        }
        return true;
    }
}
