package hepl.fead.model.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static hepl.fead.model.config.Const_Config.*;

public class DatabaseConfig {
    private static Properties pro ;
    // runtime overrides (optional)
    private static String runtimeUrl = null;
    private static String runtimeUser = null;
    private static String runtimePassword = null;

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
        try(InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(FICHIER_PROPERTIES)){
            if(input == null){
                throw new RuntimeException(ERREUR_FICHIER_INTROUVABLE);
            }
            pro.load(input);
        }catch (IOException e){
            throw new RuntimeException(ERREUR_CHARGEMENT_FICHIER);
        }

    }

    public static String getHost(){
        return pro.getProperty(KEY_DB_HOST);
    }

    public static String getName(){
        return pro.getProperty(KEY_DB_NAME);
    }

    public static String getUser(){
        return pro.getProperty(KEY_DB_USER);
    }

    public static String getPassword(){
        return pro.getProperty(KEY_DB_PASSWORD);
    }

    public static String getDriver(){
        return pro.getProperty(KEY_DB_DRIVER);
    }

    public static String getUrl(){
        if (runtimeUrl != null) return runtimeUrl;
        return JDBC_MYSQL_PREFIX + getHost() + SLASH + getName();
    }

    public static String getRuntimeUser(){
        if (runtimeUser != null) return runtimeUser;
        return getUser();
    }

    public static String getRuntimePassword(){
        if (runtimePassword != null) return runtimePassword;
        return getPassword();
    }

    /**
     * Initialize runtime DB connection parameters (used by server launcher)
     */
    public static void init(String url, String user, String password) {
        runtimeUrl = url;
        runtimeUser = user;
        runtimePassword = password;
    }

    /**
     * Clear runtime overrides
     */
    public static void close() {
        runtimeUrl = null;
        runtimeUser = null;
        runtimePassword = null;
    }

}
