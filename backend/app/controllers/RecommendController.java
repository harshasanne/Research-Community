package controllers;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import models.Author;
import org.neo4j.driver.v1.*;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DBDriver;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendController extends Controller {

    private com.typesafe.config.Config config;

    @Inject
    public RecommendController(Config config) {
        this.config = config;
    }

    public Result getRecommend(String keyword,String username) throws Exception{
        Set<String> dedup = new HashSet<>();
        keyword = URLDecoder.decode(keyword, "UTF-8");
        username = URLDecoder.decode(username, "UTF-8");
        String interest="";

        Driver driver = DBDriver.getDriver(this.config);
        List<String> res = new ArrayList<>();

        try ( Session session = driver.session() )
        {

            String query1  = "MATCH (p:Paper)-[:HAS_KEYWORD]->(:Keyword {keyword:'"+keyword+"'})\n" +
                    "WHERE p.abstract =~ '.*"+interest+".*'\n" +
                    "RETURN p.title\n" +
                    "limit 10";
            String query2  = "MATCH (p:Paper)\n" +
                    "WHERE p.abstract =~ '.*"+interest+".*'\n" +
                    "RETURN p.title\n" +
                    "limit 10";


            List<String> titles =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query1 );
                }
            } );
            for (String t : titles) {
                if(!dedup.contains(t)){
                    dedup.add(t);
                    res.add(t);
                }
            }
            if(res.size()<=10){
                titles =  session.readTransaction( new TransactionWork<List<String>>()
                {
                    @Override
                    public List<String> execute( Transaction tx )
                    {
                        return matchPaperNodes( tx, query2 );
                    }
                } );
                for (String t : titles) {
                    if(!dedup.contains(t)){
                        dedup.add(t);
                        res.add(t);
                    }
                }
            }

            if(res.size()<=10){
                String ls = "['"+String.join("','",res)+"']";
                String query3 ="MATCH (p:Paper)-[:REFERS_TO]-(p2:Paper)\n" +
                        "WHERE p.title IN "+ls+" AND NOT p2.title IN "+ls+"\n" +
                        "RETURN p2.title\n" +
                        "limit 10";
                titles =  session.readTransaction( new TransactionWork<List<String>>()
                {
                    @Override
                    public List<String> execute( Transaction tx )
                    {
                        return matchPaperNodes( tx, query3 );
                    }
                } );
                for (String t : titles) {
                    if(!dedup.contains(t)){
                        dedup.add(t);
                        res.add(t);
                    }
                }
            }

            return ok(new Gson().toJson(res));
        }



    }


    private static List<String> matchPaperNodes(Transaction tx, String query)
    {
        List<String> papers = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            papers.add(result.next().get(0).asString());
        }
        return papers;
    }

    private static String getInterest(Transaction tx, String query)
    {
        String res="";
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            res=result.next().get(0).asString();
        }
        return res;
    }


}
