package hepl.fead.model.dao;

import hepl.fead.model.bd.ConnectBD;
import hepl.fead.model.entity.Patient;
import hepl.fead.model.viewmodel.PatientSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PatientDAO {

    private ArrayList<Patient> patients;

    public PatientDAO() {
        patients = new ArrayList<>();
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    //recuperer le patient
    public Patient getPatient(Integer id) {
        try {
            String query = "SELECT * FROM patient WHERE id = ?";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setFirst_name(rs.getString("first_name"));
                patient.setLast_name(rs.getString("last_name"));
                patient.setBirth_date(rs.getString("birth_date"));
                rs.close();
                ps.close();
                return patient;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(PatientDAO.class.getName()).warning(e.getMessage());
        }
        return null;
    }

    //recuperer tout les patients sans critere
    public ArrayList<Patient> load() {
        return this.load(null);
    }

    //recuperer les patient avec critere
    public ArrayList<Patient> load(PatientSearchVM patientSearchVM) {
        ArrayList<Patient> patients = new ArrayList<>();
        try {
            String query = "SELECT * FROM patient WHERE 1=1";

            if (patientSearchVM != null) {
                if (patientSearchVM.getFirstName() != null && !patientSearchVM.getFirstName().isEmpty()) {
                    query += " AND first_name LIKE ?";
                }
                if (patientSearchVM.getLastName() != null && !patientSearchVM.getLastName().isEmpty()) {
                    query += " AND last_name LIKE ?";
                }
                if (patientSearchVM.getBirthDateFrom() != null && !patientSearchVM.getBirthDateFrom().isEmpty()) {
                    query += " AND birth_date = ?";
                }
                if (patientSearchVM.getBirthDateTo() != null && !patientSearchVM.getBirthDateTo().isEmpty()) {
                    query += " AND birth_date = ?";
                }
            }

            query += " ORDER BY id DESC";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);

            if (patientSearchVM != null) {
                int index = 1;
                if (patientSearchVM.getFirstName() != null && !patientSearchVM.getFirstName().isEmpty()) {
                    ps.setString(index++, "%" + patientSearchVM.getFirstName() + "%");
                }
                if (patientSearchVM.getLastName() != null && !patientSearchVM.getLastName().isEmpty()) {
                    ps.setString(index++, "%" + patientSearchVM.getLastName() + "%");
                }
                if (patientSearchVM.getBirthDateFrom() != null && !patientSearchVM.getBirthDateFrom().isEmpty()) {
                    ps.setString(index++, patientSearchVM.getBirthDateFrom());
                }
                if (patientSearchVM.getBirthDateTo() != null && !patientSearchVM.getBirthDateTo().isEmpty()) {
                    ps.setString(index++, patientSearchVM.getBirthDateTo());
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setFirst_name(rs.getString("first_name"));
                patient.setLast_name(rs.getString("last_name"));
                patient.setBirth_date(rs.getString("birth_date"));
                patients.add(patient);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patients;
    }

    //save soit insert soit update
    public void save(Patient patient) {
        try {
            if (patient == null || patient.getFirst_name() == null || patient.getLast_name() == null) {
                return;
            }

            if (patient.getId() != null) { // Update
                String query = "UPDATE patient SET first_name = ?, last_name = ?, birth_date = ? WHERE id = ?";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
                ps.setString(1, patient.getFirst_name());
                ps.setString(2, patient.getLast_name());
                ps.setString(3, patient.getBirth_date());
                ps.setInt(4, patient.getId());
                ps.executeUpdate();
                ps.close();
            } else { // Insert
                String query = "INSERT INTO patient (first_name, last_name, birth_date) VALUES (?, ?, ?)";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, patient.getFirst_name());
                ps.setString(2, patient.getLast_name());
                ps.setString(3, patient.getBirth_date());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    patient.setId(rs.getInt(1));
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convenience method used by server protocol to quickly add a patient
     * and return the generated id.
     */
    public int addPatient(String lastName, String firstName) {
        Patient p = new Patient();
        p.setFirst_name(firstName);
        p.setLast_name(lastName);
        save(p);
        return p.getId() == null ? -1 : p.getId();
    }

    //suppression
    public void delete(Patient patient) {
        if (patient != null && patient.getId() != null) {
            delete(patient.getId());
        }
    }

    //suppression dans la bd
    public void delete(Integer id) {
        if (id != null) {
            try {
                String query = "DELETE FROM patient WHERE id = ?";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
