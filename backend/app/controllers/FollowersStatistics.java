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

public class FollowersStatistics extends Controller {
    private Config config;

    @Inject
    public FollowersStatistics( Config config) {
        this.config = config;
    }

    public Result getStats(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:FOLLOW]->(b:Author{authorName:'"+name+"'}) match(a)-[WRITES]->(p:Paper) match(p)-[HAS_KEYWORD]->(k:Keyword)return (a.authorName) as followerName,count(distinct a) as numberOfFollowers ,count(distinct p) as numberOfPapers,count(distinct k.keyword) as numberOfKeywords, collect(distinct(k.keyword)) as Keywords";
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
    public Result getCollaboration() throws Exception {
        String query = "match (author:Author{authorName:'Jia Zhang'})-[coauth:CO_AUTHOR*1..2]-(coauthor)Return collect(distinct author), collect(coauth), collect(distinct coauthor)";
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
            System.out.println(papers+"there");

            // papers.add(new Paper(t.get(0).asString(), t.get(1).asString(), t.get(2).asString(), t.get(3).asString(), t.get(4).asString()));
        }
        return papers;
    }

}
