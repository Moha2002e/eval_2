package hepl.fead.model.bd;

public class Const_BD {
    
    // ========================================================================
    // MESSAGES D'ERREUR
    // ========================================================================
    
    public static final String ERREUR_CONNEXION = "Erreur lors de la connexion à la base de données";
    public static final String BD_INIT_IMPOSSIBLE = "Impossible d'initialiser la base de données";
    
    // ========================================================================
    // MESSAGES DE SUCCÈS - INITIALISATION
    // ========================================================================
    
    public static final String BD_INITIALISER_OK = "✅ Base de données initialisée avec succès";
    public static final String BD_VERIFIER_CREER = "✅ Base de données '%s' vérifiée/créée";
    public static final String TABLE_VERIFIER_CREER = "✅ Table '%s' vérifiée/créée";
    
    // ========================================================================
    // MESSAGES DE SUCCÈS - INSERTION
    // ========================================================================
    
    public static final String INSERT_OK = "✅ %d %s insérés";
    public static final String SPECIALITES_INSEREES = "spécialités";
    public static final String PATIENTS_INSEREES = "patients de test";
    public static final String DOCTORS_INSEREES = "docteurs de test";
    
    // ========================================================================
    // MESSAGES D'AVERTISSEMENT
    // ========================================================================
    
    public static final String BD_TENTATIVE_CREATION = "⚠️  Tentative de création de la base de données: %s";
    public static final String WARNING_SUPPRESSION_TABLES = "⚠️  ATTENTION : Vous allez supprimer TOUTES les tables de la base de données !";
    public static final String WARNING_IRREVERSIBLE = "⚠️  Cette opération est IRRÉVERSIBLE !";
    
    // ========================================================================
    // MESSAGES D'ÉCHEC
    // ========================================================================
    
    public static final String BD_INITIALISER_KO = "❌ Erreur lors de l'initialisation de la base de données";
    public static final String NETTOYAGE_BD_KO = "❌ Erreur lors de la suppression des tables";
    public static final String ECHEC_NETTOYAGE = "❌ Échec du nettoyage de la base de données : %s";
    
    // ========================================================================
    // MESSAGES INFORMATIFS
    // ========================================================================
    
    public static final String INSERT_SPECIALITES_INITIALES = "📝 Insertion des spécialités initiales...";
    public static final String INSERT_PATIENTS_TEST = "📝 Insertion de quelques patients de test...";
    public static final String INSERT_DOCTORS_TEST = "📝 Insertion de quelques docteurs de test...";
    public static final String INFO_REINITIALISER = "💡 Vous pouvez maintenant relancer DatabaseInitializer pour recréer les tables";
    
    // ========================================================================
    // MESSAGES SUPPRESSION
    // ========================================================================
    
    public static final String TABLE_SUPPRIMEE = "🗑️  Table '%s' supprimée";
    public static final String NETTOYAGE_BD_OK = "✅ Toutes les tables ont été supprimées avec succès";
    
    // ========================================================================
    // NOMS DES TABLES
    // ========================================================================
    
    public static final String TABLE_SPECIALTIES = "specialties";
    public static final String TABLE_PATIENT = "patient";
    public static final String TABLE_PATIENTS = "patients";
    public static final String TABLE_DOCTOR = "doctor";
    public static final String TABLE_DOCTORS = "doctors";
    public static final String TABLE_CONSULTATIONS = "consultations";
    
    // ========================================================================
    // REQUÊTES SQL - BASE DE DONNÉES
    // ========================================================================
    
    public static final String URL_WITHOUT_DB = "jdbc:mysql://%s/?createDatabaseIfNotExist=true";
    public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS %s CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
    
    // ========================================================================
    // REQUÊTES SQL - CRÉATION DE TABLES
    // ========================================================================
    
    public static final String CREATE_TABLE_SPECIALTIES = """
            CREATE TABLE IF NOT EXISTS specialties (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
    
    public static final String CREATE_TABLE_PATIENT = """
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
    
