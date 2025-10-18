package hepl.fead.model.test;

import hepl.fead.model.dao.SpecialtyDAO;
import hepl.fead.model.entity.Speciality;
import hepl.fead.model.viewmodel.SpecialitySearchVM;

import java.util.ArrayList;

public class Test_DAO_Specialty {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST DU DAO SPECIALTY");
        System.out.println("========================================\n");

        SpecialtyDAO specialtyDAO = new SpecialtyDAO();

        // TEST 1: LIRE TOUTES LES SPÉCIALITÉS
        System.out.println("📖 TEST 1: Lire toutes les spécialités");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Speciality> specialties = specialtyDAO.load();
            System.out.println("✅ Nombre de spécialités trouvées: " + specialties.size());
            
            if (!specialties.isEmpty()) {
                System.out.println("\nToutes les spécialités:");
                for (Speciality s : specialties) {
                    System.out.println("  - ID: " + s.getId() + ", Nom: " + s.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 2: AJOUTER UNE NOUVELLE SPÉCIALITÉ
        System.out.println("\n➕ TEST 2: Ajouter une nouvelle spécialité");
        System.out.println("------------------------------------------");
        try {
            Speciality nouvelleSpecialite = new Speciality();
            nouvelleSpecialite.setName("Neurologie");

            specialtyDAO.save(nouvelleSpecialite);
            System.out.println("✅ Spécialité ajoutée avec ID: " + nouvelleSpecialite.getId());
            
            // Vérifier l'ajout
            Speciality verif = specialtyDAO.getSpeciality(nouvelleSpecialite.getId());
            if (verif != null) {
                System.out.println("✅ Vérification: Spécialité bien enregistrée");
                System.out.println("   Détails: " + verif.getName());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 3: MODIFIER UNE SPÉCIALITÉ EXISTANTE
        System.out.println("\n✏️ TEST 3: Modifier une spécialité");
        System.out.println("------------------------------------------");
        try {
            // Récupérer une spécialité existante
            ArrayList<Speciality> specialties = specialtyDAO.load();
            if (!specialties.isEmpty()) {
                Speciality specialiteAModifier = specialties.get(0);
                Integer idOriginal = specialiteAModifier.getId();
                String nomOriginal = specialiteAModifier.getName();
                
                System.out.println("Spécialité à modifier - ID: " + idOriginal);
                System.out.println("Nom original: " + nomOriginal);
                
                // Modifier la spécialité
                specialiteAModifier.setName("Cardiologie-TEST");
                
                specialtyDAO.save(specialiteAModifier);
                
                // Vérifier la modification
                Speciality verifModif = specialtyDAO.getSpeciality(idOriginal);
                if (verifModif != null && "Cardiologie-TEST".equals(verifModif.getName())) {
                    System.out.println("✅ Spécialité modifiée avec succès");
                    System.out.println("   Nouveau nom: " + verifModif.getName());
                } else {
                    System.out.println("⚠️  Modification non confirmée");
                }
            } else {
                System.out.println("⚠️  Aucune spécialité disponible pour la modification");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 4: RECHERCHER AVEC CRITÈRES
        System.out.println("\n🔍 TEST 4: Rechercher avec critères");
        System.out.println("------------------------------------------");
        try {
            // Recherche par nom
            SpecialitySearchVM searchNom = new SpecialitySearchVM();
            searchNom.setName("Cardio");
            ArrayList<Speciality> resultatsNom = specialtyDAO.load(searchNom);
            System.out.println("✅ Spécialités contenant 'Cardio': " + resultatsNom.size());
            
            if (!resultatsNom.isEmpty()) {
                System.out.println("   Résultats:");
                for (Speciality s : resultatsNom) {
                    System.out.println("   - " + s.getName());
                }
            }
            
            // Recherche par nom exact
            SpecialitySearchVM searchExact = new SpecialitySearchVM();
            searchExact.setName("logie");
            ArrayList<Speciality> resultatsExact = specialtyDAO.load(searchExact);
            System.out.println("✅ Spécialités se terminant par 'logie': " + resultatsExact.size());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 5: SUPPRIMER UNE SPÉCIALITÉ
        System.out.println("\n🗑️  TEST 5: Supprimer une spécialité");
        System.out.println("------------------------------------------");
        try {
            // Créer une spécialité temporaire pour la supprimer
            Speciality specialiteTemp = new Speciality();
            specialiteTemp.setName("Spécialité-Temporaire-A-Supprimer");
            
            specialtyDAO.save(specialiteTemp);
            Integer idASupprimer = specialiteTemp.getId();
            System.out.println("Spécialité créée avec ID: " + idASupprimer);
            
            // Supprimer la spécialité
            specialtyDAO.delete(idASupprimer);
            System.out.println("✅ Spécialité supprimée");
            
            // Vérifier la suppression
            Speciality verifSuppression = specialtyDAO.getSpeciality(idASupprimer);
            if (verifSuppression == null) {
                System.out.println("✅ Vérification: Spécialité bien supprimée de la base");
            } else {
                System.out.println("⚠️  Attention: La spécialité existe encore");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            System.err.println("   Note: Impossible de supprimer si des docteurs utilisent cette spécialité (contrainte FK)");
        }

        // TEST 6: LIRE UNE SPÉCIALITÉ PAR ID
        System.out.println("\n🔎 TEST 6: Lire une spécialité spécifique par ID");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Speciality> specialties = specialtyDAO.load();
            if (!specialties.isEmpty()) {
                Integer idTest = specialties.get(0).getId();
                Speciality speciality = specialtyDAO.getSpeciality(idTest);
                
                if (speciality != null) {
                    System.out.println("✅ Spécialité trouvée:");
                    System.out.println("   ID: " + speciality.getId());
                    System.out.println("   Nom: " + speciality.getName());
                } else {
                    System.out.println("⚠️  Spécialité non trouvée");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture par ID: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================");
        System.out.println("  FIN DES TESTS SPECIALTY");
        System.out.println("========================================");
    }
}

