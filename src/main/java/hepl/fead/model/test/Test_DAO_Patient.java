package hepl.fead.model.test;

import hepl.fead.model.dao.PatientDAO;
import hepl.fead.model.entity.Patient;
import hepl.fead.model.viewmodel.PatientSearchVM;

import java.util.ArrayList;

public class Test_DAO_Patient {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST DU DAO PATIENT");
        System.out.println("========================================\n");

        PatientDAO patientDAO = new PatientDAO();

        // TEST 1: LIRE TOUS LES PATIENTS
        System.out.println("📖 TEST 1: Lire tous les patients");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Patient> patients = patientDAO.load();
            System.out.println("✅ Nombre de patients trouvés: " + patients.size());
            
            if (!patients.isEmpty()) {
                System.out.println("\nPremiers patients:");
                int count = 0;
                for (Patient p : patients) {
                    System.out.println("  - ID: " + p.getId() + 
                                     ", Nom: " + p.getLast_name() + 
                                     ", Prénom: " + p.getFirst_name() + 
                                     ", Date de naissance: " + p.getBirth_date());
                    if (++count >= 5) break; // Afficher seulement les 5 premiers
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 2: AJOUTER UN NOUVEAU PATIENT
        System.out.println("\n➕ TEST 2: Ajouter un nouveau patient");
        System.out.println("------------------------------------------");
        try {
            Patient nouveauPatient = new Patient();
            nouveauPatient.setFirst_name("Jean");
            nouveauPatient.setLast_name("Dupont");
            nouveauPatient.setBirth_date("1985-05-15");

            patientDAO.save(nouveauPatient);
            System.out.println("✅ Patient ajouté avec ID: " + nouveauPatient.getId());
            
            // Vérifier l'ajout
            Patient verif = patientDAO.getPatient(nouveauPatient.getId());
            if (verif != null) {
                System.out.println("✅ Vérification: Patient bien enregistré");
                System.out.println("   Détails: " + verif.getFirst_name() + " " + verif.getLast_name() + 
                                 ", né(e) le " + verif.getBirth_date());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 3: MODIFIER UN PATIENT EXISTANT
        System.out.println("\n✏️ TEST 3: Modifier un patient");
        System.out.println("------------------------------------------");
        try {
            // Récupérer un patient existant
            ArrayList<Patient> patients = patientDAO.load();
            if (!patients.isEmpty()) {
                Patient patientAModifier = patients.get(0);
                Integer idOriginal = patientAModifier.getId();
                String nomOriginal = patientAModifier.getLast_name();
                
                System.out.println("Patient à modifier - ID: " + idOriginal);
                System.out.println("Nom original: " + nomOriginal);
                
                // Modifier le patient
                patientAModifier.setFirst_name("Marie");
                patientAModifier.setLast_name("Martin-TEST");
                patientAModifier.setBirth_date("1990-12-25");
                
                patientDAO.save(patientAModifier);
                
                // Vérifier la modification
                Patient verifModif = patientDAO.getPatient(idOriginal);
                if (verifModif != null && "Martin-TEST".equals(verifModif.getLast_name())) {
                    System.out.println("✅ Patient modifié avec succès");
                    System.out.println("   Nouveau nom: " + verifModif.getFirst_name() + " " + verifModif.getLast_name());
                    System.out.println("   Nouvelle date de naissance: " + verifModif.getBirth_date());
                } else {
                    System.out.println("⚠️  Modification non confirmée");
                }
            } else {
                System.out.println("⚠️  Aucun patient disponible pour la modification");
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
            PatientSearchVM searchNom = new PatientSearchVM();
            searchNom.setLastName("Martin");
            ArrayList<Patient> resultatsNom = patientDAO.load(searchNom);
            System.out.println("✅ Patients avec nom contenant 'Martin': " + resultatsNom.size());
            
            // Recherche par prénom
            PatientSearchVM searchPrenom = new PatientSearchVM();
            searchPrenom.setFirstName("Marie");
            ArrayList<Patient> resultatsPrenom = patientDAO.load(searchPrenom);
            System.out.println("✅ Patients avec prénom contenant 'Marie': " + resultatsPrenom.size());
            
            // Recherche combinée
            PatientSearchVM searchCombi = new PatientSearchVM();
            searchCombi.setFirstName("Marie");
            searchCombi.setLastName("Martin");
            ArrayList<Patient> resultatsCombi = patientDAO.load(searchCombi);
            System.out.println("✅ Patients 'Marie Martin': " + resultatsCombi.size());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 5: SUPPRIMER UN PATIENT
        System.out.println("\n🗑️  TEST 5: Supprimer un patient");
        System.out.println("------------------------------------------");
        try {
            // Créer un patient temporaire pour le supprimer
            Patient patientTemp = new Patient();
            patientTemp.setFirst_name("Temporaire");
            patientTemp.setLast_name("ASupprimer");
            patientTemp.setBirth_date("2000-01-01");
            
            patientDAO.save(patientTemp);
            Integer idASupprimer = patientTemp.getId();
            System.out.println("Patient créé avec ID: " + idASupprimer);
            
            // Supprimer le patient
            patientDAO.delete(idASupprimer);
            System.out.println("✅ Patient supprimé");
            
            // Vérifier la suppression
            Patient verifSuppression = patientDAO.getPatient(idASupprimer);
            if (verifSuppression == null) {
                System.out.println("✅ Vérification: Patient bien supprimé de la base");
            } else {
                System.out.println("⚠️  Attention: Le patient existe encore");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }

        // TEST 6: LIRE UN PATIENT PAR ID
        System.out.println("\n🔎 TEST 6: Lire un patient spécifique par ID");
        System.out.println("------------------------------------------");
        try {
            ArrayList<Patient> patients = patientDAO.load();
            if (!patients.isEmpty()) {
                Integer idTest = patients.get(0).getId();
                Patient patient = patientDAO.getPatient(idTest);
                
                if (patient != null) {
                    System.out.println("✅ Patient trouvé:");
                    System.out.println("   ID: " + patient.getId());
                    System.out.println("   Nom: " + patient.getLast_name());
                    System.out.println("   Prénom: " + patient.getFirst_name());
                    System.out.println("   Date de naissance: " + patient.getBirth_date());
                } else {
                    System.out.println("⚠️  Patient non trouvé");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture par ID: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================");
        System.out.println("  FIN DES TESTS PATIENT");
        System.out.println("========================================");
    }
}

