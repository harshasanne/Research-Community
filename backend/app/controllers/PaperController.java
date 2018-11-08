package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;
import dbConnector.DBConnector;

import java.util.*;
import java.net.URLDecoder;

import com.google.gson.Gson;
import javax.inject.*;

public class PaperController extends Controller {

    private static DBConnector dbConnector;
    private static Driver driver;

    @Inject
    public PaperController() {
        dbConnector = DBConnector.getInstance();
        driver = dbConnector.getDriver();
    }

    public Result getPapers(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Paper)-[:REFERS_TO]-(p:Paper) where a.title = '" + name + "' and p.title<>a.title return p.title";
        System.out.println(query);
        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query );
                }
            } );
            List<Paper> paperObjects = new ArrayList<Paper>();
            for (String p : papers) {
                paperObjects.add(new Paper(p));
            }
            return ok(new Gson().toJson(paperObjects));
        }
    }

    private static List<String> matchPaperNodes(Transaction tx, String query)
    {
        List<String> papers = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            papers.add(result.next().get(0).asString());
        }
        return papers;
    }

    public Result createPaper() throws Exception {
        JsonNode json = request().body().asJson();
        if (json == null) {
            System.out.println("User info not present, expecting json data");
        }
        String author = json.path("author").asText();
        String title = json.path("title").asText();
        String abstract_ = json.path("abstract").asText();
        String journal = json.path("journal").asText();
        String year = json.path("year").asText();

        String query = "MERGE (:Author {authorName: '" + author + "'})-[:WRITES]->(:Paper {title: '" + title + "', abstract: '" + abstract_ + "', journal: '" + journal + "', year: " + year + "});";

        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>()
            {
                @Override
                public Void execute( Transaction tx )
                {
                    return createPaperNode( tx, query );
                }
            } );
            return ok();
        }
    }

    private static Void createPaperNode( Transaction tx, String query )
    {
        tx.run( query );
        return null;
    }

}