    public static final String CREATE_TABLE_DOCTOR = """
            CREATE TABLE IF NOT EXISTS doctor (
                id INT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                password VARCHAR(255) NOT NULL,
                specialite_id INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (specialite_id) REFERENCES specialties(id) ON DELETE SET NULL,
                INDEX idx_last_name (last_name),
                INDEX idx_specialite (specialite_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
    
    public static final String CREATE_TABLE_CONSULTATIONS = """
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
    
    // ========================================================================
    // REQUÊTES SQL - COMPTAGE
    // ========================================================================
    
    public static final String COUNT_SPECIALTIES = "SELECT COUNT(*) FROM specialties";
    public static final String COUNT_PATIENT = "SELECT COUNT(*) FROM patient";
    public static final String COUNT_DOCTOR = "SELECT COUNT(*) FROM doctor";
    
    // ========================================================================
    // REQUÊTES SQL - SUPPRESSION DE TABLES
    // ========================================================================
    
    public static final String DISABLE_FOREIGN_KEY_CHECKS = "SET FOREIGN_KEY_CHECKS = 0";
    public static final String ENABLE_FOREIGN_KEY_CHECKS = "SET FOREIGN_KEY_CHECKS = 1";
    public static final String DROP_TABLE_CONSULTATIONS = "DROP TABLE IF EXISTS consultations";
    public static final String DROP_TABLE_DOCTOR = "DROP TABLE IF EXISTS doctor";
    public static final String DROP_TABLE_DOCTORS = "DROP TABLE IF EXISTS doctors";
    public static final String DROP_TABLE_PATIENT = "DROP TABLE IF EXISTS patient";
    public static final String DROP_TABLE_PATIENTS = "DROP TABLE IF EXISTS patients";
    public static final String DROP_TABLE_SPECIALTIES = "DROP TABLE IF EXISTS specialties";
    
    // ========================================================================
    // REQUÊTES SQL - INSERTION
    // ========================================================================
    
    public static final String INSERT_SPECIALTY = "INSERT INTO specialties (name) VALUES ('%s')";
    
    // ========================================================================
    // DONNÉES INITIALES - SPÉCIALITÉS
    // ========================================================================
    
    public static final String[] SPECIALITES_INITIALES = {
        "Cardiologie",
        "Pédiatrie",
        "Dermatologie",
        "Neurologie",
        "Psychiatrie",
        "Médecine Générale",
        "Chirurgie",
        "Ophtalmologie"
    };
    
    // ========================================================================
    // DONNÉES INITIALES - PATIENTS
    // ========================================================================
    
    public static final String[] PATIENTS_INITIAUX = {
        "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Marie', 'Dupont', '1985-03-15')",
        "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Jean', 'Martin', '1978-07-22')",
        "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Sophie', 'Bernard', '1992-11-08')",
        "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Pierre', 'Dubois', '1965-05-30')",
        "INSERT INTO patient (first_name, last_name, birth_date) VALUES ('Julie', 'Lefebvre', '1988-09-17')"
    };
    
    // ========================================================================
    // DONNÉES INITIALES - DOCTEURS
    // ========================================================================
    
    public static final String[] DOCTORS_INITIAUX = {
        "INSERT INTO doctor (first_name, last_name, password, specialite_id) VALUES ('Dr. Antoine', 'Rousseau', 'password123', 1)",
        "INSERT INTO doctor (first_name, last_name, password, specialite_id) VALUES ('Dr. Claire', 'Moreau', 'password123', 2)",
        "INSERT INTO doctor (first_name, last_name, password, specialite_id) VALUES ('Dr. Thomas', 'Lambert', 'password123', 6)",
        "INSERT INTO doctor (first_name, last_name, password, specialite_id) VALUES ('Dr. Isabelle', 'Petit', 'password123', 3)"
    };
}
