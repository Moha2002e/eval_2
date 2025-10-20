package hepl.fead.model.test;

import hepl.fead.model.dao.PatientDAO;
import hepl.fead.model.entity.Patient;
import hepl.fead.model.viewmodel.PatientSearchVM;

import java.util.ArrayList;

import static hepl.fead.model.test.Const_Test.*;

public class Test_DAO_Patient {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_DAO_PATIENT));
        System.out.println(SEPARATEUR + "\n");

        PatientDAO patientDAO = new PatientDAO();

        try {
            System.out.println(String.format(FORMAT_TEST, 1, LIRE_TOUS));
            ArrayList<Patient> patients = patientDAO.load();
            System.out.println(String.format(NB_ENREGISTREMENTS, patients.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 2, AJOUTER));
            Patient nouveau = new Patient();
            nouveau.setFirst_name(PATIENT_PRENOM);
            nouveau.setLast_name(PATIENT_NOM);
            nouveau.setBirth_date(PATIENT_DATE_NAISSANCE);
            patientDAO.save(nouveau);
            System.out.println(String.format(ENREGISTREMENT_AJOUTE, nouveau.getId()));

            System.out.println("\n" + String.format(FORMAT_TEST, 3, MODIFIER));
            nouveau.setFirst_name(PATIENT_PRENOM_2);
            nouveau.setLast_name(PATIENT_NOM_2 + SUFFIX_TEST);
            patientDAO.save(nouveau);
            Patient verif = patientDAO.getPatient(nouveau.getId());
            if (verif != null && verif.getLast_name().contains(SUFFIX_TEST)) {
                System.out.println(ENREGISTREMENT_MODIFIE);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 4, RECHERCHER));
            PatientSearchVM search = new PatientSearchVM();
            search.setLastName(RECHERCHE_NOM_MARTIN);
            ArrayList<Patient> resultats = patientDAO.load(search);
            System.out.println(String.format(RESULTATS_RECHERCHE, resultats.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 5, SUPPRIMER));
            Patient temp = new Patient();
            temp.setFirst_name(PATIENT_TEMP_PRENOM);
            temp.setLast_name(PATIENT_TEMP_NOM);
            temp.setBirth_date(PATIENT_TEMP_DATE);
            patientDAO.save(temp);
            Integer idSuppr = temp.getId();
            patientDAO.delete(idSuppr);
            if (patientDAO.getPatient(idSuppr) == null) {
                System.out.println(ENREGISTREMENT_SUPPRIME);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 6, LIRE_PAR_ID));
            patients = patientDAO.load();
            if (!patients.isEmpty()) {
                Patient p = patientDAO.getPatient(patients.get(0).getId());
                if (p != null) {
                    System.out.println(String.format(FORMAT_ID, p.getId()));
                    System.out.println(String.format(FORMAT_NOM_PRENOM, p.getFirst_name(), p.getLast_name()));
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
