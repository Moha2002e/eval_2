package hepl.fead.model.dao;

import hepl.fead.model.bd.ConnectBD;
import hepl.fead.model.entity.Speciality;
import hepl.fead.model.viewmodel.SpecialitySearchVM; // à créer si besoin

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SpecialtyDAO {

    private ArrayList<Speciality> specialties;

    public SpecialtyDAO() {
        specialties = new ArrayList<>();
    }

    public ArrayList<Speciality> getSpecialties() {
        return specialties;
    }

    // Récupérer une spécialité par ID
    public Speciality getSpeciality(Integer id) {
        try {
            String query = "SELECT * FROM specialties WHERE id = ?";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Speciality speciality = new Speciality();
                speciality.setId(rs.getInt("id"));
                speciality.setName(rs.getString("name"));
                rs.close();
                ps.close();

                return speciality;
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            Logger.getLogger(SpecialtyDAO.class.getName()).warning(e.getMessage());
        }
        return null;
    }

    // Récupérer toutes les spécialités sans critère
    public ArrayList<Speciality> load() {
        return this.load(null);
    }

    // Récupérer les spécialités avec critères
    public ArrayList<Speciality> load(SpecialitySearchVM searchVM) {
        ArrayList<Speciality> specialties = new ArrayList<>();
        try {
            String query = "SELECT * FROM specialties WHERE 1=1";

            if (searchVM != null) {
                if (searchVM.getName() != null && !searchVM.getName().isEmpty()) {
                    query += " AND name LIKE ?";
                }
            }

            query += " ORDER BY id DESC";
            PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);

            if (searchVM != null) {
                int index = 1;
                if (searchVM.getName() != null && !searchVM.getName().isEmpty()) {
                    ps.setString(index++, "%" + searchVM.getName() + "%");
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Speciality speciality = new Speciality();
                speciality.setId(rs.getInt("id"));
                speciality.setName(rs.getString("name"));
                specialties.add(speciality);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return specialties;
    }

    // Save (insert ou update)
    public void save(Speciality speciality) {
        try {
            if (speciality == null || speciality.getName() == null || speciality.getName().isEmpty()) {
                return;
            }

            if (speciality.getId() != null) { // Update
                String query = "UPDATE specialties SET name = ? WHERE id = ?";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);
                ps.setString(1, speciality.getName());
                ps.setInt(2, speciality.getId());
                ps.executeUpdate();
                ps.close();
            } else { // Insert
                String query = "INSERT INTO specialties (name) VALUES (?)";
                PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, speciality.getName());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    speciality.setId(rs.getInt(1));
                }
                rs.close();
                ps.close();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Supprimer par objet
    public void delete(Speciality speciality) {
        if (speciality != null && speciality.getId() != null) {
            delete(speciality.getId());
        }
    }

    // Supprimer par ID
    public void delete(Integer id) {
        if (id != null) {
            try {
                String query = "DELETE FROM specialties WHERE id = ?";
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
