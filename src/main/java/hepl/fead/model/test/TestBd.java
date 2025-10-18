package hepl.fead.model.test;

import java.sql.*;

public class TestBd {

    public static void main(String[] args) {
        // Informations de connexion
        String url = "jdbc:mysql://172.20.10.4:3306/PourStudent"; // ✅ ajout explicite du port 3306
        String user = "Student";
        String password = "PassStudent1_";

        try {
            // Chargement du driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL chargé avec succès");

            // Connexion à la base de données
            try (Connection con = DriverManager.getConnection(url, user, password)) {
                System.out.println("✅ Connexion à la base 'PourStudent' établie");

                // Création d'un Statement pour exécuter une requête SELECT
                try (Statement stmt = con.createStatement()) {
                    String sql = "SELECT * FROM patients";
                    System.out.println("➡️  Exécution de la requête : " + sql);

                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        System.out.println("✅ Requête SELECT exécutée avec succès");

                        // Affichage des métadonnées : nombre et noms des colonnes
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        System.out.println("Nombre de colonnes : " + columnCount);

                        // Afficher les noms de colonnes
                        for (int j = 1; j <= columnCount; j++) {
                            System.out.print(metaData.getColumnName(j) + "\t");
                        }
                        System.out.println("\n----------------------------------");

                        // Afficher les données ligne par ligne
                        while (rs.next()) {
                            for (int j = 1; j <= columnCount; j++) {
                                System.out.print(rs.getObject(j) + "\t");
                            }
                            System.out.println();
                        }
                    }
                }

                // Exemple de requête : compter le nombre de tuples
                String countQuery = "SELECT COUNT(*) FROM patients";
                try (Statement stmtCount = con.createStatement();
                     ResultSet rsc = stmtCount.executeQuery(countQuery)) {

                    if (rsc.next()) {
                        int nbre = rsc.getInt(1);
                        System.out.println("\n✅ Nombre total d’enregistrements dans 'patients' : " + nbre);
                    }
                }

            } // La connexion est automatiquement fermée ici

        } catch (ClassNotFoundException ex) {
            System.err.println("❌ Erreur : driver MySQL non trouvé - " + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("❌ Erreur SQL : " + ex.getMessage());
        }
    }
}
