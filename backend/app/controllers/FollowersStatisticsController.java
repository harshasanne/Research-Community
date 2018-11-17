package controllers;

import models.Statistics;
import org.neo4j.driver.v1.*;
import play.mvc.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;
import utils.*;
import com.google.gson.Gson;
import com.typesafe.config.Config;
import services.Neo4jApiService;

import javax.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import models.Evolution;
import java.util.concurrent.CompletionStage;

public class FollowersStatisticsController extends Controller {
    private Config config;
    private final Neo4jApiService neo4jApiService;

    @Inject
    public FollowersStatisticsController(Neo4jApiService neo4jApiService,Config config) {
        this.neo4jApiService = neo4jApiService;
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
    public CompletionStage<Result> getCollaboration(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (author:Author{authorName:'"+name+"'})-[coauth:CO_AUTHOR*1..2]-(coauthor)Return collect(distinct author), collect(coauth) as relations, collect(distinct coauthor) as nodes";

        return neo4jApiService.callNeo4jApi(query, true).thenApply((response) -> {
            return ok(response).as("application/json");
        });
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
            System.out.println(d);
        }
        return papers;
    }
    
    public Result getTop20Papers() throws Exception {

        String query = "match(p:Paper) with p ORDER BY p.citationCount desc return p.journal as journal, collect({title:p.title,citation:p.citationCount})[0..20] as citaion";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    Gson gson = new Gson();
                    List<String> reqData = new ArrayList<>();
                    StatementResult result = tx.run(query);
                    while (result.hasNext()) {
                        Record record = result.next();
                        reqData.add(gson.toJson(record.asMap()));
                    }
                    return reqData;
                }
            } );
        System.out.println(papers);
            return ok("{\"data\": "+papers+"}").as("application/json");
        }
    }

    public Result getCitaionCount() throws Exception {
       
        String query = "match (p)where exists(p.journal) return (p.title) limit 100";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return paperList( tx, query );
                }
            } );

            for (String title: papers){
                 String cIndex ="0";
                 cIndex=getIndex(title);
                 String neo4jQuery = "match (p) where exists(p.journal) and p.title = '"+title+"' set p.citationCount='"+cIndex+"' return (p.title)";
             try ( Session session1 = driver.session() )
                {   
                  List<String> papers1 =  session1.readTransaction( new TransactionWork<List<String>>()
                 {
                    @Override
                    public List<String> execute(Transaction t){
                        return paperList( t, neo4jQuery );
                    }
                    } );
                }   Thread.sleep(1000);

            }
            return ok(new Gson().toJson(papers));
        }
    }
    private static String getIndex(String title)
    {   

        try {
                String URL = "https://scholar.google.com/scholar?hl=en&as_sdt=0%2C5&q='"+title+"'&btnG=";

                Document document = Jsoup.connect(URL).get();

                Elements linksOnPage = document.select("a[href]");
                String citaion = "";

                for (Element page : linksOnPage) {
                    String temp = page.text();
                    System.out.println(temp);
                    if(temp.contains("Cited"))
                        citaion = temp;
                }
                String cIndex ="";
                String[] one = citaion.split(" ");
                 cIndex = one[one.length-1];
        System.out.println(cIndex);

            return cIndex;
            } catch (IOException e) {
            }
            return "0";
    }
     private static List<String> paperList(Transaction tx, String query)
    {
        List<String> papers = new ArrayList<>();
        StatementResult result = tx.run( query );
        Gson gson = new Gson();

        while ( result.hasNext() )
        {
            papers.add(result.next().get(0).asString());
        }
        return papers;
    }
    public Result getTop20PapersWithYear(String start, String end) throws Exception {

        String query = "match(p:Paper) WHERE p.year>'"+start+"' and p.year<'"+end+"' with p ORDER BY p.citationCount desc return p.journal as Journal, p.year as year, collect({title:p.title,citationCount:p.citationCount})[0..10] as CitationData";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    Gson gson = new Gson();
                    List<String> reqData = new ArrayList<>();
                    StatementResult result = tx.run(query);
                    while (result.hasNext()) {
                        Record record = result.next();
                        reqData.add(gson.toJson(record.asMap()));
                    }
                    return reqData;
                }
            } );
            return ok("{\"data\": "+papers+"}").as("application/json");

        }
    }


}
