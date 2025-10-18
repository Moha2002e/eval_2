package hepl.fead.model.test;

public class Test_ALL_DAO {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     TESTS COMPLETS DE TOUS LES DAO                     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // TEST 1: SPECIALTY DAO (en premier car les docteurs dépendent des spécialités)
        System.out.println("\n\n");
        System.out.println("█████████████████████████████████████████████████████████");
        System.out.println("█                 1. TEST SPECIALTY DAO                 █");
        System.out.println("█████████████████████████████████████████████████████████");
        try {
            Test_DAO_Specialty.main(new String[]{});
        } catch (Exception e) {
            System.err.println("❌ Erreur globale dans Test_DAO_Specialty: " + e.getMessage());
        }

        // Pause entre les tests
        pause();

        // TEST 2: PATIENT DAO
        System.out.println("\n\n");
        System.out.println("█████████████████████████████████████████████████████████");
        System.out.println("█                  2. TEST PATIENT DAO                  █");
        System.out.println("█████████████████████████████████████████████████████████");
        try {
            Test_DAO_Patient.main(new String[]{});
        } catch (Exception e) {
            System.err.println("❌ Erreur globale dans Test_DAO_Patient: " + e.getMessage());
        }

        // Pause entre les tests
        pause();

        // TEST 3: DOCTOR DAO (après specialty car dépendance)
        System.out.println("\n\n");
        System.out.println("█████████████████████████████████████████████████████████");
        System.out.println("█                  3. TEST DOCTOR DAO                   █");
        System.out.println("█████████████████████████████████████████████████████████");
        try {
            Test_DAO_Doctor.main(new String[]{});
        } catch (Exception e) {
            System.err.println("❌ Erreur globale dans Test_DAO_Doctor: " + e.getMessage());
        }

        // Pause entre les tests
        pause();

        // TEST 4: CONSULTATION DAO (en dernier car dépend de patients et docteurs)
        System.out.println("\n\n");
        System.out.println("█████████████████████████████████████████████████████████");
        System.out.println("█               4. TEST CONSULTATION DAO                █");
        System.out.println("█████████████████████████████████████████████████████████");
        try {
            Test_DAO_Consultation.main(new String[]{});
        } catch (Exception e) {
            System.err.println("❌ Erreur globale dans Test_DAO_Consultation: " + e.getMessage());
        }

        // Résumé final
        System.out.println("\n\n");
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║              TOUS LES TESTS SONT TERMINÉS              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n✅ Tests exécutés:");
        System.out.println("   1. SpecialtyDAO - Gestion des spécialités médicales");
        System.out.println("   2. PatientDAO   - Gestion des patients");
        System.out.println("   3. DoctorDAO    - Gestion des docteurs");
        System.out.println("   4. ConsultationDAO - Gestion des consultations");
        System.out.println("\n📊 Vérifiez les résultats ci-dessus pour chaque test.");
    }

    private static void pause() {
        try {
            System.out.println("\n⏸️  Pause de 2 secondes...\n");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

