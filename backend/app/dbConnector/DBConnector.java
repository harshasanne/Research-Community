package dbConnector;

import java.util.*;

import models.Author;
import models.Keyword;
import models.Paper;

import org.neo4j.driver.v1.*;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

public class DBConnector {

    private final URL = "bolt://localhost:7687";
    private final USERNAME = "neo4j";
    private final PASSWORD = "123456";

    private static instance = null;
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