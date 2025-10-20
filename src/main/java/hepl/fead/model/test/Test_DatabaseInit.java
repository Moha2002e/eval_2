package hepl.fead.model.test;

import hepl.fead.model.bd.ConnectBD;
import hepl.fead.model.dao.SpecialtyDAO;
import hepl.fead.model.dao.PatientDAO;
import hepl.fead.model.dao.DoctorDAO;
import hepl.fead.model.entity.Speciality;
import hepl.fead.model.entity.Patient;
import hepl.fead.model.entity.Doctor;

import java.sql.Connection;
import java.util.ArrayList;

import static hepl.fead.model.test.Const_Test.*;

public class Test_DatabaseInit {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_INIT_BD));
        System.out.println(SEPARATEUR + "\n");

        try {
            System.out.println(String.format(FORMAT_TEST, 1, CONNEXION_BD));
            System.out.println(SEPARATEUR_COURT);
            Connection conn = ConnectBD.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println(CONNEXION_ETABLIE);
                System.out.println(String.format(FORMAT_DETAIL, LABEL_URL, conn.getMetaData().getURL()));
                System.out.println(String.format(FORMAT_DETAIL, LABEL_BD, conn.getCatalog()));
            } else {
                System.out.println(ECHEC);
                return;
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 2, TABLE_SPECIALTIES));
            System.out.println(SEPARATEUR_COURT);
            SpecialtyDAO specialtyDAO = new SpecialtyDAO();
            ArrayList<Speciality> specialties = specialtyDAO.load();
            System.out.println(TABLE_ACCESSIBLE);
            System.out.println(String.format(NB_ENREGISTREMENTS, specialties.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 3, TABLE_PATIENT));
            System.out.println(SEPARATEUR_COURT);
            PatientDAO patientDAO = new PatientDAO();
            ArrayList<Patient> patients = patientDAO.load();
            System.out.println(TABLE_ACCESSIBLE);
            System.out.println(String.format(NB_ENREGISTREMENTS, patients.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 4, TABLE_DOCTOR));
            System.out.println(SEPARATEUR_COURT);
            DoctorDAO doctorDAO = new DoctorDAO();
            ArrayList<Doctor> doctors = doctorDAO.load();
            System.out.println(TABLE_ACCESSIBLE);
            System.out.println(String.format(NB_ENREGISTREMENTS, doctors.size()));

            System.out.println("\n" + SEPARATEUR);
            System.out.println(String.format(FORMAT_TITRE, SUCCES));
            System.out.println(SEPARATEUR);

        } catch (Exception e) {
            System.err.println("\n" + ERREUR);
            System.err.println(SEPARATEUR_COURT);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
