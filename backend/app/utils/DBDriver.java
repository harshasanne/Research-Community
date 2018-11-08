package utils;

import org.neo4j.driver.v1.*;
import com.typesafe.config.Config;
public class DBDriver {
    private static Driver driver;
    public static Driver getDriver(Config configuration) {
        if (driver != null) {
            return driver;
        }

        String username = configuration.getString("username");
        String password = configuration.getString("password");
        driver = GraphDatabase.driver(
          "bolt://localhost:7687", AuthTokens.basic(username, password));
        return driver;
    }
}
