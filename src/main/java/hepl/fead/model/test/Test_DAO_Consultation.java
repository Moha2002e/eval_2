package hepl.fead.model.test;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.entity.Consultation;
import hepl.fead.model.viewmodel.ConsultationSearchVM;

import java.util.ArrayList;

import static hepl.fead.model.test.Const_Test.*;

public class Test_DAO_Consultation {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_DAO_CONSULTATION));
        System.out.println(SEPARATEUR + "\n");

        ConsultationDAO consultationDAO = new ConsultationDAO();

        try {
            System.out.println(String.format(FORMAT_TEST, 1, LIRE_TOUS));
            ArrayList<Consultation> consultations = consultationDAO.load();
            System.out.println(String.format(NB_ENREGISTREMENTS, consultations.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 2, AJOUTER));
            Consultation nouvelle = new Consultation();
            nouvelle.setPatient_id(1);
            nouvelle.setDoctor_id(1);
            nouvelle.setDate("2025-01-15");
            nouvelle.setHour("14:30");
            nouvelle.setReason("Consultation de controle");
            consultationDAO.save(nouvelle);
            System.out.println(String.format(ENREGISTREMENT_AJOUTE, nouvelle.getId()));

            System.out.println("\n" + String.format(FORMAT_TEST, 3, MODIFIER));
            nouvelle.setReason("Raison modifiee - TEST");
            nouvelle.setHour("15:45");
            consultationDAO.save(nouvelle);
            Consultation verif = consultationDAO.getConsultationById(nouvelle.getId());
            if (verif != null && verif.getReason().contains("TEST")) {
                System.out.println(ENREGISTREMENT_MODIFIE);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 4, RECHERCHER));
            ConsultationSearchVM search = new ConsultationSearchVM();
            search.setDateFrom("2025-01-01");
            search.setDateTo("2025-12-31");
            ArrayList<Consultation> resultats = consultationDAO.load(search);
            System.out.println(String.format(RESULTATS_RECHERCHE, resultats.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 5, SUPPRIMER));
            Consultation temp = new Consultation();
            temp.setPatient_id(1);
            temp.setDoctor_id(1);
            temp.setDate("2025-12-31");
            temp.setHour("23:59");
            temp.setReason("Temporaire");
            consultationDAO.save(temp);
            Integer idSuppr = temp.getId();
            consultationDAO.delete(idSuppr);
            if (consultationDAO.getConsultationById(idSuppr) == null) {
                System.out.println(ENREGISTREMENT_SUPPRIME);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 6, LIRE_PAR_ID));
            consultations = consultationDAO.load();
            if (!consultations.isEmpty()) {
                Consultation c = consultationDAO.getConsultationById(consultations.get(0).getId());
                if (c != null) {
                    System.out.println(String.format(FORMAT_ID, c.getId()));
                    System.out.println(String.format(FORMAT_DETAIL, "Date", c.getDate()));
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
