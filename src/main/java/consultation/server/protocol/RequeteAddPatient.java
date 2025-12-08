package consultation.server.protocol;

import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.dao.PatientDAO;

public class RequeteAddPatient implements Requete {
    private static final long serialVersionUID = 1L;
    
    private final String nom;
    private final String prenom;
    public RequeteAddPatient(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        PatientDAO patientDAO = daoFactory.getPatientDAO();
        int id = patientDAO.addPatient(nom, prenom);
        return new ReponseTraitee(true, "Patient ajouté", id);
    }
}
