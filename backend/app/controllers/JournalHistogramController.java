package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.io.*;
import com.google.gson.Gson;

public class JournalHistogramController extends Controller {

    public Result getJournalAuthors(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        System.out.println(name);
       // String query = "match (a:Author)-[:WRITES]->(p:Paper) where a.authorName = '" + name + "' return p.title";
       String query = "match (a:Author)-[:WRITES]->(p:Paper) where p.journal = '" + name + "' return count(a.authorName),p.volume";

        Driver driver = GraphDatabase.driver(
          "bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
        try ( Session session = driver.session() )
        {
            List<String> year =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query );
                }
            } );
              try {
                System.out.println(year);
            FileWriter writer = new FileWriter("journal.json"); 
             writer.write(year.toString());
            
            writer.close();

        } catch (IOException iox) {
            //do stuff with exception
            iox.printStackTrace();
        }

            // List<Paper> paperObjects = new ArrayList<Paper>();
            // for (int p : year) {
            //     paperObjects.add(new Paper(p));
            // }
            return ok(year.toString()).as("applications/json");
        }
    }

    private static List<String> matchPaperNodes(Transaction tx, String query)
    {
        List<String> year = new ArrayList<>();
        StatementResult result = tx.run( query );
        Gson gson = new Gson();

        while ( result.hasNext() )
        {
             Record record = result.next();
            String d= gson.toJson(record.asMap());
            year.add(d);
            System.out.println(d);

        }
        return year;
    }
       
    
}
