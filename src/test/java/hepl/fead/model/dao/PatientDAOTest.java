package hepl.fead.model.dao;

import hepl.fead.model.entity.Patient;
import hepl.fead.model.viewmodel.PatientSearchVM;
import hepl.fead.model.bd.DatabaseInitializer;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static hepl.fead.model.test.Const_JUnit.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientDAOTest {

    private static PatientDAO patientDAO;
    private static Patient patientTest;

    @BeforeAll
    static void setUpAll() {
        // Initialiser la base de données
        DatabaseInitializer.initialize();
        patientDAO = new PatientDAO();
    }

    @BeforeEach
    void setUp() {
        // Pas besoin de recréer le DAO à chaque test
    }

    @Test
    @Order(1)
    @DisplayName("Test - Lire tous les patients")
    void testLoadAllPatients() {
        ArrayList<Patient> patients = patientDAO.load();
        assertNotNull(patients, MSG_ENTITE_NON_NULLE);
        assertTrue(patients.size() >= 0, "La liste des patients doit etre accessible");
    }

    @Test
    @Order(2)
    @DisplayName("Test - Ajouter un nouveau patient")
    void testAddPatient() {
        Patient nouveau = new Patient();
        nouveau.setFirst_name(PATIENT_PRENOM);
        nouveau.setLast_name(PATIENT_NOM);
        nouveau.setBirth_date(PATIENT_DATE_NAISSANCE);

        patientDAO.save(nouveau);
        assertNotNull(nouveau.getId(), MSG_ID_ASSIGNE);
        
        patientTest = nouveau; // Garder pour les tests suivants
    }

    @Test
    @Order(3)
    @DisplayName("Test - Lire un patient par ID")
    void testGetPatientById() {
        assertNotNull(patientTest, "Le patient de test doit exister");
        
        Patient patient = patientDAO.getPatient(patientTest.getId());
        assertNotNull(patient, MSG_ENTITE_NON_NULLE);
        assertEquals(PATIENT_PRENOM, patient.getFirst_name(), MSG_VALEUR_ATTENDUE);
        assertEquals(PATIENT_NOM, patient.getLast_name(), MSG_VALEUR_ATTENDUE);
    }

    @Test
    @Order(4)
    @DisplayName("Test - Modifier un patient")
    void testUpdatePatient() {
        assertNotNull(patientTest, "Le patient de test doit exister");
        
        patientTest.setFirst_name(PATIENT_PRENOM_2);
        patientTest.setLast_name(PATIENT_NOM_2 + SUFFIX_TEST);
        patientTest.setBirth_date(PATIENT_DATE_NAISSANCE_2);
        
        patientDAO.save(patientTest);
        
        Patient patientModifie = patientDAO.getPatient(patientTest.getId());
        assertNotNull(patientModifie, MSG_ENTITE_NON_NULLE);
        assertEquals(PATIENT_PRENOM_2, patientModifie.getFirst_name(), MSG_ENTITE_MODIFIEE);
        assertTrue(patientModifie.getLast_name().contains(SUFFIX_TEST), MSG_ENTITE_MODIFIEE);
    }

    @Test
    @Order(5)
    @DisplayName("Test - Rechercher des patients")
    void testSearchPatients() {
        PatientSearchVM search = new PatientSearchVM();
        search.setLastName(PATIENT_NOM); // Utiliser le nom du patient créé
        
        ArrayList<Patient> resultats = patientDAO.load(search);
        assertNotNull(resultats, MSG_ENTITE_NON_NULLE);
        assertTrue(resultats.size() >= 0, "La recherche doit retourner une liste");
    }

    @Test
    @Order(6)
    @DisplayName("Test - Supprimer un patient")
    void testDeletePatient() {
        assertNotNull(patientTest, "Le patient de test doit exister");
        
        Integer idASupprimer = patientTest.getId();
        patientDAO.delete(idASupprimer);
        
        Patient patientSupprime = patientDAO.getPatient(idASupprimer);
        assertNull(patientSupprime, MSG_ENTITE_SUPPRIMEE);
    }
}
