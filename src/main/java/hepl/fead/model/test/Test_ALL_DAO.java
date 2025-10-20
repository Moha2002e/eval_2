package hepl.fead.model.test;

import static hepl.fead.model.test.Const_Test.*;

public class Test_ALL_DAO {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, "TESTS COMPLETS DE TOUS LES DAO"));
        System.out.println(SEPARATEUR + "\n");

        System.out.println("\n" + SEPARATEUR);
        System.out.println("1. " + TEST_DAO_SPECIALTY);
        System.out.println(SEPARATEUR);
        try {
            Test_DAO_Specialty.main(new String[]{});
        } catch (Exception e) {
            System.err.println(ERREUR + e.getMessage());
        }

        pause();

        System.out.println("\n" + SEPARATEUR);
        System.out.println("2. " + TEST_DAO_PATIENT);
        System.out.println(SEPARATEUR);
        try {
            Test_DAO_Patient.main(new String[]{});
        } catch (Exception e) {
            System.err.println(ERREUR + e.getMessage());
        }

        pause();

        System.out.println("\n" + SEPARATEUR);
        System.out.println("3. " + TEST_DAO_DOCTOR);
        System.out.println(SEPARATEUR);
        try {
            Test_DAO_Doctor.main(new String[]{});
        } catch (Exception e) {
            System.err.println(ERREUR + e.getMessage());
        }

        pause();

        System.out.println("\n" + SEPARATEUR);
        System.out.println("4. " + TEST_DAO_CONSULTATION);
        System.out.println(SEPARATEUR);
        try {
            Test_DAO_Consultation.main(new String[]{});
        } catch (Exception e) {
            System.err.println(ERREUR + e.getMessage());
        }

        System.out.println("\n" + SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, "TOUS LES TESTS TERMINES"));
        System.out.println(SEPARATEUR);
    }

    private static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
