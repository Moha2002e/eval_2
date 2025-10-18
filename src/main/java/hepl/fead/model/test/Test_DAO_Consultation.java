package hepl.fead.model.test;

import hepl.fead.model.dao.ConsultationDAO;
import hepl.fead.model.entity.Consultation;
import hepl.fead.model.viewmodel.ConsultationSearchVM;

import java.util.ArrayList;

public class Test_DAO_Consultation {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST DU DAO CONSULTATION");
        System.out.println("========================================\n");

        ConsultationDAO consultationDAO = new ConsultationDAO();

        // TEST 1: LIRE TOUTES LES CONSULTATIONS
        System.out.println("📖 TEST 1: Lire toutes les consultations");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Consultation> consultations = consultationDAO.load();
            System.out.println("✅ Nombre de consultations trouvées: " + consultations.size());
            
            if (!consultations.isEmpty()) {
                System.out.println("\nPremières consultations:");
                int count = 0;
                for (Consultation c : consultations) {
                    System.out.println("  - ID: " + c.getId() + 
                                     ", Patient ID: " + c.getPatient_id() + 
                                     ", Doctor ID: " + c.getDoctor_id() + 
                                     ", Date: " + c.getDate() + 
                                     ", Heure: " + c.getHour() +
                                     ", Raison: " + c.getReason());
                    if (++count >= 3) break; // Afficher seulement les 3 premières
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 2: AJOUTER UNE NOUVELLE CONSULTATION
        System.out.println("\n➕ TEST 2: Ajouter une nouvelle consultation");
        System.out.println("------------------------------------------");
        try {
            Consultation nouvelleConsultation = new Consultation();
            nouvelleConsultation.setPatient_id(1);  // Assurez-vous que ce patient existe
            nouvelleConsultation.setDoctor_id(1);   // Assurez-vous que ce docteur existe
            nouvelleConsultation.setDate("2025-01-15");
            nouvelleConsultation.setHour("14:30");
            nouvelleConsultation.setReason("Consultation de contrôle");

            consultationDAO.save(nouvelleConsultation);
            System.out.println("✅ Consultation ajoutée avec ID: " + nouvelleConsultation.getId());
            
            // Vérifier l'ajout
            Consultation verif = consultationDAO.getConsultationById(nouvelleConsultation.getId());
            if (verif != null) {
                System.out.println("✅ Vérification: Consultation bien enregistrée");
                System.out.println("   Détails: Date=" + verif.getDate() + ", Raison=" + verif.getReason());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 3: MODIFIER UNE CONSULTATION EXISTANTE
        System.out.println("\n✏️ TEST 3: Modifier une consultation");
        System.out.println("------------------------------------------");
        try {
            // Récupérer une consultation existante
            ArrayList<Consultation> consultations = consultationDAO.load();
            if (!consultations.isEmpty()) {
                Consultation consultationAModifier = consultations.get(0);
                Integer idOriginal = consultationAModifier.getId();
                String raisonOriginale = consultationAModifier.getReason();
                
                System.out.println("Consultation à modifier - ID: " + idOriginal);
                System.out.println("Raison originale: " + raisonOriginale);
                
                // Modifier la consultation
                consultationAModifier.setReason("Raison modifiée - TEST");
                consultationAModifier.setHour("15:45");
                
                consultationDAO.save(consultationAModifier);
                
                // Vérifier la modification
                Consultation verifModif = consultationDAO.getConsultationById(idOriginal);
                if (verifModif != null && "Raison modifiée - TEST".equals(verifModif.getReason())) {
                    System.out.println("✅ Consultation modifiée avec succès");
                    System.out.println("   Nouvelle raison: " + verifModif.getReason());
                    System.out.println("   Nouvelle heure: " + verifModif.getHour());
                } else {
                    System.out.println("⚠️  Modification non confirmée");
                }
            } else {
                System.out.println("⚠️  Aucune consultation disponible pour la modification");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 4: RECHERCHER AVEC CRITÈRES
        System.out.println("\n🔍 TEST 4: Rechercher avec critères");
        System.out.println("------------------------------------------");
        try {
            ConsultationSearchVM searchVM = new ConsultationSearchVM();
            searchVM.setDateFrom("2025-01-01");
            searchVM.setDateTo("2025-12-31");
            
            ArrayList<Consultation> resultats = consultationDAO.load(searchVM);
            System.out.println("✅ Consultations trouvées en 2025: " + resultats.size());
            
            // Recherche par raison
            ConsultationSearchVM searchRaison = new ConsultationSearchVM();
            searchRaison.setReason("contrôle");
            ArrayList<Consultation> resultatsRaison = consultationDAO.load(searchRaison);
            System.out.println("✅ Consultations contenant 'contrôle': " + resultatsRaison.size());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 5: SUPPRIMER UNE CONSULTATION
        System.out.println("\n🗑️  TEST 5: Supprimer une consultation");
        System.out.println("------------------------------------------");
        try {
            // Créer une consultation temporaire pour la supprimer
            Consultation consultationTemp = new Consultation();
            consultationTemp.setPatient_id(1);
            consultationTemp.setDoctor_id(1);
            consultationTemp.setDate("2025-12-31");
            consultationTemp.setHour("23:59");
            consultationTemp.setReason("Consultation à supprimer - TEST");
            
            consultationDAO.save(consultationTemp);
            Integer idASupprimer = consultationTemp.getId();
            System.out.println("Consultation créée avec ID: " + idASupprimer);
            
            // Supprimer la consultation
            consultationDAO.delete(idASupprimer);
            System.out.println("✅ Consultation supprimée");
            
            // Vérifier la suppression
            Consultation verifSuppression = consultationDAO.getConsultationById(idASupprimer);
            if (verifSuppression == null) {
                System.out.println("✅ Vérification: Consultation bien supprimée de la base");
            } else {
                System.out.println("⚠️  Attention: La consultation existe encore");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 6: LIRE UNE CONSULTATION PAR ID
        System.out.println("\n🔎 TEST 6: Lire une consultation spécifique par ID");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Consultation> consultations = consultationDAO.load();
            if (!consultations.isEmpty()) {
                Integer idTest = consultations.get(0).getId();
                Consultation consultation = consultationDAO.getConsultationById(idTest);
                
                if (consultation != null) {
                    System.out.println("✅ Consultation trouvée:");
                    System.out.println("   ID: " + consultation.getId());
                    System.out.println("   Patient ID: " + consultation.getPatient_id());
                    System.out.println("   Doctor ID: " + consultation.getDoctor_id());
                    System.out.println("   Date: " + consultation.getDate());
                    System.out.println("   Heure: " + consultation.getHour());
                    System.out.println("   Raison: " + consultation.getReason());
                } else {
                    System.out.println("⚠️  Consultation non trouvée");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture par ID: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================");
        System.out.println("  FIN DES TESTS");
        System.out.println("========================================");
    }
}
