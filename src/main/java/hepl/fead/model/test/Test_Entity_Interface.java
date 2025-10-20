package hepl.fead.model.test;

import hepl.fead.model.entity.Entity;
import hepl.fead.model.entity.Patient;
import hepl.fead.model.entity.Doctor;
import hepl.fead.model.entity.Speciality;
import hepl.fead.model.entity.Consultation;

import static hepl.fead.model.test.Const_Test.*;

public class Test_Entity_Interface {
    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_ENTITY_INTERFACE));
        System.out.println(SEPARATEUR + "\n");

        Patient patient = new Patient();
        patient.setFirst_name(PATIENT_PRENOM);
        patient.setLast_name(PATIENT_NOM);
        
        System.out.println(String.format(FORMAT_TEST, 1, ENTITY_NOUVEAU));
        System.out.println(SEPARATEUR_COURT);
        System.out.println(String.format(FORMAT_NOM_PRENOM, patient.getFirst_name(), patient.getLast_name()));
        System.out.println(String.format(FORMAT_DETAIL, ENTITY_EST_NOUVEAU, patient.isNew()));
        System.out.println(String.format(FORMAT_DETAIL, ENTITY_EST_PERSISTE, patient.isPersisted()));

        patient.setId(42);
        
        System.out.println("\n" + String.format(FORMAT_TEST, 2, ENTITY_PERSISTE));
        System.out.println(SEPARATEUR_COURT);
        System.out.println(String.format(FORMAT_ID, patient.getId()));
        System.out.println(String.format(FORMAT_DETAIL, ENTITY_EST_NOUVEAU, patient.isNew()));
        System.out.println(String.format(FORMAT_DETAIL, ENTITY_EST_PERSISTE, patient.isPersisted()));

        System.out.println("\n" + String.format(FORMAT_TEST, 3, "Methode generique"));
        System.out.println(SEPARATEUR_COURT);
        
        Doctor doctor = new Doctor(10, 1, DOCTOR_NOM_2, DOCTOR_PRENOM_2);
        Speciality speciality = new Speciality(5, SPECIALTY_NOM);
        Consultation consultation = new Consultation();
        consultation.setId(99);
        
        afficherInfoEntity(patient);
        afficherInfoEntity(doctor);
        afficherInfoEntity(speciality);
        afficherInfoEntity(consultation);

        System.out.println("\n" + SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, FIN_TESTS));
        System.out.println(SEPARATEUR);
    }

    private static void afficherInfoEntity(Entity entity) {
        String type = entity.getClass().getSimpleName();
        String statut = entity.isPersisted() ? ENTITY_PERSISTE : ENTITY_NOUVEAU;
        System.out.println(String.format("  %s [%s] - %s", type, String.format(FORMAT_ID, entity.getId()), statut));
    }
}
