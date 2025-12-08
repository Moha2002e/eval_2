package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.entity.Consultation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RequeteAddConsultation implements Requete {
    private static final long serialVersionUID = 1L;
    
    private final int doctorId;
    private final LocalDate date;
    private final LocalTime time;
    private final int count;
    public RequeteAddConsultation(int doctorId, LocalDate date, LocalTime time, int count) {
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.count = count;
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO consultationDAO = daoFactory.getConsultationDAO();
        List<Consultation> created = new java.util.ArrayList<>();
        java.time.LocalTime slot = this.time;
        // Check that the created slots do not exceed 17:00 (end time). We assume each slot is 30 minutes.
        java.time.LocalTime endIfCreated = this.time.plusMinutes(30L * this.count);
        if (endIfCreated.isAfter(java.time.LocalTime.of(17, 0))) {
            return new ReponseTraitee(false, "Création refusée : les plages dépasseraient 17:00", null);
        }
        for (int i = 0; i < count; i++) {
            Consultation c = new Consultation();
            c.setDoctor_id(this.doctorId);
            // store date and hour as strings to match DAO expectations
            c.setDate(this.date.toString());
            c.setHour(slot.toString());
            c.setReason("");
            c.setPatient_id(null);
            consultationDAO.save(c);
            created.add(c);
            slot = slot.plusMinutes(30);
        }
        return new ReponseTraitee(true, "Consultations créées", created);
    }
}
