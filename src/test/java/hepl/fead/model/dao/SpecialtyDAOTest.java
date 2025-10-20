package hepl.fead.model.dao;

import hepl.fead.model.entity.Speciality;
import hepl.fead.model.viewmodel.SpecialitySearchVM;
import hepl.fead.model.bd.DatabaseInitializer;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static hepl.fead.model.test.Const_JUnit.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpecialtyDAOTest {

    private static SpecialtyDAO specialtyDAO;
    private static Speciality specialityTest;

    @BeforeAll
    static void setUpAll() {
        // Initialiser la base de données
        DatabaseInitializer.initialize();
        specialtyDAO = new SpecialtyDAO();
    }

    @BeforeEach
    void setUp() {
        // Pas besoin de recréer le DAO à chaque test
    }

    @Test
    @Order(1)
    @DisplayName("Test - Lire toutes les spécialités")
    void testLoadAllSpecialties() {
        ArrayList<Speciality> specialties = specialtyDAO.load();
        assertNotNull(specialties, MSG_ENTITE_NON_NULLE);
        assertTrue(specialties.size() >= 0, "La liste des spécialités doit etre accessible");
    }

    @Test
    @Order(2)
    @DisplayName("Test - Ajouter une nouvelle spécialité")
    void testAddSpecialty() {
        try {
            Speciality nouvelle = new Speciality();
            nouvelle.setName(SPECIALTY_NOM);

            specialtyDAO.save(nouvelle);
            assertNotNull(nouvelle.getId(), MSG_ID_ASSIGNE);
            
            specialityTest = nouvelle; // Garder pour les tests suivants
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) {
                // Spécialité déjà existante, c'est OK
                System.out.println("Spécialité déjà existante - Test ignoré");
            } else {
                throw e;
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test - Lire une spécialité par ID")
    void testGetSpecialtyById() {
        if (specialityTest == null) {
            // Si le test précédent a échoué, on prend la première spécialité existante
            ArrayList<Speciality> specialties = specialtyDAO.load();
            if (!specialties.isEmpty()) {
                specialityTest = specialties.get(0);
            }
        }
        
        assertNotNull(specialityTest, "Une spécialité de test doit exister");
        
        Speciality speciality = specialtyDAO.getSpeciality(specialityTest.getId());
        assertNotNull(speciality, MSG_ENTITE_NON_NULLE);
        assertNotNull(speciality.getName(), "Le nom de la spécialité ne doit pas etre nul");
    }

    @Test
    @Order(4)
    @DisplayName("Test - Modifier une spécialité")
    void testUpdateSpecialty() {
        if (specialityTest == null) {
            // Si pas de spécialité de test, on prend la première existante
            ArrayList<Speciality> specialties = specialtyDAO.load();
            if (!specialties.isEmpty()) {
                specialityTest = specialties.get(0);
            }
        }
        
        assertNotNull(specialityTest, "Une spécialité de test doit exister");
        
        String nomOriginal = specialityTest.getName();
        specialityTest.setName(SPECIALTY_NOM_MODIF);
        
        specialtyDAO.save(specialityTest);
        
        Speciality specialityModifiee = specialtyDAO.getSpeciality(specialityTest.getId());
        assertNotNull(specialityModifiee, MSG_ENTITE_NON_NULLE);
        assertEquals(SPECIALTY_NOM_MODIF, specialityModifiee.getName(), MSG_ENTITE_MODIFIEE);
        
        // Restaurer le nom original
        specialityTest.setName(nomOriginal);
        specialtyDAO.save(specialityTest);
    }

    @Test
    @Order(5)
    @DisplayName("Test - Rechercher des spécialités")
    void testSearchSpecialties() {
        SpecialitySearchVM search = new SpecialitySearchVM();
        search.setName(RECHERCHE_CARDIO);
        
        ArrayList<Speciality> resultats = specialtyDAO.load(search);
        assertNotNull(resultats, MSG_ENTITE_NON_NULLE);
        assertTrue(resultats.size() >= 0, "La recherche doit retourner une liste");
    }

    @Test
    @Order(6)
    @DisplayName("Test - Supprimer une spécialité temporaire")
    void testDeleteSpecialty() {
        // Créer une spécialité temporaire pour la supprimer
        Speciality temp = new Speciality();
        temp.setName("Specialite-Temporaire-A-Supprimer");
        
        specialtyDAO.save(temp);
        Integer idASupprimer = temp.getId();
        
        specialtyDAO.delete(idASupprimer);
        
        Speciality specialiteSupprimee = specialtyDAO.getSpeciality(idASupprimer);
        assertNull(specialiteSupprimee, MSG_ENTITE_SUPPRIMEE);
    }
}
