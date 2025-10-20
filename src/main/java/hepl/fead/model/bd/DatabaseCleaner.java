package hepl.fead.model.bd;

import hepl.fead.model.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import static hepl.fead.model.bd.Const_BD.*;

/**
 * Classe utilitaire pour nettoyer la base de données en supprimant toutes les tables
 */
public class DatabaseCleaner {
    private static final Logger LOGGER = Logger.getLogger(DatabaseCleaner.class.getName());

    /**
     * Supprime toutes les tables de la base de données
     * ATTENTION : Cette opération est irréversible !
     */
    public static void dropAllTables() {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPassword();
        String driver = DatabaseConfig.getDriver();

        try {
            Class.forName(driver);

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement()) {

                // Désactiver temporairement les contraintes de clés étrangères
                stmt.executeUpdate(DISABLE_FOREIGN_KEY_CHECKS);

                // Supprimer toutes les tables (ordre n'a plus d'importance)
                stmt.executeUpdate(DROP_TABLE_CONSULTATIONS);
                LOGGER.info(String.format(TABLE_SUPPRIMEE, TABLE_CONSULTATIONS));

                stmt.executeUpdate(DROP_TABLE_DOCTOR);
                LOGGER.info(String.format(TABLE_SUPPRIMEE, TABLE_DOCTOR));

                // Supprimer aussi l'ancienne table doctors (au pluriel) si elle existe
                stmt.executeUpdate(DROP_TABLE_DOCTORS);
                LOGGER.info(String.format(TABLE_SUPPRIMEE, TABLE_DOCTORS));

                stmt.executeUpdate(DROP_TABLE_PATIENT);
                LOGGER.info(String.format(TABLE_SUPPRIMEE, TABLE_PATIENT));

                // Supprimer aussi l'ancienne table patients (au pluriel) si elle existe
                stmt.executeUpdate(DROP_TABLE_PATIENTS);
                LOGGER.info(String.format(TABLE_SUPPRIMEE, TABLE_PATIENTS));

                stmt.executeUpdate(DROP_TABLE_SPECIALTIES);
                LOGGER.info(String.format(TABLE_SUPPRIMEE, TABLE_SPECIALTIES));

                // Réactiver les contraintes de clés étrangères
                stmt.executeUpdate(ENABLE_FOREIGN_KEY_CHECKS);

                LOGGER.info(NETTOYAGE_BD_OK);

            } catch (SQLException e) {
                LOGGER.severe(NETTOYAGE_BD_KO + ": " + e.getMessage());
                throw new RuntimeException(NETTOYAGE_BD_KO, e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(ERREUR_CONNEXION, e);
        }
    }

    /**
     * Main pour exécuter le nettoyage de la base de données
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        LOGGER.warning(WARNING_SUPPRESSION_TABLES);
        LOGGER.warning(WARNING_IRREVERSIBLE);
        
        try {
            dropAllTables();
            
            // Réinitialiser le flag d'initialisation si on veut recréer les tables après
            DatabaseInitializer.reset();
            LOGGER.info(INFO_REINITIALISER);
            
        } catch (Exception e) {
            LOGGER.severe(String.format(ECHEC_NETTOYAGE, e.getMessage()));
            e.printStackTrace();
        }
    }
}

