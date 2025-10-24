package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import hepl.fead.model.entity.Consultation;

public class RequeteUpdateConsultation implements Requete {
    private final int consultationId;
    private final LocalDate newDate;
    private final LocalTime newTime;
    private final Integer patientId;
    private final String reason;
    public RequeteUpdateConsultation(int consultationId, LocalDate newDate, LocalTime newTime, Integer patientId, String reason) {
        this.consultationId = consultationId;
        this.newDate = newDate;
        this.newTime = newTime;
        this.patientId = patientId;
        this.reason = reason;
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO dao = daoFactory.getConsultationDAO();
        Consultation existing = dao.getConsultationById(consultationId);
        if (existing == null) {
            return new ReponseTraitee(false, "Consultation introuvable", null);
        }
        if (newDate != null) existing.setDate(newDate.toString());
        if (newTime != null) existing.setHour(newTime.toString());
        if (patientId != null) existing.setPatient_id(patientId);
        if (reason != null) existing.setReason(reason);
        dao.save(existing);
        return new ReponseTraitee(true, "Consultation mise à jour", null);
    }
}
