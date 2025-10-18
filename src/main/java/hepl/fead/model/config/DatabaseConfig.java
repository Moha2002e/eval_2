package hepl.fead.model.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties pro ;

    static
    {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadProperties() throws IOException {
        pro = new Properties();
        try(InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")){
            if(input == null){
                throw new RuntimeException("impossible de trouver le fichier data");
            }
            pro.load(input);
        }catch (IOException e){
            throw new RuntimeException("erreur de chargement du fichier de conf");
        }

    }

    public static String getHost(){
        return pro.getProperty("db.host");
    }

    public static String getName(){
        return pro.getProperty("db.name");
    }

    public static String getUser(){
        return pro.getProperty("db.user");
    }

    public static String getPassword(){
        return pro.getProperty("db.password");
    }

    public static String getDriver(){
        return pro.getProperty("db.driver");
    }

    public static String getUrl(){
        return "jdbc:mysql://"+getHost()+"/"+getName();
    }

}
