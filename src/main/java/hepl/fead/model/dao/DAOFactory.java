package hepl.fead.model.dao;

/**
 * DAOFactory simplifié : les DAO du projet utilisent des constructeurs sans arguments
 * et se connectent via `ConnectBD.getConnection()` en interne. Cette classe
 * retourne simplement de nouvelles instances des DAO.
 */
public class DAOFactory {
    public DAOFactory() {
    }

    public ConsultationDAO getConsultationDAO() {
        return new ConsultationDAO();
    }

    public DoctorDAO getDoctorDAO() {
        return new DoctorDAO();
    }

    public PatientDAO getPatientDAO() {
        return new PatientDAO();
    }
}
