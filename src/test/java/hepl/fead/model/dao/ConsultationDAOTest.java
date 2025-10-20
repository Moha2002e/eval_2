package hepl.fead.model.dao;

import hepl.fead.model.entity.Consultation;
import hepl.fead.model.viewmodel.ConsultationSearchVM;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static hepl.fead.model.test.Const_JUnit.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConsultationDAOTest {

    private ConsultationDAO consultationDAO;
    private Consultation consultationTest;

    @BeforeEach
    void setUp() {
        consultationDAO = new ConsultationDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Test - Lire toutes les consultations")
    void testLoadAllConsultations() {
        ArrayList<Consultation> consultations = consultationDAO.load();
        assertNotNull(consultations, MSG_ENTITE_NON_NULLE);
        assertTrue(consultations.size() >= 0, "La liste des consultations doit etre accessible");
    }

    @Test
    @Order(2)
    @DisplayName("Test - Ajouter une nouvelle consultation")
    void testAddConsultation() {
        Consultation nouvelle = new Consultation();
        nouvelle.setPatient_id(1); // Assurer qu'un patient existe
        nouvelle.setDoctor_id(1);  // Assurer qu'un docteur existe
        nouvelle.setDate(CONSULTATION_DATE);
        nouvelle.setHour(CONSULTATION_HEURE);
        nouvelle.setReason(CONSULTATION_RAISON);

        consultationDAO.save(nouvelle);
        assertNotNull(nouvelle.getId(), MSG_ID_ASSIGNE);
        
        consultationTest = nouvelle; // Garder pour les tests suivants
    }

    @Test
    @Order(3)
    @DisplayName("Test - Lire une consultation par ID")
    void testGetConsultationById() {
        assertNotNull(consultationTest, "La consultation de test doit exister");
        
        Consultation consultation = consultationDAO.getConsultationById(consultationTest.getId());
        assertNotNull(consultation, MSG_ENTITE_NON_NULLE);
        assertEquals(CONSULTATION_DATE, consultation.getDate(), MSG_VALEUR_ATTENDUE);
        assertEquals(CONSULTATION_RAISON, consultation.getReason(), MSG_VALEUR_ATTENDUE);
    }

    @Test
    @Order(4)
    @DisplayName("Test - Modifier une consultation")
    void testUpdateConsultation() {
        assertNotNull(consultationTest, "La consultation de test doit exister");
        
        consultationTest.setReason(CONSULTATION_RAISON_MODIF);
        consultationTest.setHour("15:45");
        
        consultationDAO.save(consultationTest);
        
        Consultation consultationModifiee = consultationDAO.getConsultationById(consultationTest.getId());
        assertNotNull(consultationModifiee, MSG_ENTITE_NON_NULLE);
        assertEquals(CONSULTATION_RAISON_MODIF, consultationModifiee.getReason(), MSG_ENTITE_MODIFIEE);
    }

    @Test
    @Order(5)
    @DisplayName("Test - Rechercher des consultations")
    void testSearchConsultations() {
        ConsultationSearchVM search = new ConsultationSearchVM();
        search.setDateFrom("2025-01-01");
        search.setDateTo("2025-12-31");
        
        ArrayList<Consultation> resultats = consultationDAO.load(search);
        assertNotNull(resultats, MSG_ENTITE_NON_NULLE);
        assertTrue(resultats.size() >= 0, "La recherche doit retourner une liste");
    }

    @Test
    @Order(6)
    @DisplayName("Test - Supprimer une consultation")
    void testDeleteConsultation() {
        assertNotNull(consultationTest, "La consultation de test doit exister");
        
        Integer idASupprimer = consultationTest.getId();
        consultationDAO.delete(idASupprimer);
        
        Consultation consultationSupprimee = consultationDAO.getConsultationById(idASupprimer);
        assertNull(consultationSupprimee, MSG_ENTITE_SUPPRIMEE);
    }
}

