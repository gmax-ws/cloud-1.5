package scalable.solutions.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DataBaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseInitializer.class);

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void init() {
        logger.info("Database initialize...");
        try (Connection connection = dataSource.getConnection()) {
            for (String line : batch()) {
                try (Statement stm = connection.createStatement()) {
                    stm.execute(line);
                } catch (SQLException e) {
                    logger.error("An error occurred while executing statement", e);
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while opening database connection", e);
        }
    }

    private static List<String> batch() {
        List<String> statements = new ArrayList<>();
        statements.add("CREATE TABLE IF NOT EXISTS oauth_client_details (\n" +
                "  client_id varchar(256) NOT NULL,\n" +
                "  client_secret varchar(256) DEFAULT NULL,\n" +
                "  resource_ids varchar(256) DEFAULT NULL,\n" +
                "  scope varchar(256) DEFAULT NULL,\n" +
                "  authorized_grant_types varchar(256) DEFAULT NULL,\n" +
                "  web_server_redirect_uri varchar(256) DEFAULT NULL,\n" +
                "  authorities varchar(256) DEFAULT NULL,\n" +
                "  access_token_validity int(11) DEFAULT NULL,\n" +
                "  refresh_token_validity int(11) DEFAULT NULL,\n" +
                "  additional_information varchar(4096) DEFAULT NULL,\n" +
                "  autoapprove varchar(256) DEFAULT NULL,\n" +
                "  PRIMARY KEY (client_id)\n" +
                ")");
        statements.add("INSERT INTO oauth_client_details " +
                "VALUES ('gmax','2n3055s','inbound,outbound','read,write','password,refresh_token',NULL,'ROLE_CLIENT',3600,7200,NULL,NULL)\n");
        statements.add("CREATE TABLE IF NOT EXISTS roles (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  username varchar(45) DEFAULT NULL,\n" +
                "  role varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (id)\n" +
                ")");
        statements.add("INSERT INTO roles " +
                "VALUES (1,'admin','ROLE_ADMIN'),(2,'admin','ROLE_USER'),(3,'user','ROLE_USER'),(4,'marius','ROLE_DEVELOPER')");
        statements.add("CREATE TABLE IF NOT EXISTS users (\n" +
                "  username varchar(45) NOT NULL,\n" +
                "  password varchar(45) DEFAULT NULL,\n" +
                "  enabled tinyint(4) DEFAULT NULL,\n" +
                "  PRIMARY KEY (username)\n" +
                ")");
        statements.add("INSERT INTO users VALUES ('admin','admin',1),('marius','gligor',1),('user','user',0)");
        return statements;
    }
}
