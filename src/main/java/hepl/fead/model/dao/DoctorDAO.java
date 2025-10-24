package hepl.fead.model.dao;

import hepl.fead.model.bd.ConnectBD;
import hepl.fead.model.entity.Doctor;
import hepl.fead.model.viewmodel.DoctorSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DoctorDAO {

    private ArrayList<Doctor> doctors;

    public DoctorDAO() {
        doctors = new ArrayList<>();
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }
    //recuperer le docteur
    public Doctor getDoctor(Integer id) {
        try {
            String query = "SELECT * FROM doctor WHERE id = ?";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setFirst_name(rs.getString("first_name"));
                doctor.setLast_name(rs.getString("last_name"));
                doctor.setSpecialite_id(rs.getInt("specialite_id"));
                doctor.setPassword(rs.getString("password"));
                rs.close();
                ps.close();
                return doctor;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(DoctorDAO.class.getName()).warning(e.getMessage());
        }
        return null;
    }

    // recuperer tous les docteurs sans critere
    public ArrayList<Doctor> load() {
        return this.load(null);
    }

    // recuperer les docteurs avec des criteres
    public ArrayList<Doctor> load(DoctorSearchVM doctorSearchVM) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            String query = "SELECT * FROM doctor WHERE 1=1";

            if (doctorSearchVM != null) {
                if (doctorSearchVM.getFirstName() != null && !doctorSearchVM.getFirstName().isEmpty()) {
                    query += " AND first_name LIKE ?";
                }
                if (doctorSearchVM.getLastName() != null && !doctorSearchVM.getLastName().isEmpty()) {
                    query += " AND last_name LIKE ?";
                }
                if (doctorSearchVM.getSpecialityName() != null && !doctorSearchVM.getSpecialityName().isEmpty()) {
                    query += " AND speciality_name LIKE ?";
                }
            }

            query += " ORDER BY id DESC";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);

            if (doctorSearchVM != null) {
                int index = 1;
                if (doctorSearchVM.getFirstName() != null && !doctorSearchVM.getFirstName().isEmpty()) {
                    ps.setString(index++, "%" + doctorSearchVM.getFirstName() + "%");
                }
                if (doctorSearchVM.getLastName() != null && !doctorSearchVM.getLastName().isEmpty()) {
                    ps.setString(index++, "%" + doctorSearchVM.getLastName() + "%");
                }
                if (doctorSearchVM.getSpecialityName() != null && !doctorSearchVM.getSpecialityName().isEmpty()) {
                    ps.setString(index++, "%" + doctorSearchVM.getSpecialityName() + "%");
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setFirst_name(rs.getString("first_name"));
                doctor.setLast_name(rs.getString("last_name"));
                doctor.setSpecialite_id(rs.getInt("specialite_id"));
                doctor.setPassword(rs.getString("password"));
                doctors.add(doctor);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return doctors;
    }

    // save donc update ou insert
    public void save(Doctor doctor) {
        try {
            if (doctor == null || doctor.getFirst_name() == null || doctor.getLast_name() == null) {
                return;
            }

            if (doctor.getId() != null) { // Update
                String query = "UPDATE doctor SET first_name = ?, last_name = ?, specialite_id = ?, password = ? WHERE id = ?";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
                ps.setString(1, doctor.getFirst_name());
                ps.setString(2, doctor.getLast_name());
                ps.setInt(3, doctor.getSpecialite_id());
                ps.setString(4, doctor.getPassword());
                ps.setInt(5, doctor.getId());
                ps.executeUpdate();
                ps.close();
            } else { // Insert
                String query = "INSERT INTO doctor (first_name, last_name, specialite_id, password) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, doctor.getFirst_name());
                ps.setString(2, doctor.getLast_name());
                ps.setInt(3, doctor.getSpecialite_id());
                ps.setString(4, doctor.getPassword());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    doctor.setId(rs.getInt(1));
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // suppression
    public void delete(Doctor doctor) {
        if (doctor != null && doctor.getId() != null) {
            delete(doctor.getId());
        }
    }

    // suppression dans la bd
    public void delete(Integer id) {
        if (id != null) {
            try {
                String query = "DELETE FROM doctor WHERE id = ?";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Doctor login(String login, String password) {
        if (login == null || login.isEmpty() || password == null) {
            return null;
        }

        try {
            String query = "SELECT * FROM doctor WHERE (first_name = ? OR last_name = ? OR CONCAT(first_name, ' ', last_name) = ?) AND password = ?";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
            ps.setString(1, login);
            ps.setString(2, login);
            ps.setString(3, login);
            ps.setString(4, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setFirst_name(rs.getString("first_name"));
                doctor.setLast_name(rs.getString("last_name"));
                doctor.setSpecialite_id(rs.getInt("specialite_id"));
                doctor.setPassword(rs.getString("password"));
                rs.close();
                ps.close();
                return doctor;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(DoctorDAO.class.getName()).warning(e.getMessage());
        }

        return null;
    }
}
