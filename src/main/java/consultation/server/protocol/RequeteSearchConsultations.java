package consultation.server.protocol;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.dao.DAOFactory;
import hepl.fead.model.entity.Consultation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.List;

public class RequeteSearchConsultations implements Requete {
    private static final long serialVersionUID = 1L;
    
    private transient Integer doctorId;
    private transient Integer patientId;
    private transient LocalDate fromDate;
    private transient LocalDate toDate;
    private transient String fromDateString;
    private transient String toDateString;
    public RequeteSearchConsultations(Integer doctorId, Integer patientId, LocalDate fromDate, LocalDate toDate) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromDateString = fromDate != null ? fromDate.toString() : null;
        this.toDateString = toDate != null ? toDate.toString() : null;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        String fromStr = fromDateString != null ? fromDateString : (fromDate != null ? fromDate.toString() : null);
        String toStr = toDateString != null ? toDateString : (toDate != null ? toDate.toString() : null);
        out.writeObject(doctorId);
        out.writeObject(patientId);
        out.writeObject(fromStr);
        out.writeObject(toStr);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.doctorId = (Integer) in.readObject();
        this.patientId = (Integer) in.readObject();
        String fromStr = (String) in.readObject();
        String toStr = (String) in.readObject();
        
        this.fromDateString = fromStr;
        this.toDateString = toStr;
        
        if (fromStr != null && !fromStr.isEmpty()) {
            try {
                this.fromDate = LocalDate.parse(fromStr);
            } catch (Exception e) {
                this.fromDate = null;
            }
        } else {
            this.fromDate = null;
        }
        if (toStr != null && !toStr.isEmpty()) {
            try {
                this.toDate = LocalDate.parse(toStr);
            } catch (Exception e) {
                this.toDate = null;
            }
        } else {
            this.toDate = null;
        }
    }
    @Override
    public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
        ConsultationDAO dao = daoFactory.getConsultationDAO();
        List<Consultation> list = dao.searchConsultations(doctorId, patientId, fromDate, toDate);
        return new ReponseTraitee(true, "Consultations trouvées", list);
    }
}
