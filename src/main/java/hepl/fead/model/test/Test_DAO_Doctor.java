package hepl.fead.model.test;

import hepl.fead.model.dao.DoctorDAO;
import hepl.fead.model.entity.Doctor;
import hepl.fead.model.viewmodel.DoctorSearchVM;

import java.util.ArrayList;

public class Test_DAO_Doctor {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST DU DAO DOCTOR");
        System.out.println("========================================\n");

        DoctorDAO doctorDAO = new DoctorDAO();

        // TEST 1: LIRE TOUS LES DOCTEURS
        System.out.println("📖 TEST 1: Lire tous les docteurs");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Doctor> doctors = doctorDAO.load();
            System.out.println("✅ Nombre de docteurs trouvés: " + doctors.size());
            
            if (!doctors.isEmpty()) {
                System.out.println("\nPremiers docteurs:");
                int count = 0;
                for (Doctor d : doctors) {
                    System.out.println("  - ID: " + d.getId() + 
                                     ", Nom: " + d.getLast_name() + 
                                     ", Prénom: " + d.getFirst_name() + 
                                     ", Spécialité ID: " + d.getSpecialite_id());
                    if (++count >= 5) break; // Afficher seulement les 5 premiers
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 2: AJOUTER UN NOUVEAU DOCTEUR
        System.out.println("\n➕ TEST 2: Ajouter un nouveau docteur");
        System.out.println("------------------------------------------");
        try {
            Doctor nouveauDocteur = new Doctor();
            nouveauDocteur.setFirst_name("Dr. Sophie");
            nouveauDocteur.setLast_name("Bernard");
            nouveauDocteur.setSpecialite_id(1); // Assurez-vous que cette spécialité existe

            doctorDAO.save(nouveauDocteur);
            System.out.println("✅ Docteur ajouté avec ID: " + nouveauDocteur.getId());
            
            // Vérifier l'ajout
            Doctor verif = doctorDAO.getDoctor(nouveauDocteur.getId());
            if (verif != null) {
                System.out.println("✅ Vérification: Docteur bien enregistré");
                System.out.println("   Détails: " + verif.getFirst_name() + " " + verif.getLast_name() + 
                                 ", Spécialité ID: " + verif.getSpecialite_id());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 3: MODIFIER UN DOCTEUR EXISTANT
        System.out.println("\n✏️ TEST 3: Modifier un docteur");
        System.out.println("------------------------------------------");
        try {
            // Récupérer un docteur existant
            ArrayList<Doctor> doctors = doctorDAO.load();
            if (!doctors.isEmpty()) {
                Doctor docteurAModifier = doctors.get(0);
                Integer idOriginal = docteurAModifier.getId();
                String nomOriginal = docteurAModifier.getLast_name();
                
                System.out.println("Docteur à modifier - ID: " + idOriginal);
                System.out.println("Nom original: " + nomOriginal);
                
                // Modifier le docteur
                docteurAModifier.setFirst_name("Dr. Pierre");
                docteurAModifier.setLast_name("Dubois-TEST");
                docteurAModifier.setSpecialite_id(2); // Changer de spécialité
                
                doctorDAO.save(docteurAModifier);
                
                // Vérifier la modification
                Doctor verifModif = doctorDAO.getDoctor(idOriginal);
                if (verifModif != null && "Dubois-TEST".equals(verifModif.getLast_name())) {
                    System.out.println("✅ Docteur modifié avec succès");
                    System.out.println("   Nouveau nom: " + verifModif.getFirst_name() + " " + verifModif.getLast_name());
                    System.out.println("   Nouvelle spécialité ID: " + verifModif.getSpecialite_id());
                } else {
                    System.out.println("⚠️  Modification non confirmée");
                }
            } else {
                System.out.println("⚠️  Aucun docteur disponible pour la modification");
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
            DoctorSearchVM searchNom = new DoctorSearchVM();
            searchNom.setLastName("Dubois");
            ArrayList<Doctor> resultatsNom = doctorDAO.load(searchNom);
            System.out.println("✅ Docteurs avec nom contenant 'Dubois': " + resultatsNom.size());
            
            // Recherche par prénom
            DoctorSearchVM searchPrenom = new DoctorSearchVM();
            searchPrenom.setFirstName("Pierre");
            ArrayList<Doctor> resultatsPrenom = doctorDAO.load(searchPrenom);
            System.out.println("✅ Docteurs avec prénom contenant 'Pierre': " + resultatsPrenom.size());
            
            // Recherche combinée
            DoctorSearchVM searchCombi = new DoctorSearchVM();
            searchCombi.setFirstName("Pierre");
            searchCombi.setLastName("Dubois");
            ArrayList<Doctor> resultatsCombi = doctorDAO.load(searchCombi);
            System.out.println("✅ Docteurs 'Pierre Dubois': " + resultatsCombi.size());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 5: SUPPRIMER UN DOCTEUR
        System.out.println("\n🗑️  TEST 5: Supprimer un docteur");
        System.out.println("------------------------------------------");
        try {
            // Créer un docteur temporaire pour le supprimer
            Doctor docteurTemp = new Doctor();
            docteurTemp.setFirst_name("Dr. Temporaire");
            docteurTemp.setLast_name("ASupprimer");
            docteurTemp.setSpecialite_id(1);
            
            doctorDAO.save(docteurTemp);
            Integer idASupprimer = docteurTemp.getId();
            System.out.println("Docteur créé avec ID: " + idASupprimer);
            
            // Supprimer le docteur
            doctorDAO.delete(idASupprimer);
            System.out.println("✅ Docteur supprimé");
            
            // Vérifier la suppression
            Doctor verifSuppression = doctorDAO.getDoctor(idASupprimer);
            if (verifSuppression == null) {
                System.out.println("✅ Vérification: Docteur bien supprimé de la base");
            } else {
                System.out.println("⚠️  Attention: Le docteur existe encore");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 6: LIRE UN DOCTEUR PAR ID
        System.out.println("\n🔎 TEST 6: Lire un docteur spécifique par ID");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Doctor> doctors = doctorDAO.load();
            if (!doctors.isEmpty()) {
                Integer idTest = doctors.get(0).getId();
                Doctor doctor = doctorDAO.getDoctor(idTest);
                
                if (doctor != null) {
                    System.out.println("✅ Docteur trouvé:");
                    System.out.println("   ID: " + doctor.getId());
                    System.out.println("   Nom: " + doctor.getLast_name());
                    System.out.println("   Prénom: " + doctor.getFirst_name());
                    System.out.println("   Spécialité ID: " + doctor.getSpecialite_id());
                } else {
                    System.out.println("⚠️  Docteur non trouvé");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture par ID: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================");
        System.out.println("  FIN DES TESTS DOCTOR");
        System.out.println("========================================");
    }
}

