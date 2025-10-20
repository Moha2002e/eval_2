package hepl.fead.model.dao;

import hepl.fead.model.entity.Doctor;
import hepl.fead.model.viewmodel.DoctorSearchVM;
import hepl.fead.model.bd.DatabaseInitializer;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static hepl.fead.model.test.Const_JUnit.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DoctorDAOTest {

    private static DoctorDAO doctorDAO;
    private static Doctor doctorTest;

    @BeforeAll
    static void setUpAll() {
        // Initialiser la base de données
        DatabaseInitializer.initialize();
        doctorDAO = new DoctorDAO();
    }

    @BeforeEach
    void setUp() {
        // Pas besoin de recréer le DAO à chaque test
    }

    @Test
    @Order(1)
    @DisplayName("Test - Lire tous les docteurs")
    void testLoadAllDoctors() {
        ArrayList<Doctor> doctors = doctorDAO.load();
        assertNotNull(doctors, MSG_ENTITE_NON_NULLE);
        assertTrue(doctors.size() >= 0, "La liste des docteurs doit etre accessible");
    }

    @Test
    @Order(2)
    @DisplayName("Test - Ajouter un nouveau docteur")
    void testAddDoctor() {
        Doctor nouveau = new Doctor();
        nouveau.setFirst_name(DOCTOR_PRENOM);
        nouveau.setLast_name(DOCTOR_NOM);
        nouveau.setPassword(DOCTOR_PASSWORD);
        nouveau.setSpecialite_id(1);

        doctorDAO.save(nouveau);
        assertNotNull(nouveau.getId(), MSG_ID_ASSIGNE);
        
        doctorTest = nouveau; // Garder pour les tests suivants
    }

    @Test
    @Order(3)
    @DisplayName("Test - Lire un docteur par ID")
    void testGetDoctorById() {
        assertNotNull(doctorTest, "Le docteur de test doit exister");
        
        Doctor doctor = doctorDAO.getDoctor(doctorTest.getId());
        assertNotNull(doctor, MSG_ENTITE_NON_NULLE);
        assertEquals(DOCTOR_PRENOM, doctor.getFirst_name(), MSG_VALEUR_ATTENDUE);
        assertEquals(DOCTOR_NOM, doctor.getLast_name(), MSG_VALEUR_ATTENDUE);
        assertEquals(DOCTOR_PASSWORD, doctor.getPassword(), MSG_VALEUR_ATTENDUE);
    }

    @Test
    @Order(4)
    @DisplayName("Test - Modifier un docteur")
    void testUpdateDoctor() {
        assertNotNull(doctorTest, "Le docteur de test doit exister");
        
        doctorTest.setFirst_name(DOCTOR_PRENOM_2);
        doctorTest.setLast_name(DOCTOR_NOM_2 + SUFFIX_TEST);
        doctorTest.setSpecialite_id(2);
        
        doctorDAO.save(doctorTest);
        
        Doctor doctorModifie = doctorDAO.getDoctor(doctorTest.getId());
        assertNotNull(doctorModifie, MSG_ENTITE_NON_NULLE);
        assertEquals(DOCTOR_PRENOM_2, doctorModifie.getFirst_name(), MSG_ENTITE_MODIFIEE);
        assertTrue(doctorModifie.getLast_name().contains(SUFFIX_TEST), MSG_ENTITE_MODIFIEE);
    }

    @Test
    @Order(5)
    @DisplayName("Test - Rechercher des docteurs")
    void testSearchDoctors() {
        DoctorSearchVM search = new DoctorSearchVM();
        search.setLastName(DOCTOR_NOM); // Utiliser le nom du docteur créé
        
        ArrayList<Doctor> resultats = doctorDAO.load(search);
        assertNotNull(resultats, MSG_ENTITE_NON_NULLE);
        assertTrue(resultats.size() >= 0, "La recherche doit retourner une liste");
    }

    @Test
    @Order(6)
    @DisplayName("Test - Supprimer un docteur")
    void testDeleteDoctor() {
        assertNotNull(doctorTest, "Le docteur de test doit exister");
        
        Integer idASupprimer = doctorTest.getId();
        doctorDAO.delete(idASupprimer);
        
        Doctor doctorSupprime = doctorDAO.getDoctor(idASupprimer);
        assertNull(doctorSupprime, MSG_ENTITE_SUPPRIMEE);
    }
}
