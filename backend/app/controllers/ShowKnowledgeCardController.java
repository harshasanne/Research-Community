package controllers;

import com.typesafe.config.Config;
import models.Paper;
import models.PaperMeta;
import org.neo4j.driver.v1.*;

import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.*;
import utils.DBDriver;

import javax.inject.Inject;
import java.io.*;


public class ShowKnowledgeCardController extends Controller{
    private com.typesafe.config.Config config;

    @Inject
    public ShowKnowledgeCardController(Config config) {
        this.config = config;
    }
    public Result getCard(String title) throws Exception{

        title = URLDecoder.decode(title, "UTF-8");
        String query1 = "match (p:Paper) where p.title = '" + title + "' return p";
        String query2 = "MATCH (p1:Paper)-[r:REFERS_TO]->(p2:Paper) WHERE p1.title = '" +title + "' RETURN p2.title ";
//3D Medical Volume Reconstruction Using Web Services.
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query1 );
                }
            } );
            List<String> citations =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchCitationsNodes( tx, query2 );
                }
            } );
            System.out.println(citations);
            JsonArray jArray = new JsonArray();
            for(String c: citations)
            {
                JsonPrimitive element = new JsonPrimitive(c);
                jArray.add(element);
            }

            //System.out.println(jArray);
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(papers.get(0)).getAsJsonObject();



            obj.add("citations",jArray);
            System.out.println(obj.toString());
            //System.out.println("gson :" + gson);
            return ok(new Gson().toJson(obj)).as("applications/json");
            //return ok(papers.get(0).toString()).as("applications/json");
        }
    }

    private static List<String> matchPaperNodes(Transaction tx, String query)
    {
        List<String> metaDatas = new ArrayList<>();
        StatementResult result = tx.run( query );
        Gson gson = new Gson();
        while ( result.hasNext() )
        {

            Record record = result.next();
            String recordString = gson.toJson(record.asMap());

            metaDatas.add(recordString);
        }
        return metaDatas;
    }

    private static List<String> matchCitationsNodes(Transaction tx, String query)
    {
        List<String> metaDatas = new ArrayList<>();
        StatementResult result = tx.run( query );

        while ( result.hasNext() )
        {

            Record record = result.next();
            metaDatas.add(record.get(0).asString());
        }
        return metaDatas;
    }
    public static void main(String[] args) {

    }
}
