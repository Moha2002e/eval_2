package hepl.fead.model.test;

import hepl.fead.model.dao.DoctorDAO;
import hepl.fead.model.entity.Doctor;
import hepl.fead.model.viewmodel.DoctorSearchVM;

import java.util.ArrayList;

import static hepl.fead.model.test.Const_Test.*;

public class Test_DAO_Doctor {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_DAO_DOCTOR));
        System.out.println(SEPARATEUR + "\n");

        DoctorDAO doctorDAO = new DoctorDAO();

        try {
            System.out.println(String.format(FORMAT_TEST, 1, LIRE_TOUS));
            ArrayList<Doctor> doctors = doctorDAO.load();
            System.out.println(String.format(NB_ENREGISTREMENTS, doctors.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 2, AJOUTER));
            Doctor nouveau = new Doctor();
            nouveau.setFirst_name(DOCTOR_PRENOM);
            nouveau.setLast_name(DOCTOR_NOM);
            nouveau.setPassword(DOCTOR_PASSWORD);
            nouveau.setSpecialite_id(1);
            doctorDAO.save(nouveau);
            System.out.println(String.format(ENREGISTREMENT_AJOUTE, nouveau.getId()));

            System.out.println("\n" + String.format(FORMAT_TEST, 3, MODIFIER));
            nouveau.setFirst_name(DOCTOR_PRENOM_2);
            nouveau.setLast_name(DOCTOR_NOM_2 + SUFFIX_TEST);
            nouveau.setSpecialite_id(2);
            doctorDAO.save(nouveau);
            Doctor verif = doctorDAO.getDoctor(nouveau.getId());
            if (verif != null && verif.getLast_name().contains(SUFFIX_TEST)) {
                System.out.println(ENREGISTREMENT_MODIFIE);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 4, RECHERCHER));
            DoctorSearchVM search = new DoctorSearchVM();
            search.setLastName(RECHERCHE_NOM_DUBOIS);
            ArrayList<Doctor> resultats = doctorDAO.load(search);
            System.out.println(String.format(RESULTATS_RECHERCHE, resultats.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 5, SUPPRIMER));
            Doctor temp = new Doctor();
            temp.setFirst_name(DOCTOR_TEMP_PRENOM);
            temp.setLast_name(DOCTOR_TEMP_NOM);
            temp.setPassword(DOCTOR_PASSWORD);
            temp.setSpecialite_id(1);
            doctorDAO.save(temp);
            Integer idSuppr = temp.getId();
            doctorDAO.delete(idSuppr);
            if (doctorDAO.getDoctor(idSuppr) == null) {
                System.out.println(ENREGISTREMENT_SUPPRIME);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 6, LIRE_PAR_ID));
            doctors = doctorDAO.load();
            if (!doctors.isEmpty()) {
                Doctor d = doctorDAO.getDoctor(doctors.get(0).getId());
                if (d != null) {
                    System.out.println(String.format(FORMAT_ID, d.getId()));
                    System.out.println(String.format(FORMAT_NOM_PRENOM, d.getFirst_name(), d.getLast_name()));
                }
            }

        } catch (Exception e) {
            System.err.println(ERREUR + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n" + SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, FIN_TESTS));
        System.out.println(SEPARATEUR);
    }
}
