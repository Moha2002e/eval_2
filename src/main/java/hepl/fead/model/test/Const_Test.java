package hepl.fead.model.test;

public class Const_Test {
    
    // ========================================================================
    // MESSAGES DE TEST
    // ========================================================================
    
    public static final String TEST_CONNEXION = "TEST DE CONNEXION";
    public static final String TEST_INIT_BD = "TEST INITIALISATION BD";
    public static final String TEST_ENTITY_INTERFACE = "TEST INTERFACE ENTITY";
    public static final String TEST_DAO_PATIENT = "TEST DAO PATIENT";
    public static final String TEST_DAO_DOCTOR = "TEST DAO DOCTOR";
    public static final String TEST_DAO_SPECIALTY = "TEST DAO SPECIALTY";
    public static final String TEST_DAO_CONSULTATION = "TEST DAO CONSULTATION";
    public static final String FIN_TESTS = "FIN DES TESTS";
    
    // ========================================================================
    // MESSAGES DE RÉSULTAT
    // ========================================================================
    
    public static final String SUCCES = "SUCCES";
    public static final String ECHEC = "ECHEC";
    public static final String ERREUR = "ERREUR";
    public static final String ERREUR_SQL = "ERREUR SQL";
    public static final String ERREUR_GENERALE = "ERREUR GENERALE";
    
    // ========================================================================
    // ACTIONS
    // ========================================================================
    
    public static final String CONNEXION_BD = "Connexion a la base de donnees";
    public static final String LIRE_TOUS = "Lire tous les enregistrements";
    public static final String AJOUTER = "Ajouter un nouvel enregistrement";
    public static final String MODIFIER = "Modifier un enregistrement";
    public static final String SUPPRIMER = "Supprimer un enregistrement";
    public static final String RECHERCHER = "Rechercher avec criteres";
    public static final String LIRE_PAR_ID = "Lire par ID";
    public static final String VERIFICATION = "Verification";
    public static final String STATISTIQUES_BD = "Statistiques de la base";
    public static final String INFO_CONNEXION = "Informations de connexion";
    
    // ========================================================================
    // NOMS DE TABLES
    // ========================================================================
    
    public static final String TABLE_PATIENT = "patient";
    public static final String TABLE_DOCTOR = "doctor";
    public static final String TABLE_SPECIALTIES = "specialties";
    public static final String TABLE_CONSULTATIONS = "consultations";
    
    // ========================================================================
    // REQUÊTES SQL
    // ========================================================================
    
    public static final String SELECT_ALL_PATIENT = "SELECT * FROM patient";
    public static final String SELECT_COUNT = "SELECT COUNT(*) FROM %s";
    
    // ========================================================================
    // MESSAGES D'INFORMATION
    // ========================================================================
    
    public static final String CONNEXION_ETABLIE = "Connexion etablie";
    public static final String TABLE_ACCESSIBLE = "Table accessible";
    public static final String ENREGISTREMENT_AJOUTE = "Enregistrement ajoute avec ID: %d";
    public static final String ENREGISTREMENT_MODIFIE = "Enregistrement modifie";
    public static final String ENREGISTREMENT_SUPPRIME = "Enregistrement supprime";
    public static final String VERIFICATION_OK = "Verification OK";
    public static final String VERIFICATION_ECHEC = "Verification echouee";
    public static final String AUCUNE_DONNEE = "Aucune donnee trouvee";
    public static final String LIGNES_AFFICHEES = "%d ligne(s) affichee(s)";
    public static final String NB_ENREGISTREMENTS = "Nombre d'enregistrements: %d";
    public static final String RESULTATS_RECHERCHE = "Resultats de recherche: %d";
    
    // ========================================================================
    // DONNÉES DE TEST
    // ========================================================================
    
