package hepl.fead.adminclient.config;

/**
 * Configuration centralisée pour le Client Admin
 * Contient toutes les constantes et paramètres de l'application
 */
public class AdminClientConfig {
    
    // ==========================================
    // CONFIGURATION SERVEUR
    // ==========================================
    
    /**
     * Adresse IP par défaut du serveur C
     */
    public static final String DEFAULT_SERVER_HOST = "192.168.0.16";
    
    /**
     * Port par défaut du serveur C
     */
    public static final int DEFAULT_SERVER_PORT = 8081;
    
    // ==========================================
    // TIMEOUTS ET CONFIGURATION RÉSEAU
    // ==========================================
    
    /**
     * Timeout de connexion en millisecondes (10 secondes)
     */
    public static final int CONNECTION_TIMEOUT = 5000;
    
    /**
     * Timeout de lecture en millisecondes (30 secondes)
     */
    public static final int READ_TIMEOUT = 3000;
    
    // ==========================================
    // COMMANDES PROTOCOLE
    // ==========================================
    
    /**
     * Commande pour lister les clients connectés
     */
    public static final String COMMAND_LIST_CLIENTS = "LIST_CLIENTS#";
    
    // ==========================================
    // INTERFACE UTILISATEUR
    // ==========================================
    
    /**
     * Titre de la fenêtre principale
     */
    public static final String WINDOW_TITLE = "Client Admin - Liste des patients connectés";
    
    /**
     * Largeur de la fenêtre par défaut
     */
    public static final int WINDOW_WIDTH = 600;
    
    /**
     * Hauteur de la fenêtre par défaut
     */
    public static final int WINDOW_HEIGHT = 600;
    
    /**
     * Largeur préférée des boutons
     */
    public static final int BUTTON_WIDTH = 150;
    
    /**
     * Hauteur préférée des boutons
     */
    public static final int BUTTON_HEIGHT = 30;
    
    /**
     * Largeur préférée du bouton d'actualisation
     */
    public static final int REFRESH_BUTTON_WIDTH = 200;
    
    /**
     * Nom de la police utilisée
     */
    public static final String FONT_NAME = "Arial";
    
    /**
     * Taille de la police des boutons
     */
    public static final int BUTTON_FONT_SIZE = 12;
    
    /**
     * Taille de la police du statut
     */
    public static final int STATUS_FONT_SIZE = 12;
    
    /**
     * Hauteur des lignes du tableau
     */
    public static final int TABLE_ROW_HEIGHT = 25;
    
    // ==========================================
    // NOMS DES COLONNES DU TABLEAU
    // ==========================================
    
    /**
     * Noms des colonnes du tableau des clients
     */
    public static final String[] COLUMN_NAMES = {"Adresse IP", "Nom", "Prénom", "ID Patient"};
    
    // ==========================================
    // MESSAGES D'ÉTAT
    // ==========================================
    
    /**
     * Messages d'état de connexion
     */
    public static final String STATUS_DISCONNECTED = "Déconnecté du serveur";
    public static final String STATUS_CONNECTED = "Connecté au serveur";
    public static final String STATUS_CONNECTING = "Tentative de connexion au serveur...";
    public static final String STATUS_REFRESHING = "Actualisation de la liste...";
    public static final String STATUS_NO_CLIENTS = "Aucun patient connecté";
    public static final String STATUS_CLIENTS_FORMAT = "%d patient(s) connecté(s)";
    
    // ==========================================
    // TEXTE DES BOUTONS
    // ==========================================
    
    /**
     * Texte des boutons
     */
    public static final String BUTTON_CONNECT_TEXT = "Se connecter";
    public static final String BUTTON_DISCONNECT_TEXT = "Se déconnecter";
    public static final String BUTTON_REFRESH_TEXT = "Actualiser la liste";
    
    // ==========================================
    // MESSAGES DE DIALOGUE
    // ==========================================
    
    /**
     * Messages d'information
     */
    public static final String INFO_ALREADY_CONNECTED = "Déjà connecté au serveur";
    public static final String INFO_ALREADY_DISCONNECTED = "Déjà déconnecté du serveur";
    public static final String INFO_CONNECTION_SUCCESS = "Connecté au serveur %s:%d";
    public static final String INFO_DISCONNECTION_SUCCESS = "Déconnecté du serveur";
    
