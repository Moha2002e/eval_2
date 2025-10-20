package hepl.fead.model.bd;

import hepl.fead.model.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

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
            LOGGER.info("✅ Base de données initialisée avec succès");

        } catch (Exception e) {
            LOGGER.severe("❌ Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            throw new RuntimeException("Impossible d'initialiser la base de données", e);
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
        String urlWithoutDb = "jdbc:mysql://" + host + "/?createDatabaseIfNotExist=true";
        
        Class.forName(driver);
        
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, user, password);
             Statement stmt = conn.createStatement()) {

            // Créer la base de données si elle n'existe pas
            String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + dbName + 
                                  " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(createDbQuery);
            
            LOGGER.info("✅ Base de données '" + dbName + "' vérifiée/créée");

        } catch (SQLException e) {
            LOGGER.warning("⚠️  Tentative de création de la base de données: " + e.getMessage());
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
            String createSpecialtiesTable = """
                CREATE TABLE IF NOT EXISTS specialties (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
            stmt.executeUpdate(createSpecialtiesTable);
            LOGGER.info("✅ Table 'specialties' vérifiée/créée");

            // Table patient (singulier pour correspondre aux DAOs)
            String createPatientsTable = """
                CREATE TABLE IF NOT EXISTS patient (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    birth_date DATE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_last_name (last_name),
                    INDEX idx_birth_date (birth_date)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
            stmt.executeUpdate(createPatientsTable);
            LOGGER.info("✅ Table 'patient' vérifiée/créée");

            // Table doctor (singulier pour correspondre aux DAOs)
            String createDoctorsTable = """
                CREATE TABLE IF NOT EXISTS doctor (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    specialite_id INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (specialite_id) REFERENCES specialties(id) ON DELETE SET NULL,
                    INDEX idx_last_name (last_name),
                    INDEX idx_specialite (specialite_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
            stmt.executeUpdate(createDoctorsTable);
            LOGGER.info("✅ Table 'doctor' vérifiée/créée");

            // Table consultations
            String createConsultationsTable = """
                CREATE TABLE IF NOT EXISTS consultations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    patient_id INT NOT NULL,
                    doctor_id INT NOT NULL,
                    date DATE NOT NULL,
                    hour TIME,
                    reason TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
                    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
                    INDEX idx_date (date),
                    INDEX idx_patient (patient_id),
                    INDEX idx_doctor (doctor_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
            stmt.executeUpdate(createConsultationsTable);
            LOGGER.info("✅ Table 'consultations' vérifiée/créée");

            // Insérer des données initiales si les tables sont vides
            insertInitialDataIfNeeded(stmt);

        }
    }

    /**
     * Insère des données initiales si les tables sont vides
     */
    private static void insertInitialDataIfNeeded(Statement stmt) throws SQLException {
        // Vérifier si des spécialités existent
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM specialties");
        rs.next();
        int specialtyCount = rs.getInt(1);
        rs.close();

        if (specialtyCount == 0) {
            LOGGER.info("📝 Insertion des spécialités initiales...");
            
            String[] specialties = {
                "Cardiologie",
                "Pédiatrie",
                "Dermatologie",
                "Neurologie",
                "Psychiatrie",
                "Médecine Générale",
                "Chirurgie",
                "Ophtalmologie"
            };

            for (String specialty : specialties) {
                String insertQuery = "INSERT INTO specialties (name) VALUES ('" + specialty + "')";
                stmt.executeUpdate(insertQuery);
            }
            
            LOGGER.info("✅ " + specialties.length + " spécialités insérées");
        }

        // Vérifier si des patients existent
        rs = stmt.executeQuery("SELECT COUNT(*) FROM patient");
        rs.next();
        int patientCount = rs.getInt(1);
        rs.close();

        if (patientCount == 0) {
            LOGGER.info("📝 Insertion de quelques patients de test...");
            
            String[] patients = {
                "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Marie', 'Dupont', '1985-03-15')",
                "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Jean', 'Martin', '1978-07-22')",
                "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Sophie', 'Bernard', '1992-11-08')",
                "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Pierre', 'Dubois', '1965-05-30')",
                "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Julie', 'Lefebvre', '1988-09-17')"
            };

            for (String insertQuery : patients) {
                stmt.executeUpdate(insertQuery);
            }
            
            LOGGER.info("✅ " + patients.length + " patients de test insérés");
        }

        // Vérifier si des docteurs existent
        rs = stmt.executeQuery("SELECT COUNT(*) FROM doctor");
        rs.next();
        int doctorCount = rs.getInt(1);
        rs.close();

        if (doctorCount == 0) {
            LOGGER.info("📝 Insertion de quelques docteurs de test...");
            
            String[] doctors = {
                "INSERT INTO doctor (first_name, last_name, specialite_id , password) VALUES ('Dr. Antoine', 'Rousseau', 1,'test')",
                "INSERT INTO doctor (first_name, last_name, specialite_id,password) VALUES ('Dr. Claire', 'Moreau', 2,'test')",
                "INSERT INTO doctor (first_name, last_name, specialite_id,password) VALUES ('Dr. Thomas', 'Lambert', 6,'test')",
                "INSERT INTO doctor (first_name, last_name, specialite_id,password) VALUES ('Dr. Isabelle', 'Petit', 3,'test')"
            };

            for (String insertQuery : doctors) {
                stmt.executeUpdate(insertQuery);
            }
            
            LOGGER.info("✅ " + doctors.length + " docteurs de test insérés");
        }
    }

    /**
     * Réinitialise le flag d'initialisation (utile pour les tests)
     */
    public static void reset() {
        initialized = false;
    }
}

