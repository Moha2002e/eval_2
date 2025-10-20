package hepl.fead.model.adminclient;

/**
 * Classe représentant les informations d'un client connecté
 * Utilisée pour stocker les données reçues du serveur C
 */
public class ClientInfo {
    private String ipAddress;
    private String lastName;
    private String firstName;
    private int patientId;

    /**
     * Constructeur avec tous les paramètres
     * @param ipAddress Adresse IP du client
     * @param lastName Nom de famille du patient
     * @param firstName Prénom du patient
     * @param patientId ID du patient dans la base de données
     */
    public ClientInfo(String ipAddress, String lastName, String firstName, int patientId) {
        this.ipAddress = ipAddress;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patientId = patientId;
    }

    // Getters
    public String getIpAddress() {
        return ipAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getPatientId() {
        return patientId;
    }

    /**
     * Représentation textuelle de l'objet
     * @return String formatée pour l'affichage
     */
    @Override
    public String toString() {
        return String.format("%s - %s %s (ID: %d)", ipAddress, firstName, lastName, patientId);
    }

    /**
     * Comparaison d'égalité basée sur l'ID du patient
     * @param obj Objet à comparer
     * @return true si les objets sont égaux
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientInfo that = (ClientInfo) obj;
        return patientId == that.patientId;
    }

    /**
     * Hash code basé sur l'ID du patient
     * @return Hash code de l'objet
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(patientId);
    }
}
