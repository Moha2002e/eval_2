package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.entity.Consultation;

import java.time.LocalDate;
import java.util.List;

public class RequeteSearchConsultations implements Requete {
    private static final long serialVersionUID = 1L;
    
    private final Integer doctorId;
    private final Integer patientId;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    public RequeteSearchConsultations(Integer doctorId, Integer patientId, LocalDate fromDate, LocalDate toDate) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO dao = daoFactory.getConsultationDAO();
        List<Consultation> list = dao.searchConsultations(doctorId, patientId, fromDate, toDate);
        return new ReponseTraitee(true, "Consultations trouvées", list);
    }
}
