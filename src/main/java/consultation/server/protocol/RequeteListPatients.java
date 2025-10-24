package consultation.server.protocol;

import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.dao.PatientDAO;
import hepl.fead.model.entity.Patient;

import java.util.List;

public class RequeteListPatients implements Requete {
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        PatientDAO dao = daoFactory.getPatientDAO();
        java.util.List<Patient> list = dao.load();
        return new ReponseTraitee(true, "Liste des patients", list);
    }
}
