package hepl.fead.model.bd;

import hepl.fead.model.config.DatabaseConfig;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de connexion à la base de données")
public class DatabaseConnectionTest {

    @Test
    @DisplayName("Test - Connexion à la base de données")
    void testDatabaseConnection() throws SQLException {
        Connection conn = ConnectBD.getConnection();
        
        assertNotNull(conn, "La connexion ne doit pas etre nulle");
        assertFalse(conn.isClosed(), "La connexion doit etre ouverte");
        
        try {
            String catalog = conn.getCatalog();
            assertNotNull(catalog, "Le nom de la base de données ne doit pas etre nul");
            assertEquals(DatabaseConfig.getName(), catalog, "Le nom de la base doit correspondre à la configuration");
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }

    @Test
    @DisplayName("Test - Métadonnées de la base de données")
    void testDatabaseMetadata() {
        Connection conn = ConnectBD.getConnection();
        
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            
            assertNotNull(metaData.getDatabaseProductName(), "Le nom du produit ne doit pas etre nul");
            assertNotNull(metaData.getDatabaseProductVersion(), "La version ne doit pas etre nulle");
            assertNotNull(metaData.getDriverName(), "Le nom du driver ne doit pas etre nul");
            assertNotNull(metaData.getDriverVersion(), "La version du driver ne doit pas etre nulle");
            
            // Vérifier que c'est bien MySQL
            assertTrue(metaData.getDatabaseProductName().toLowerCase().contains("mysql"), 
                      "Le produit doit etre MySQL");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }

    @Test
    @DisplayName("Test - Tables de la base de données")
    void testDatabaseTables() throws SQLException {
        Connection conn = ConnectBD.getConnection();
        
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});
            
            int tableCount = 0;
            while (tables.next()) {
                tableCount++;
                String tableName = tables.getString("TABLE_NAME");
                assertNotNull(tableName, "Le nom de la table ne doit pas etre nul");
            }
            
            assertTrue(tableCount >= 4, "Au moins 4 tables doivent exister (specialties, patient, doctor, consultations)");
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }

    @Test
    @DisplayName("Test - Exécution de requête SQL")
    void testSQLQuery() {
        Connection conn = ConnectBD.getConnection();
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 as test_value");
            
            assertTrue(rs.next(), "La requête doit retourner un résultat");
            assertEquals(1, rs.getInt("test_value"), "La valeur doit etre 1");
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }
}