    /**
     * Messages d'erreur
     */
    public static final String ERROR_NOT_CONNECTED = "Vous devez d'abord vous connecter au serveur";
    public static final String ERROR_CONNECTION_TITLE = "Erreur de connexion";
    public static final String ERROR_COMMUNICATION_TITLE = "Erreur de communication";
    public static final String ERROR_CONNECTION_TIMEOUT = "Timeout de connexion au serveur (%dms)";
    public static final String ERROR_CONNECTION_REFUSED = "Impossible de se connecter au serveur. Vérifiez que le serveur C est démarré sur %s:%d";
    public static final String ERROR_COMMUNICATION = "Erreur de communication avec le serveur : %s";
    public static final String ERROR_INVALID_RESPONSE = "Format de réponse invalide du serveur : %s";
    public static final String ERROR_UNEXPECTED_COMMAND = "Commande inattendue : %s";
    public static final String ERROR_SERVER_ERROR = "Erreur du serveur : %s";
    public static final String ERROR_INVALID_LINE_FORMAT = "Format de ligne invalide. Attendu : IP;Nom;Prénom;ID";
    public static final String ERROR_EMPTY_IP = "Adresse IP vide";
    public static final String ERROR_EMPTY_NAME = "Nom ou prénom vide";
    public static final String ERROR_INVALID_PATIENT_ID = "ID patient invalide : %d";
    public static final String ERROR_NON_NUMERIC_PATIENT_ID = "ID patient non numérique : %s";
    
    // ==========================================
    // RACCOURCIS CLAVIER
    // ==========================================
    
    /**
     * Raccourcis clavier
     */
    public static final String SHORTCUT_CONNECT = "ctrl C";
    public static final String SHORTCUT_DISCONNECT = "ctrl D";
    public static final String SHORTCUT_REFRESH = "F5";
    public static final String SHORTCUT_QUIT = "ctrl Q";
    
    // ==========================================
    // MESSAGES DE DÉMARRAGE
    // ==========================================
    
    /**
     * Messages affichés au démarrage
     */
    public static final String STARTUP_BANNER = "========================================";
    public static final String STARTUP_TITLE = "    CLIENT ADMIN - HÔPITAL";
    public static final String STARTUP_SERVER_INFO = "Serveur : %s:%d";
    public static final String STARTUP_PROTOCOL = "Protocole : ACBP (Admin Consultation Booking Protocol)";
    public static final String STARTUP_COMMAND = "Commande : LIST_CLIENTS";
    public static final String STARTUP_CONFIG_UPDATE = "Configuration serveur mise à jour : %s:%d";
    
    // ==========================================
    // MESSAGES DE LOG
    // ==========================================
    
    /**
     * Messages de log
     */
    public static final String LOG_CLIENT_STARTED = "Client Admin démarré - Serveur : %s:%d";
    public static final String LOG_CONNECTION_ATTEMPT = "Tentative de connexion à %s:%d";
    public static final String LOG_CONNECTION_SUCCESS = "Connexion établie avec succès !";
    public static final String LOG_CONNECTION_ESTABLISHED = "Connexion établie avec succès au serveur %s:%d";
    public static final String LOG_DISCONNECTED = "Déconnecté du serveur";
    public static final String LOG_BUTTON_CLICKED = "Bouton '%s' cliqué";
    public static final String LOG_SHORTCUT_USED = "Raccourci %s - %s";
    public static final String LOG_SERVER_RESPONSE = "Ligne reçue du serveur : %s";
    public static final String LOG_ADDITIONAL_LINE = "Ligne supplémentaire reçue : %s";
    public static final String LOG_END_OF_STREAM = "Fin de flux détectée";
    public static final String LOG_RAW_RESPONSE = "Réponse brute du serveur : %s";
    public static final String LOG_COMMAND_PARSED = "Commande : %s";
    public static final String LOG_STATUS_PARSED = "Status : %s";
    public static final String LOG_DATA_PARSED = "Données : %s";
    public static final String LOG_PARSING_ERROR = "Erreur lors du parsing de la ligne : %s - %s";
    public static final String LOG_CLOSE_ERROR = "Erreur lors de la fermeture du %s : %s";
    
    // ==========================================
    // FORMATS DE DONNÉES
    // ==========================================
    
    /**
     * Formats utilisés pour le parsing
     */
    public static final String DATA_SEPARATOR = ";";
    public static final String LINE_SEPARATOR = "\n";
    public static final String COMMAND_SEPARATOR = "#";
    public static final String EMPTY_CLIENT_MESSAGE = "Aucun client connecté";
    
    // ==========================================
    // VALIDATION
    // ==========================================
    
    /**
     * Valeurs de validation
     */
    public static final int MIN_PATIENT_ID = 1;
    public static final String EXPECTED_COMMAND = "LIST_CLIENTS";
    public static final String SUCCESS_STATUS = "ok";
    public static final int EXPECTED_COLUMNS = 4;
    
    // ==========================================
    // CONSTRUCTEUR PRIVÉ
    // ==========================================
    
    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private AdminClientConfig() {
        throw new UnsupportedOperationException("Cette classe ne doit pas être instanciée");
    }
}

