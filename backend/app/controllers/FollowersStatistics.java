package controllers;

import models.Author;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;

public class FollowersStatistics extends Controller {
    public Result getStats() throws Exception{
        // keyword = URLDecoder.decode(keyword, "UTF-8");
        String query = "match (a:Author)-[:FOLLOW]->(b:Author{authorName:'Ravinder Pal'})match (c:Author)-[:FOLLOW]->(a) match(a)-[WRITES]->(p:Paper) match(p)-[HAS_KEYWORD]->(k:Keyword)return (a.authorName) as followerName,count(distinct c) as numberOfFollowers ,count(distinct p) as numberOfPapers,count(distinct k.keyword) as numberOfKeywords, collect(distinct(k.keyword)) as Keywords";
        Driver driver = GraphDatabase.driver(
          "bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"));
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
            return ok(new Gson().toJson(authors));
        }
    }

    private static List<String> findExpert(Transaction tx, String query)
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
