package controllers;

import models.Author;
import models.Paper;
import utils.DBDriver;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;
import com.typesafe.config.Config;

import javax.inject.Inject;
import java.io.*;

public class AuthorController extends Controller {
    private Config config;

    @Inject
    public AuthorController(Config config) {
        this.config = config;
    }

    public Result getPapers(String name) throws Exception{
        System.out.println("reached the controller");
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:WRITES]->(p:Paper) where a.authorName = '" + name + "' return p.title";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchNodes( tx, query );
                }
            } );
            List<Paper> paperObjects = new ArrayList<Paper>();
            for (String p : papers) {
                paperObjects.add(new Paper(p));
            }
            return ok(new Gson().toJson(paperObjects));
        }
    }

    public Result getCollaborators(String name) throws Exception {
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:WRITES]->(p:Paper)<-[:WRITES]-(b:Author) where a.authorName = '"
          + name + "' return b.authorName";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchNodes( tx, query );
                }
            } );

            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }
            return ok(new Gson().toJson(authorObjects));
        }
    }

    private static List<String> matchNodes(Transaction tx, String query)
    {
        List<String> values = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            values.add(result.next().get(0).asString());
        }
        return values;
    }
       
    
}
