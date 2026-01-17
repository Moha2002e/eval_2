package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.entity.Consultation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequeteAddConsultation implements Requete {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private transient int doctorId;
    private transient LocalDate date;
    private transient LocalTime time;
    private transient int count;
    private transient String dateString;
    private transient String timeString;
    private transient int duree;

    public RequeteAddConsultation(int doctorId, LocalDate date, LocalTime time, int count) {
        this(doctorId, date, time, count, 0);
    }

    public RequeteAddConsultation(int doctorId, LocalDate date, LocalTime time, int count, int duree) {
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.count = count;
        this.dateString = date != null ? date.toString() : null;
        this.timeString = time != null ? time.toString() : null;
        this.duree = duree;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        String dateStr = dateString != null ? dateString : (date != null ? date.toString() : null);
        String timeStr = timeString != null ? timeString : (time != null ? time.toString() : null);
        out.writeInt(doctorId);
        out.writeInt(duree);
        out.writeInt(count);
        out.writeObject(dateStr);
        out.writeObject(timeStr);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.doctorId = in.readInt();
        this.duree = in.readInt();
        this.count = in.readInt();
        String dateStr = (String) in.readObject();
        String timeStr = (String) in.readObject();
        this.dateString = dateStr;
        this.timeString = timeStr;

        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                this.date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                throw new IOException("Erreur lors de la désérialisation de la date: " + dateStr, e);
            }
        } else {
            throw new IOException("dateString ne peut pas être null ou vide");
        }
        if (timeStr != null && !timeStr.isEmpty()) {
            try {
                if (timeStr.length() == 5 && timeStr.indexOf(':') == 2) {
                    this.time = LocalTime.parse(timeStr, TIME_FORMATTER);
                } else {
                    this.time = LocalTime.parse(timeStr);
                }
            } catch (Exception e) {
                throw new IOException("Erreur lors de la désérialisation de l'heure: " + timeStr, e);
            }
        } else {
            throw new IOException("timeString ne peut pas être null ou vide");
        }
    }

    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO consultationDAO = daoFactory.getConsultationDAO();
        List<Consultation> created = new java.util.ArrayList<>();
        java.time.LocalTime slot = this.time;
        java.time.LocalTime endIfCreated = this.time.plusMinutes(30L * this.count);
        if (endIfCreated.isAfter(java.time.LocalTime.of(17, 0))) {
            return new ReponseTraitee(false, "Création refusée : les plages dépasseraient 17:00", null);
        }
        for (int i = 0; i < count; i++) {
            Consultation c = new Consultation();
            c.setDoctor_id(this.doctorId);
            c.setDate(this.date.toString());
            c.setHour(slot.toString());
            c.setReason("");
            c.setPatient_id(null);
            // appliquer duree si fournie
            if (this.duree > 0)
                c.setDuree(String.valueOf(this.duree));
            consultationDAO.save(c);
            created.add(c);
            slot = slot.plusMinutes(30);
        }
        return new ReponseTraitee(true, "Consultations créées", created);
    }
}
