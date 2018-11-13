package controllers;

import models.Author;
import models.Statistics;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.util.*;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import dbConnector.DBConnector;
import utils.DBDriver;
import javax.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class FollowersStatistics extends Controller {
    private Config config;

    @Inject
    public FollowersStatistics( Config config) {
        this.config = config;
    }

    public Result getStats(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:FOLLOWS]->(b:Author{authorName:'"+name+"'}) match(a)-[WRITES]->(p:Paper) match(p)-[HAS_KEYWORD]->(k:Keyword)return (a.authorName) as followerName,count(distinct a) as numberOfFollowers ,count(distinct p) as numberOfPapers,count(distinct k.keyword) as numberOfKeywords, collect(distinct(k.keyword)) as Keywords";
       
        Driver driver = DBDriver.getDriver(this.config);
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
    
    public Result getTop20Papers() throws Exception {

       
        // name = URLDecoder.decode(name, "UTF-8");
        String query = "match (p)where exists(p.journal) return (p.title) ,(p.cIndex) order by toInt(p.cIndex) limit 100";
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
                 // String cIndex =getIndex("title");

            for (String title: papers){
                 String cIndex =getIndex(title);
                 String neo4jQuery = "match (p)where exists(p.journal)and p.title = '"+title+"' set p.citationCount= '"+cIndex+"' return (p.title) ,(p.index),(p.cIndex)";
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
        // name = URLDecoder.decode(name, "UTF-8");

        String query = "match (p)where exists(p.journal)and p.year >= '"+start+"'and p.year<='"+end+"' return (p.title) ,(p.index) order by toInt(p.index) desc limit 10";
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
            return ok(new Gson().toJson(papers));
        }
    }


}
