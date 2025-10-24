package consultation.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConsultationServerConfig {
    private int serverPort;
    private int serverThreads;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public ConsultationServerConfig(String filePath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            props.load(fis);
        }
        this.serverPort = Integer.parseInt(props.getProperty("server.port", "9090"));
        this.serverThreads = Integer.parseInt(props.getProperty("server.threads", "4"));
        this.dbUrl = props.getProperty("db.url");
        this.dbUser = props.getProperty("db.user");
        this.dbPassword = props.getProperty("db.password");
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getServerThreads() {
        return serverThreads;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
