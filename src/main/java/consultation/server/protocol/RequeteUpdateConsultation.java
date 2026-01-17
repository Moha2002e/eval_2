package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import hepl.fead.model.entity.Consultation;

public class RequeteUpdateConsultation implements Requete {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private transient int consultationId;
    private transient LocalDate newDate;
    private transient LocalTime newTime;
    private transient Integer patientId;
    private transient String reason;
    private transient String duree;
    private transient String newDateString;
    private transient String newTimeString;

    public RequeteUpdateConsultation(int consultationId, LocalDate newDate, LocalTime newTime, Integer patientId,
            String reason) {
        this.consultationId = consultationId;
        this.newDate = newDate;
        this.newTime = newTime;
        this.patientId = patientId;
        this.reason = reason;
        this.duree = null;
        this.newDateString = newDate != null ? newDate.toString() : null;
        this.newTimeString = newTime != null ? newTime.toString() : null;
    }

    public RequeteUpdateConsultation(int consultationId, LocalDate newDate, LocalTime newTime, Integer patientId,
            String reason, String duree) {
        this.consultationId = consultationId;
        this.newDate = newDate;
        this.newTime = newTime;
        this.patientId = patientId;
        this.reason = reason;
        this.duree = duree;
        this.newDateString = newDate != null ? newDate.toString() : null;
        this.newTimeString = newTime != null ? newTime.toString() : null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        String dateStr = newDateString != null ? newDateString : (newDate != null ? newDate.toString() : null);
        String timeStr = newTimeString != null ? newTimeString : (newTime != null ? newTime.toString() : null);
        out.writeInt(consultationId);
        out.writeObject(patientId);
        out.writeObject(reason);
        out.writeObject(dateStr);
        out.writeObject(timeStr);
        // out.writeObject(duree); // Removed to match Client
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.consultationId = in.readInt();
        this.patientId = (Integer) in.readObject();
        this.reason = (String) in.readObject();
        String dateStr = (String) in.readObject();
        String timeStr = (String) in.readObject();
        // this.duree = (String) in.readObject(); // Removed to match Client

        this.newDateString = dateStr;
        this.newTimeString = timeStr;

        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                this.newDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                this.newDate = null;
            }
        } else {
            this.newDate = null;
        }
        if (timeStr != null && !timeStr.isEmpty()) {
            try {
                if (timeStr.length() == 5 && timeStr.indexOf(':') == 2) {
                    this.newTime = LocalTime.parse(timeStr, TIME_FORMATTER);
                } else {
                    this.newTime = LocalTime.parse(timeStr);
                }
            } catch (Exception e) {
                this.newTime = null;
            }
        } else {
            this.newTime = null;
        }
    }

    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO dao = daoFactory.getConsultationDAO();
        Consultation existing = dao.getConsultationById(consultationId);
        if (existing == null) {
            return new ReponseTraitee(false, "Consultation introuvable", null);
        }
        if (newDate != null)
            existing.setDate(newDate.toString());
        if (newTime != null)
            existing.setHour(newTime.toString());
        if (patientId != null)
            existing.setPatient_id(patientId);
        if (reason != null)
            existing.setReason(reason);
        if (duree != null)
            existing.setDuree(duree);
        dao.save(existing);
        return new ReponseTraitee(true, "Consultation mise à jour", null);
    }
}
