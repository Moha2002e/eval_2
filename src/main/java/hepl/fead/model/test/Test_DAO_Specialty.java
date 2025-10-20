package hepl.fead.model.test;

import hepl.fead.model.dao.SpecialtyDAO;
import hepl.fead.model.entity.Speciality;
import hepl.fead.model.viewmodel.SpecialitySearchVM;

import java.util.ArrayList;

import static hepl.fead.model.test.Const_Test.*;

public class Test_DAO_Specialty {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_DAO_SPECIALTY));
        System.out.println(SEPARATEUR + "\n");

        SpecialtyDAO specialtyDAO = new SpecialtyDAO();

        try {
            System.out.println(String.format(FORMAT_TEST, 1, LIRE_TOUS));
            ArrayList<Speciality> specialties = specialtyDAO.load();
            System.out.println(String.format(NB_ENREGISTREMENTS, specialties.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 2, AJOUTER));
            try {
                Speciality nouvelle = new Speciality();
                nouvelle.setName(SPECIALTY_NOM);
                specialtyDAO.save(nouvelle);
                System.out.println(String.format(ENREGISTREMENT_AJOUTE, nouvelle.getId()));
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    System.out.println("Specialite deja existante - OK");
                } else {
                    throw e;
                }
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 3, MODIFIER));
            specialties = specialtyDAO.load();
            if (!specialties.isEmpty()) {
                Speciality aModifier = specialties.get(0);
                String nomOriginal = aModifier.getName();
                aModifier.setName(SPECIALTY_NOM_MODIF);
                specialtyDAO.save(aModifier);
                Speciality verif = specialtyDAO.getSpeciality(aModifier.getId());
                if (verif != null && SPECIALTY_NOM_MODIF.equals(verif.getName())) {
                    System.out.println(ENREGISTREMENT_MODIFIE);
                }
                // Restaurer le nom original
                aModifier.setName(nomOriginal);
                specialtyDAO.save(aModifier);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 4, RECHERCHER));
            SpecialitySearchVM search = new SpecialitySearchVM();
            search.setName(RECHERCHE_CARDIO);
            ArrayList<Speciality> resultats = specialtyDAO.load(search);
            System.out.println(String.format(RESULTATS_RECHERCHE, resultats.size()));

            System.out.println("\n" + String.format(FORMAT_TEST, 5, SUPPRIMER));
            Speciality temp = new Speciality();
            temp.setName(SPECIALTY_TEMP);
            specialtyDAO.save(temp);
            Integer idSuppr = temp.getId();
            specialtyDAO.delete(idSuppr);
            if (specialtyDAO.getSpeciality(idSuppr) == null) {
                System.out.println(ENREGISTREMENT_SUPPRIME);
            }

            System.out.println("\n" + String.format(FORMAT_TEST, 6, LIRE_PAR_ID));
            specialties = specialtyDAO.load();
            if (!specialties.isEmpty()) {
                Speciality s = specialtyDAO.getSpeciality(specialties.get(0).getId());
                if (s != null) {
                    System.out.println(String.format(FORMAT_ID, s.getId()));
                    System.out.println(String.format(FORMAT_DETAIL, LABEL_NOM, s.getName()));
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
