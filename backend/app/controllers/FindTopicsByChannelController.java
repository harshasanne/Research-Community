package controllers;

import models.Keyword;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;

public class FindTopicsByChannelController extends Controller {
    public Result getTopics(String channel) throws Exception{
        channel = URLDecoder.decode(channel, "UTF-8");
        String query = "MATCH (p:Paper)-[:HAS_KEYWORD]-(k:Keyword)\n" +
                "WHERE p.journal = '"+channel+"'\n" +
                "RETURN k.keyword, count(k.keyword) as c\n" +
                "ORDER BY c desc";
        Driver driver = GraphDatabase.driver(
          "bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
        try ( Session session = driver.session() )
        {
            List<Keyword> keywords =  session.readTransaction( new TransactionWork<List<Keyword>>()
            {
                @Override
                public List<Keyword> execute( Transaction tx )
                {
                    return findKeyword( tx, query );
                }
            } );

            return ok(new Gson().toJson(keywords));
        }
    }

    private static List<Keyword> findKeyword(Transaction tx, String query)
    {
        List<Keyword> keywords = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            Record t=result.next();
            keywords.add(new Keyword(t.get(0).asString(),t.get(1).asInt()));
        }
        return keywords;
    }
}
