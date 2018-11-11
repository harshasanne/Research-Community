package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.*;
import java.net.URLDecoder;

import com.google.gson.Gson;

import play.data.DynamicForm;
import play.data.FormFactory;

import utils.DBDriver;
import javax.inject.Inject;
import com.typesafe.config.Config;

public class PaperController extends Controller {

    private FormFactory formFactory;
    private Config config;

    @Inject
    public PaperController(FormFactory formFactory, Config config) {
        this.formFactory = formFactory;
        this.config = config;
    }

    public Result getPapers(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Paper)-[:REFERS_TO]-(p:Paper) where a.title = '" + name + "' and p.title<>a.title return p.title";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);
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
        DynamicForm requestData = formFactory.form().bindFromRequest();
        System.out.println("test" + requestData.get("author"));

        String author = requestData.get("author");
        String title = requestData.get("title");
        String abstract_ = requestData.get("abstract_");
        String journal = requestData.get("journal");
        String year = requestData.get("year");

        String query = "MERGE (a:Author {authorName:'" + author + "'})\n" +
                "MERGE (p:Paper {title: '" + title + "'})\n" +
                "ON CREATE SET p.title='" + title + "', p.abstract='" + abstract_ + "', p.journal='" + journal + "', p.year='" + year+ "'\n" +
                "CREATE UNIQUE (a) - [:WRITES] -> (p);";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);
        String res = null;
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>()
            {
                @Override
                public Void execute( Transaction tx )
                {
                    tx.run( query );
                    return null;
                }
            } );
            return created();
        }
    }

}
