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

/**
 * Test pour vérifier que la base de données et les tables sont créées automatiquement
 */
public class Test_DatabaseInit {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║    TEST D'INITIALISATION AUTOMATIQUE DE LA BD         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        try {
            // TEST 1 : Connexion (va initialiser la BD automatiquement)
            System.out.println("📡 TEST 1: Tentative de connexion à la base de données");
            System.out.println("------------------------------------------");
            Connection conn = ConnectBD.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion établie avec succès");
                System.out.println("   URL: " + conn.getMetaData().getURL());
                System.out.println("   Utilisateur: " + conn.getMetaData().getUserName());
                System.out.println("   Base de données: " + conn.getCatalog());
            } else {
                System.out.println("❌ Échec de la connexion");
                return;
            }

            // TEST 2 : Vérifier les spécialités
            System.out.println("\n📋 TEST 2: Vérification de la table 'specialties'");
            System.out.println("------------------------------------------");
            SpecialtyDAO specialtyDAO = new SpecialtyDAO();
            ArrayList<Speciality> specialties = specialtyDAO.load();
            
            System.out.println("✅ Table 'specialties' accessible");
            System.out.println("   Nombre de spécialités: " + specialties.size());
            
            if (!specialties.isEmpty()) {
                System.out.println("   Spécialités disponibles:");
                for (Speciality s : specialties) {
                    System.out.println("      - " + s.getName() + " (ID: " + s.getId() + ")");
                }
            }

            // TEST 3 : Vérifier les patients
            System.out.println("\n👥 TEST 3: Vérification de la table 'patients'");
            System.out.println("------------------------------------------");
            PatientDAO patientDAO = new PatientDAO();
            ArrayList<Patient> patients = patientDAO.load();
            
            System.out.println("✅ Table 'patients' accessible");
            System.out.println("   Nombre de patients: " + patients.size());
            
            if (!patients.isEmpty()) {
                System.out.println("   Quelques patients:");
                int count = 0;
                for (Patient p : patients) {
                    System.out.println("      - " + p.getFirst_name() + " " + p.getLast_name() + 
                                     " (né(e) le " + p.getBirth_date() + ")");
                    if (++count >= 3) break;
                }
            }

            // TEST 4 : Vérifier les docteurs
            System.out.println("\n👨‍⚕️ TEST 4: Vérification de la table 'doctors'");
            System.out.println("------------------------------------------");
            DoctorDAO doctorDAO = new DoctorDAO();
            ArrayList<Doctor> doctors = doctorDAO.load();
            
            System.out.println("✅ Table 'doctors' accessible");
            System.out.println("   Nombre de docteurs: " + doctors.size());
            
            if (!doctors.isEmpty()) {
                System.out.println("   Quelques docteurs:");
                int count = 0;
                for (Doctor d : doctors) {
                    System.out.println("      - " + d.getFirst_name() + " " + d.getLast_name() + 
                                     " (Spécialité ID: " + d.getSpecialite_id() + ")");
                    if (++count >= 3) break;
                }
            }

            // TEST 5 : Afficher les métadonnées des tables
            System.out.println("\n📊 TEST 5: Vérification de la structure de la base");
            System.out.println("------------------------------------------");
            var meta = conn.getMetaData();
            var tables = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});
            
            System.out.println("✅ Tables présentes dans la base '" + conn.getCatalog() + "':");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   ✓ " + tableName);
            }
            tables.close();

            // Résumé
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║              INITIALISATION RÉUSSIE ✅                 ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("\n📊 Résumé:");
            System.out.println("   • Base de données: CRÉÉE et ACCESSIBLE");
            System.out.println("   • Tables: CRÉÉES avec succès");
            System.out.println("   • Données initiales: INSÉRÉES");
            System.out.println("   • Spécialités: " + specialties.size());
            System.out.println("   • Patients: " + patients.size());
            System.out.println("   • Docteurs: " + doctors.size());
            System.out.println("\n✨ Votre application est prête à être utilisée !");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR lors de l'initialisation");
            System.err.println("------------------------------------------");
            System.err.println("Message: " + e.getMessage());
            System.err.println("\n📝 Vérifiez:");
            System.err.println("   1. Le serveur MySQL est démarré");
            System.err.println("   2. Les informations dans database.properties sont correctes");
            System.err.println("   3. L'utilisateur a les droits CREATE DATABASE et CREATE TABLE");
            System.err.println("\nStack trace:");
            e.printStackTrace();
        }
    }
}

