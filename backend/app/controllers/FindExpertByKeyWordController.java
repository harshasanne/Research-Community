package controllers;

import models.Author;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;

public class FindExpertByKeyWordController extends Controller {
    public Result getExpert(String keyword) throws Exception{
        keyword = URLDecoder.decode(keyword, "UTF-8");
        String query = "MATCH (a:Author)-[:WRITES]->(p:Paper)\n" +
                "WHERE p.abstract =~ '.*"+keyword+".*'\n" +
                "RETURN a.authorName, count(a.authorName) as c\n" +
                "ORDER BY c desc\n" +
                "limit 1";
        Driver driver = GraphDatabase.driver(
          "bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findExpert( tx, query );
                }
            } );
            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }
            return ok(new Gson().toJson(authorObjects));
        }
    }

    private static List<String> findExpert(Transaction tx, String query)
    {
        List<String> authors = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            authors.add(result.next().get(0).asString());
        }
        return authors;
    }
}
