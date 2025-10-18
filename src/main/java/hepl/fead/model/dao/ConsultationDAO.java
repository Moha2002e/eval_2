package hepl.fead.model.dao;

import hepl.fead.model.bd.ConnectBD;
import hepl.fead.model.entity.Consultation;
import hepl.fead.model.viewmodel.ConsultationSearchVM;

import java.io.Console;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultationDAO {

    private ArrayList<Consultation> consultations;
    public ConsultationDAO() {
        consultations = new ArrayList<>();
    }
    public ArrayList<Consultation> getConsultations() {
        return consultations;
    }
    public Consultation getConsultationById(int id) {
        try{
            String query = """
                    SELECT c.* FROM consultations c \
                    LEFT JOIN patient p ON c.patient_id = p.id \
                    LEFT JOIN doctor d ON c.doctor_id = d.id \
                    LEFT JOIN specialties s ON d.specialite_id = s.id \
                    WHERE c.id = ?""";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Consultation consultation = new Consultation();
                consultation.setId(rs.getInt("id"));
                consultation.setDoctor_id(rs.getInt("doctor_id"));
                consultation.setPatient_id(rs.getInt("patient_id"));
                consultation.setDate(rs.getString("date"));
                consultation.setHour(rs.getString("hour"));
                consultation.setReason(rs.getString("reason"));
                rs.close();
                ps.close();
                return consultation;
            }
            rs.close();
            ps.close();
        }catch(Exception e){
            Logger.getLogger(ConsultationDAO.class.getName()).warning(e.getMessage());
        }
        return null;
    }
    public ArrayList<Consultation>load(){
        return this.load(null);

    }
    public ArrayList<Consultation>load(ConsultationSearchVM csearchvm){
        ArrayList<Consultation> consultations = new ArrayList<>();
        try{
            String query ="SELECT c.* FROM consultations c " +
                    "LEFT JOIN patient p ON c.patient_id = p.id " +
                    "LEFT JOIN doctor d ON c.doctor_id = d.id " +
                    "WHERE 1=1 ";
            if(csearchvm!=null){
                if(csearchvm.getPatientName()!=null && !csearchvm.getPatientName().isEmpty()){
                    query += "AND p.last_name LIKE ? ";
                }
                if(csearchvm.getDoctorName()!=null && !csearchvm.getDoctorName().isEmpty()){
                    query += "AND d.last_name LIKE ? ";
                }
                if (csearchvm.getDateFrom()!=null && !csearchvm.getDateFrom().isEmpty()){
                    query += "AND c.date >= ? ";
                }
                if (csearchvm.getDateTo()!=null && !csearchvm.getDateTo().isEmpty()){
                    query += "AND c.date <= ? ";
                }
                if (csearchvm.getReason()!=null && !csearchvm.getReason().isEmpty()){
                    query += "AND c.reason LIKE ? ";
                }
            }
            query += " ORDER BY c.date desc";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
           if (csearchvm!=null) {
               int paraDeREchercher = 1;
               if (csearchvm.getPatientName() != null && !csearchvm.getPatientName().isEmpty()) {
                ps.setString(paraDeREchercher++, "%"+csearchvm.getPatientName()+"%");
               }
               if (csearchvm.getDoctorName() != null && !csearchvm.getDoctorName().isEmpty()) {
                   ps.setString(paraDeREchercher++, "%"+csearchvm.getDoctorName()+"%");

               }
               if (csearchvm.getDateFrom() != null && !csearchvm.getDateFrom().isEmpty()) {
                   ps.setString(paraDeREchercher++, "%"+csearchvm.getDateFrom()+"%");
               }
               if (csearchvm.getDateTo() !=null && !csearchvm.getDateTo().isEmpty()) {
                   ps.setString(paraDeREchercher++, "%"+csearchvm.getDateTo()+"%");
               }
               if (csearchvm.getReason() != null && !csearchvm.getReason().isEmpty()) {
                   ps.setString(paraDeREchercher++, "%"+csearchvm.getReason()+"%");
               }
           }
           ResultSet rs = ps.executeQuery();
           consultations.clear();
           while (rs.next()) {
               Consultation consultation = new Consultation();
               consultation.setId(rs.getInt("id"));
               consultation.setDoctor_id(rs.getInt("doctor_id"));
               consultation.setPatient_id(rs.getInt("patient_id"));
               consultation.setDate(rs.getString("date"));
               consultation.setHour(rs.getString("hour"));
               consultation.setReason(rs.getString("reason"));

               consultations.add(consultation);
           }
           rs.close();
           ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            return consultations;
        }


    }
    public void save(Consultation consultation){
        try{
            String query;
            if(consultation != null){
                if (consultation.getId()!=null){//update
                    if (consultation.getDoctor_id()== null||consultation.getPatient_id()==null){
                        return;
                    }
                    query = "UPDATE consultations SET " +
                    "doctor_id = ?, " +
                            "patient_id = ?, " +
                            "date = ?, " +
                            "hour = ?, " +
                            "reason = ? " +
                            "WHERE id = ?";
                    PreparedStatement pStmt = ConnectBD.getConnection().prepareStatement(query);
                    pStmt.setInt(1, consultation.getDoctor_id());
                    pStmt.setInt(2, consultation.getPatient_id());
                    pStmt.setString(3, consultation.getDate());
                    pStmt.setString(4, consultation.getHour());
                    pStmt.setString(5, consultation.getReason());
                    pStmt.setInt(6, consultation.getId());
                    pStmt.executeUpdate();
                    pStmt.close();
                }else {// Insert into
                    if(consultation.getDoctor_id()==null||consultation.getPatient_id()==null){
                        return;
                    }
                    query = "INSERT INTO consultations (" +
                    "doctor_id, " +
                            "patient_id, " +
                            "date, " +
                            "hour, " +
                            "reason " +
                            ") VALUES (" +
                            "?, " +
                            "?, " +
                            "?, " +
                            "?, " +
                            "? " +
                            ")";
                    PreparedStatement pStmt = ConnectBD.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                    pStmt.setInt(1, consultation.getDoctor_id());
                    pStmt.setInt(2, consultation.getPatient_id());
                    pStmt.setString(3, consultation.getDate());
                    pStmt.setString(4, consultation.getHour());
                    pStmt.setString(5, consultation.getReason());
                    pStmt.executeUpdate();
                    ResultSet rs = pStmt.getGeneratedKeys();
                    rs.next();
                    consultation.setId((int) rs.getLong(1));
                    rs.close();
                    pStmt.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete(Consultation consultation){
        if(consultation!=null && consultation.getId()!=null){
            this.delete(consultation.getId());
        }
    }
    public void delete (Integer id){
        if(id !=null){
            try {
                String query = "DELETE FROM consultations WHERE id = ?";
                PreparedStatement statement = ConnectBD.getConnection().prepareStatement(query);
                statement.setInt(1, id);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
