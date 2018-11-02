package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;
import java.io.*;

public class AuthorController extends Controller {
    public Result getPapers(String name) throws Exception{
        System.out.println("reached the controller");
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:WRITES]->(p:Paper) where a.authorName = '" + name + "' return p.title";
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
       
    
}
