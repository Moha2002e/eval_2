package hepl.fead.model.test;

import hepl.fead.model.bd.ConnectBD;
import java.sql.*;

/**
 * Test de connexion basique utilisant la classe ConnectBD
 * Affiche les données de la table 'patient'
 */
public class TestBd {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST DE CONNEXION - Utilisation de ConnectBD      ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        try {
            // Utiliser ConnectBD pour obtenir la connexion
            // Cela initialisera automatiquement la base de données si nécessaire
            System.out.println("📡 Connexion via ConnectBD.getConnection()...");
            Connection con = ConnectBD.getConnection();
            
            if (con != null && !con.isClosed()) {
                System.out.println("✅ Connexion établie avec succès");
                System.out.println("   Base de données: " + con.getCatalog());
                System.out.println("   Utilisateur: " + con.getMetaData().getUserName());
                System.out.println();

                // TEST 1: Afficher toutes les données de la table 'patient'
                System.out.println("📊 TEST 1: Lire toutes les données de la table 'patient'");
                System.out.println("----------------------------------------------------------");
                try (Statement stmt = con.createStatement()) {
                    String sql = "SELECT * FROM patient";
                    System.out.println("➡️  Requête: " + sql + "\n");

                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        // Affichage des métadonnées : nombre et noms des colonnes
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        // Afficher les noms de colonnes
                        System.out.println("Colonnes (" + columnCount + "):");
                        for (int j = 1; j <= columnCount; j++) {
                            System.out.print(metaData.getColumnName(j) + "\t");
                        }
                        System.out.println("\n" + "-".repeat(80));

                        // Afficher les données ligne par ligne
                        int rowCount = 0;
                        while (rs.next()) {
                            rowCount++;
                            for (int j = 1; j <= columnCount; j++) {
                                System.out.print(rs.getObject(j) + "\t");
                            }
                            System.out.println();
                        }
                        
                        if (rowCount == 0) {
                            System.out.println("(Aucune donnée trouvée)");
                        }
                        System.out.println("\n✅ " + rowCount + " ligne(s) affichée(s)");
                    }
                }

                // TEST 2: Compter le nombre total d'enregistrements
                System.out.println("\n📊 TEST 2: Statistiques de la base de données");
                System.out.println("----------------------------------------------------------");
                
                String[] tables = {"patient", "doctor", "specialties", "consultations"};
                for (String table : tables) {
                    try (Statement stmtCount = con.createStatement();
                         ResultSet rsc = stmtCount.executeQuery("SELECT COUNT(*) FROM " + table)) {
                        
                        if (rsc.next()) {
                            int count = rsc.getInt(1);
                            System.out.printf("  %-20s : %d enregistrement(s)%n", table, count);
                        }
                    } catch (SQLException e) {
                        System.out.printf("  %-20s : Table non trouvée ou erreur%n", table);
                    }
                }

                // TEST 3: Afficher des informations sur la connexion
                System.out.println("\n📊 TEST 3: Informations de connexion");
                System.out.println("----------------------------------------------------------");
                DatabaseMetaData dbMeta = con.getMetaData();
                System.out.println("  Produit: " + dbMeta.getDatabaseProductName());
                System.out.println("  Version: " + dbMeta.getDatabaseProductVersion());
                System.out.println("  Driver: " + dbMeta.getDriverName());
                System.out.println("  Version du driver: " + dbMeta.getDriverVersion());
                
                System.out.println("\n╔════════════════════════════════════════════════════════╗");
                System.out.println("║              TEST TERMINÉ AVEC SUCCÈS ✅               ║");
                System.out.println("╚════════════════════════════════════════════════════════╝");
                
            } else {
                System.err.println("❌ Impossible d'obtenir une connexion valide");
            }

        } catch (SQLException ex) {
            System.err.println("\n❌ ERREUR SQL");
            System.err.println("----------------------------------------------------------");
            System.err.println("Message: " + ex.getMessage());
            System.err.println("Code erreur: " + ex.getErrorCode());
            System.err.println("État SQL: " + ex.getSQLState());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("\n❌ ERREUR GÉNÉRALE");
            System.err.println("----------------------------------------------------------");
            System.err.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
