package controllers;

import models.Author;
import models.Statistics;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;

public class FollowersStatistics extends Controller {
    public Result getStats(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:FOLLOW]->(b:Author{authorName:'"+name+"'})match (c:Author)-[:FOLLOW]->(a) match(a)-[WRITES]->(p:Paper) match(p)-[HAS_KEYWORD]->(k:Keyword)return (a.authorName) as followerName,count(distinct c) as numberOfFollowers ,count(distinct p) as numberOfPapers,count(distinct k.keyword) as numberOfKeywords, collect(distinct(k.keyword)) as Keywords";
        Driver driver = GraphDatabase.driver(
          "bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"));
        try ( Session session = driver.session() )
        {
            List<Statistics> authors =  session.readTransaction( new TransactionWork<List<Statistics>>()
            {
                @Override
                public List<Statistics> execute( Transaction tx )
                {
                    return findExpert( tx, query );
                }
            } );
            return ok(new Gson().toJson(authors));
        }
    }

    private static List<Statistics> findExpert(Transaction tx, String query)
    {
         List<Statistics> stat = new ArrayList<>();
         List<String> s = new ArrayList<>();
        StatementResult result = tx.run( query );
        Gson gson = new Gson();
        while ( result.hasNext() )
        {
            Record t = result.next();
            stat.add(new Statistics(t.get(0).asString(), t.get(1).toString(), t.get(2).toString(), t.get(3).toString(), t.get(4).toString()));
        }
            //  Record record = result.next();
            // String d= gson.toJson(record.asMap());
            // year.add(d);
            // System.out.println(d);
        return stat;
    }
}
