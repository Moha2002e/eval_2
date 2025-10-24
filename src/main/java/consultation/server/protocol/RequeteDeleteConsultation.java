package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;

public class RequeteDeleteConsultation implements Requete {
    private final int consultationId;
    public RequeteDeleteConsultation(int consultationId) {
        this.consultationId = consultationId;
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO dao = daoFactory.getConsultationDAO();
        boolean deleted = dao.deleteConsultation(consultationId);
        if (deleted) {
            return new ReponseTraitee(true, "Consultation supprimée", null);
        }
        return new ReponseTraitee(false, "La consultation ne peut pas être supprimée", null);
    }
}
