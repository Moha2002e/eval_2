package hepl.fead.model.test;

import hepl.fead.model.bd.ConnectBD;
import java.sql.*;

import static hepl.fead.model.test.Const_Test.*;

public class TestBd {

    public static void main(String[] args) {
        System.out.println(SEPARATEUR);
        System.out.println(String.format(FORMAT_TITRE, TEST_CONNEXION));
        System.out.println(SEPARATEUR + "\n");

        try {
            System.out.println(CONNEXION_BD);
            Connection con = ConnectBD.getConnection();
            
            if (con != null && !con.isClosed()) {
                System.out.println(CONNEXION_ETABLIE);
                System.out.printf((FORMAT_DETAIL) + "%n", LABEL_BD, con.getCatalog());
                System.out.printf((FORMAT_DETAIL) + "%n", LABEL_UTILISATEUR, con.getMetaData().getUserName());

                System.out.println("\n" + String.format(FORMAT_TEST, 1, LIRE_TOUS));
                System.out.println(SEPARATEUR_COURT);
                try (Statement stmt = con.createStatement();
                     ResultSet rs = stmt.executeQuery(SELECT_ALL_PATIENT)) {
                    
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    System.out.printf((LABEL_NB_COLONNES) + "%n", columnCount);
                    for (int j = 1; j <= columnCount; j++) {
                        System.out.print(metaData.getColumnName(j) + "\t");
                    }
                    System.out.println();
                    
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                        for (int j = 1; j <= columnCount; j++) {
                            System.out.print(rs.getObject(j) + "\t");
                        }
                        System.out.println();
                    }
                    
                    if (rowCount == 0) System.out.println(AUCUNE_DONNEE);
                    System.out.println(String.format(LIGNES_AFFICHEES, rowCount));
                }

                System.out.println("\n" + String.format(FORMAT_TEST, 2, STATISTIQUES_BD));
                System.out.println(SEPARATEUR_COURT);
                String[] tables = {TABLE_PATIENT, TABLE_DOCTOR, TABLE_SPECIALTIES, TABLE_CONSULTATIONS};
                for (String table : tables) {
                    try (Statement stmtCount = con.createStatement();
                         ResultSet rsc = stmtCount.executeQuery(String.format(SELECT_COUNT, table))) {
                        if (rsc.next()) {
                            System.out.println(String.format(FORMAT_TABLE_COUNT, table, rsc.getInt(1)));
                        }
                    } catch (SQLException e) {
                        System.out.println(String.format(FORMAT_TABLE_ERREUR, table));
                    }
                }

                System.out.println("\n" + String.format(FORMAT_TEST, 3, INFO_CONNEXION));
                System.out.println(SEPARATEUR_COURT);
                DatabaseMetaData dbMeta = con.getMetaData();
                System.out.println(String.format(FORMAT_DETAIL, LABEL_PRODUIT, dbMeta.getDatabaseProductName()));
                System.out.println(String.format(FORMAT_DETAIL, LABEL_VERSION, dbMeta.getDatabaseProductVersion()));
                System.out.println(String.format(FORMAT_DETAIL, LABEL_DRIVER, dbMeta.getDriverName()));
                System.out.println(String.format(FORMAT_DETAIL, LABEL_VERSION_DRIVER, dbMeta.getDriverVersion()));
                
                System.out.println("\n" + SEPARATEUR);
                System.out.println(String.format(FORMAT_TITRE, SUCCES));
                System.out.println(SEPARATEUR);
            } else {
                System.err.println(ECHEC);
            }

        } catch (SQLException ex) {
            System.err.println("\n" + ERREUR_SQL);
            System.err.println(SEPARATEUR_COURT);
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("\n" + ERREUR_GENERALE);
            System.err.println(SEPARATEUR_COURT);
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
