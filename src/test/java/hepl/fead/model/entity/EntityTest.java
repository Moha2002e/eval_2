package hepl.fead.model.entity;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'interface Entity")
public class EntityTest {

    @Test
    @DisplayName("Test - Patient nouveau")
    void testPatientNew() {
        Patient patient = new Patient();
        patient.setFirst_name("Jean");
        patient.setLast_name("Dupont");
        
        assertTrue(patient.isNew(), "Un patient sans ID doit etre nouveau");
        assertFalse(patient.isPersisted(), "Un patient sans ID ne doit pas etre persisté");
    }

    @Test
    @DisplayName("Test - Patient persisté")
    void testPatientPersisted() {
        Patient patient = new Patient();
        patient.setId(42);
        patient.setFirst_name("Marie");
        patient.setLast_name("Martin");
        
        assertFalse(patient.isNew(), "Un patient avec ID ne doit pas etre nouveau");
        assertTrue(patient.isPersisted(), "Un patient avec ID doit etre persisté");
    }

    @Test
    @DisplayName("Test - Doctor nouveau")
    void testDoctorNew() {
        Doctor doctor = new Doctor();
        doctor.setFirst_name("Dr. Pierre");
        doctor.setLast_name("Dubois");
        doctor.setPassword("password123");
        
        assertTrue(doctor.isNew(), "Un docteur sans ID doit etre nouveau");
        assertFalse(doctor.isPersisted(), "Un docteur sans ID ne doit pas etre persisté");
    }

    @Test
    @DisplayName("Test - Doctor persisté")
    void testDoctorPersisted() {
        Doctor doctor = new Doctor(10, 1, "Dr. Sophie", "Bernard");
        doctor.setPassword("password123");
        
        assertFalse(doctor.isNew(), "Un docteur avec ID ne doit pas etre nouveau");
        assertTrue(doctor.isPersisted(), "Un docteur avec ID doit etre persisté");
    }

    @Test
    @DisplayName("Test - Speciality nouveau")
    void testSpecialityNew() {
        Speciality speciality = new Speciality();
        speciality.setName("Cardiologie");
        
        assertTrue(speciality.isNew(), "Une spécialité sans ID doit etre nouvelle");
        assertFalse(speciality.isPersisted(), "Une spécialité sans ID ne doit pas etre persistée");
    }

    @Test
    @DisplayName("Test - Speciality persistée")
    void testSpecialityPersisted() {
        Speciality speciality = new Speciality(5, "Neurologie");
        
        assertFalse(speciality.isNew(), "Une spécialité avec ID ne doit pas etre nouvelle");
        assertTrue(speciality.isPersisted(), "Une spécialité avec ID doit etre persistée");
    }

    @Test
    @DisplayName("Test - Consultation nouveau")
    void testConsultationNew() {
        Consultation consultation = new Consultation();
        consultation.setPatient_id(1);
        consultation.setDoctor_id(1);
        consultation.setDate("2025-01-15");
        
        assertTrue(consultation.isNew(), "Une consultation sans ID doit etre nouvelle");
        assertFalse(consultation.isPersisted(), "Une consultation sans ID ne doit pas etre persistée");
    }

    @Test
    @DisplayName("Test - Consultation persistée")
    void testConsultationPersisted() {
        Consultation consultation = new Consultation();
        consultation.setId(99);
        consultation.setPatient_id(1);
        consultation.setDoctor_id(1);
        consultation.setDate("2025-01-15");
        
        assertFalse(consultation.isNew(), "Une consultation avec ID ne doit pas etre nouvelle");
        assertTrue(consultation.isPersisted(), "Une consultation avec ID doit etre persistée");
    }

    @Test
    @DisplayName("Test - Polymorphisme avec Entity")
    void testEntityPolymorphism() {
        Entity[] entities = {
            new Patient(),
            new Doctor(),
            new Speciality(),
            new Consultation()
        };
        
        int nouvelles = 0;
        int persistees = 0;
        
        for (Entity entity : entities) {
            if (entity.isNew()) {
                nouvelles++;
            } else {
                persistees++;
            }
        }
        
        assertEquals(4, nouvelles, "Toutes les entités sans ID doivent etre nouvelles");
        assertEquals(0, persistees, "Aucune entité ne doit etre persistée sans ID");
    }
}