    // Patients
    public static final String PATIENT_PRENOM = "Jean";
    public static final String PATIENT_NOM = "Dupont";
    public static final String PATIENT_DATE_NAISSANCE = "1985-05-15";
    public static final String PATIENT_PRENOM_2 = "Marie";
    public static final String PATIENT_NOM_2 = "Martin";
    public static final String PATIENT_DATE_NAISSANCE_2 = "1990-12-25";
    public static final String PATIENT_TEMP_PRENOM = "Temporaire";
    public static final String PATIENT_TEMP_NOM = "ASupprimer";
    public static final String PATIENT_TEMP_DATE = "2000-01-01";
    
    // Doctors
    public static final String DOCTOR_PRENOM = "Dr. Sophie";
    public static final String DOCTOR_NOM = "Bernard";
    public static final String DOCTOR_PASSWORD = "password123";
    public static final String DOCTOR_PRENOM_2 = "Dr. Pierre";
    public static final String DOCTOR_NOM_2 = "Dubois";
    public static final String DOCTOR_TEMP_PRENOM = "Dr. Temporaire";
    public static final String DOCTOR_TEMP_NOM = "ASupprimer";
    
    // Specialties
    public static final String SPECIALTY_NOM = "Gastro-enterologie";
    public static final String SPECIALTY_NOM_MODIF = "Cardiologie-TEST";
    public static final String SPECIALTY_TEMP = "Specialite-Temporaire-A-Supprimer";
    
    // Recherche
    public static final String RECHERCHE_NOM_MARTIN = "Martin";
    public static final String RECHERCHE_PRENOM_MARIE = "Marie";
    public static final String RECHERCHE_NOM_DUBOIS = "Dubois";
    public static final String RECHERCHE_PRENOM_PIERRE = "Pierre";
    public static final String RECHERCHE_CARDIO = "Cardio";
    public static final String RECHERCHE_LOGIE = "logie";
    
    // Suffixes de test
    public static final String SUFFIX_TEST = "-TEST";
    
    // ========================================================================
    // FORMATS D'AFFICHAGE
    // ========================================================================
    
    public static final String SEPARATEUR = "========================================";
    public static final String SEPARATEUR_COURT = "------------------------------------------";
    public static final String FORMAT_TITRE = "  %s";
    public static final String FORMAT_TEST = "TEST %d: %s";
    public static final String FORMAT_DETAIL = "  %s: %s";
    public static final String FORMAT_ID = "ID: %d";
    public static final String FORMAT_NOM_PRENOM = "%s %s";
    public static final String FORMAT_TABLE_COUNT = "  %-20s : %d enregistrement(s)";
    public static final String FORMAT_TABLE_ERREUR = "  %-20s : Table non trouvee ou erreur";
    
    // ========================================================================
    // LABELS
    // ========================================================================
    
    public static final String LABEL_ID = "ID";
    public static final String LABEL_NOM = "Nom";
    public static final String LABEL_PRENOM = "Prenom";
    public static final String LABEL_DATE_NAISSANCE = "Date de naissance";
    public static final String LABEL_SPECIALITE_ID = "Specialite ID";
    public static final String LABEL_URL = "URL";
    public static final String LABEL_UTILISATEUR = "Utilisateur";
    public static final String LABEL_BD = "Base de donnees";
    public static final String LABEL_PRODUIT = "Produit";
    public static final String LABEL_VERSION = "Version";
    public static final String LABEL_DRIVER = "Driver";
    public static final String LABEL_VERSION_DRIVER = "Version du driver";
    public static final String LABEL_NB_COLONNES = "Colonnes (%d):";
    
    // ========================================================================
    // MESSAGES D'ENTITÉ
    // ========================================================================
    
    public static final String ENTITY_NOUVEAU = "Nouveau";
    public static final String ENTITY_PERSISTE = "Persiste";
    public static final String ENTITY_EST_NOUVEAU = "Est nouveau ?";
    public static final String ENTITY_EST_PERSISTE = "Est persiste ?";
}

