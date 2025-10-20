package hepl.fead.model.bd;

import hepl.fead.model.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import static hepl.fead.model.bd.Const_BD.*;

/**
 * Classe pour initialiser la base de données et créer les tables si elles n'existent pas
 */
public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private static boolean initialized = false;

    /**
     * Initialise la base de données : crée la BD et les tables si nécessaire
     */
    public static void initialize() {
        if (initialized) {
            return; // Déjà initialisé
        }

        try {
            // Étape 1 : Créer la base de données si elle n'existe pas
            createDatabaseIfNotExists();

            // Étape 2 : Créer les tables si elles n'existent pas
            createTablesIfNotExist();

            initialized = true;
            LOGGER.info(BD_INITIALISER_OK);

        } catch (Exception e) {
            LOGGER.severe(BD_INITIALISER_KO + ": " + e.getMessage());
            throw new RuntimeException(BD_INIT_IMPOSSIBLE, e);
        }
    }

    /**
     * Crée la base de données si elle n'existe pas
     */
    private static void createDatabaseIfNotExists() throws SQLException, ClassNotFoundException {
        String host = DatabaseConfig.getHost();
        String dbName = DatabaseConfig.getName();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPassword();
        String driver = DatabaseConfig.getDriver();

        // Connexion sans spécifier la base de données
        String urlWithoutDb = DatabaseConfig.getUrl();
        
        Class.forName(driver);
        
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, user, password);
             Statement stmt = conn.createStatement()) {

            // Créer la base de données si elle n'existe pas
            String createDbQuery = String.format(CREATE_DATABASE, dbName);
            stmt.executeUpdate(createDbQuery);
            
            LOGGER.info(String.format(BD_VERIFIER_CREER, dbName));

        } catch (SQLException e) {
            LOGGER.warning(String.format(BD_TENTATIVE_CREATION, e.getMessage()));
            // On continue même si la BD existe déjà
        }
    }

    /**
     * Crée toutes les tables nécessaires si elles n'existent pas
     */
    private static void createTablesIfNotExist() throws SQLException, ClassNotFoundException {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPassword();
        String driver = DatabaseConfig.getDriver();

        Class.forName(driver);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // Table specialties (spécialités médicales)
            stmt.executeUpdate(CREATE_TABLE_SPECIALTIES);
            LOGGER.info(String.format(TABLE_VERIFIER_CREER, TABLE_SPECIALTIES));

            // Table patient (singulier pour correspondre aux DAOs)
            stmt.executeUpdate(CREATE_TABLE_PATIENT);
            LOGGER.info(String.format(TABLE_VERIFIER_CREER, TABLE_PATIENT));

            // Table doctor (singulier pour correspondre aux DAOs)
            stmt.executeUpdate(CREATE_TABLE_DOCTOR);
            LOGGER.info(String.format(TABLE_VERIFIER_CREER, TABLE_DOCTOR));

            // Table consultations
            stmt.executeUpdate(CREATE_TABLE_CONSULTATIONS);
            LOGGER.info(String.format(TABLE_VERIFIER_CREER, TABLE_CONSULTATIONS));

            // Insérer des données initiales si les tables sont vides
            insertInitialDataIfNeeded(stmt);

        }
    }

    /**
     * Insère des données initiales si les tables sont vides
     */
    private static void insertInitialDataIfNeeded(Statement stmt) throws SQLException {
        // Vérifier si des spécialités existent
        var rs = stmt.executeQuery(COUNT_SPECIALTIES);
        rs.next();
        int specialtyCount = rs.getInt(1);
        rs.close();

        if (specialtyCount == 0) {
            LOGGER.info(INSERT_SPECIALITES_INITIALES);

            for (String specialty : SPECIALITES_INITIALES) {
                String insertQuery = String.format(INSERT_SPECIALTY, specialty);
                stmt.executeUpdate(insertQuery);
            }
            
            LOGGER.info(String.format(INSERT_OK, SPECIALITES_INITIALES.length, SPECIALITES_INSEREES));
        }

        // Vérifier si des patients existent
        rs = stmt.executeQuery(COUNT_PATIENT);
        rs.next();
        int patientCount = rs.getInt(1);
        rs.close();

        if (patientCount == 0) {
            LOGGER.info(INSERT_PATIENTS_TEST);

            for (String insertQuery : PATIENTS_INITIAUX) {
                stmt.executeUpdate(insertQuery);
            }
            
            LOGGER.info(String.format(INSERT_OK, PATIENTS_INITIAUX.length, PATIENTS_INSEREES));
        }

        // Vérifier si des docteurs existent
        rs = stmt.executeQuery(COUNT_DOCTOR);
        rs.next();
        int doctorCount = rs.getInt(1);
        rs.close();

        if (doctorCount == 0) {
            LOGGER.info(INSERT_DOCTORS_TEST);

            for (String insertQuery : DOCTORS_INITIAUX) {
                stmt.executeUpdate(insertQuery);
            }
            
            LOGGER.info(String.format(INSERT_OK, DOCTORS_INITIAUX.length, DOCTORS_INSEREES));
        }
    }

    /**
     * Réinitialise le flag d'initialisation (utile pour les tests)
     */
    public static void reset() {
        initialized = false;
    }
}

