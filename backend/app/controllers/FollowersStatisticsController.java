package controllers;

import models.Statistics;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import utils.DBDriver;
import javax.inject.Inject;

public class FollowersStatisticsController extends Controller {
    private Config config;

    @Inject
    public FollowersStatisticsController(Config config) {
        this.config = config;
    }

    public Result getStats(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");

        String getFollowersQuery = "match (a:Author)-[:FOLLOWS]->(b:Author{authorName:'"+name+"'}) "
          + "return a.authorName";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<Statistics> authors =  session.readTransaction( new TransactionWork<List<Statistics>>()
            {
                @Override
                public List<Statistics> execute( Transaction tx )
                {
                    List<Statistics> stats = new ArrayList<Statistics>();
                    // First, get all the followers' names since some followers have no publications
                    List<String> followers = getFollowers(tx, getFollowersQuery);
                    if (followers.isEmpty()) return stats;
                    String getStatisticsQuery = "with " + serializeList(followers)
                      + "as names optional match (a:Author)-[:WRITES]->(p:Paper)-[:HAS_KEYWORD]->(k:Keyword)"
                      + "where a.authorName in names return a.authorName, count(distinct p.title) as cnt, count(distinct k) as keys";

                    Map<String, Statistics> statistics = getStatistics(tx, getStatisticsQuery);
                    for (String follower: followers) {
                        if (statistics.containsKey(follower)) {
                            stats.add(statistics.get(follower));
                        }
                        else {
                            stats.add(new Statistics(follower, 0,0));
                        }
                    }
                    return stats;
                }
            } );
            return ok(new Gson().toJson(authors));
        }
    }

    private static String serializeList(List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String value : values) {
            sb.append("'" + value + "', ");
        }

        String result = sb.toString();
        return result.substring(0, result.length()-2) + "]";
    }

    private static List<String> getFollowers(Transaction tx, String query) {
        StatementResult result  = tx.run(query);
        List<String> followers = new ArrayList<String>();
        while (result.hasNext()) {
            followers.add(result.next().get(0).asString());
        }

        return followers;
    }

    private static Map<String, Statistics> getStatistics(Transaction tx, String query)
    {
        Map<String, Statistics> stat = new HashMap<String, Statistics>();
        List<String> s = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() ) {
            Record t = result.next();
            stat.put(t.get(0).asString(), new Statistics(t.get(0).asString(), t.get(1).asInt(), t.get(2).asInt()));
        }

        return stat;
    }
    public Result getCollaboration( String name) throws Exception {
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (author:Author{authorName:'"+name+"'})-[coauth:CO_AUTHOR*1..2]-(coauthor)Return collect(distinct author), collect(coauth) as relations, collect(distinct coauthor) as nodes";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findRecentPaper( tx, query );
                }
            } );
            // MyJsonContainer jsonContainer = new MyJsonContainer();
            // jsonContainer.setTest(papers);
            return ok(new Gson().toJson(papers));
        }
    }
     private static List<String> findRecentPaper(Transaction tx, String query)
    {
        List<String> papers = new ArrayList<>();
        StatementResult result = tx.run( query );
        Gson gson = new Gson();

        while ( result.hasNext() )
        {
            Record t = result.next();
            String d= gson.toJson(t.asMap());
            papers.add(d);
            System.out.println(d+"there");

            // papers.add(new Paper(t.get(0).asString(), t.get(1).asString(), t.get(2).asString(), t.get(3).asString(), t.get(4).asString()));
        }
        return papers;
    }

}
