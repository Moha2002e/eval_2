package hepl.fead.model;

import hepl.fead.model.bd.DatabaseConnectionTest;
import hepl.fead.model.dao.*;
import hepl.fead.model.entity.EntityTest;
import org.junit.jupiter.api.*;

/**
 * Suite de tests unitaires - Classe de référence pour tous les tests
 * 
 * Pour exécuter tous les tests :
 * mvn test
 * 
 * Pour exécuter une classe spécifique :
 * mvn test -Dtest=PatientDAOTest
 */
@DisplayName("Suite complète de tests unitaires")
public class AllTestsSuite {
    
    @BeforeAll
    static void setUpSuite() {
        System.out.println("========================================");
        System.out.println("  DÉBUT DES TESTS UNITAIRES JUNIT");
        System.out.println("========================================");
        System.out.println("Classes de test disponibles :");
        System.out.println("- DatabaseConnectionTest");
        System.out.println("- EntityTest");
        System.out.println("- PatientDAOTest");
        System.out.println("- DoctorDAOTest");
        System.out.println("- SpecialtyDAOTest");
        System.out.println("- ConsultationDAOTest");
        System.out.println("========================================");
    }
    
    @AfterAll
    static void tearDownSuite() {
        System.out.println("========================================");
        System.out.println("  FIN DES TESTS UNITAIRES JUNIT");
        System.out.println("========================================");
    }
}
