package dbConnector;

import java.util.*;

import models.Author;
import models.Keyword;
import models.Paper;

import org.neo4j.driver.v1.*;

public class DBConnector {

    private final String URL = "bolt://localhost:7687";
    private final String USERNAME = "neo4j";
    private final String PASSWORD = "12345";

    private static DBConnector instance = null;
    private static Driver driver = null;
    private static Session session = null;

    private DBConnector() {
        driver = GraphDatabase.driver(URL, AuthTokens.basic(USERNAME, PASSWORD));
        session = driver.session();
    }

    public static DBConnector getInstance() {
        if (instance == null) {
            instance = new DBConnector();
        }
        return instance;
    }

    public Driver getDriver() {
        return driver;
    }

    public Session getSession() {
        return session;
    }

}