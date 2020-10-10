package scalable.solutions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    private List<String> batch() {
        List<String> statements = new ArrayList<>();
        statements.add("CREATE TABLE IF NOT EXISTS orders (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  description varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (id)\n" +
                ")");
        statements.add("INSERT INTO orders VALUES(\n" +
                "  0,'first order'\n" +
                ")");
        return statements;
    }
}
