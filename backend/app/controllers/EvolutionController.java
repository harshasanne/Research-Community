package controllers;

import com.google.gson.Gson;
import models.Evolution;
import models.Keyword;
import org.neo4j.driver.v1.*;
import play.mvc.Controller;
import play.mvc.Result;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class EvolutionController extends Controller {

    public Result getTopics(String channel, Integer startYear,Integer endYear ) throws Exception{
        channel = URLDecoder.decode(channel, "UTF-8");

        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"));

        List<Evolution> res = new ArrayList<>();


        for(int i=startYear;i<=endYear;i++){
            String query = "MATCH (p:Paper)-[:HAS_KEYWORD]-(k:Keyword)\n" +
                    "WHERE p.journal = '"+channel+"' and p.year='"+String.valueOf(i)+"'\n" +
                    "RETURN k.keyword, count(k.keyword) as c\n" +
                    "ORDER BY c desc\n"+
                    "LIMIT 5";
            try ( Session session = driver.session() )
            {
                List<Keyword> keywords =  session.readTransaction(new TransactionWork<List<Keyword>>()
                {
                    @Override
                    public List<Keyword> execute( Transaction tx )
                    {
                        return findKeyword( tx, query );
                    }
                } );
                res.add(new Evolution(String.valueOf(i),keywords));

            }
        }
        return ok(new Gson().toJson(res));
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
