package controllers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import com.typesafe.config.Config;
import org.neo4j.driver.v1.*;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DBDriver;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ShowAuthorCardController extends Controller {
    private com.typesafe.config.Config config;

    @Inject
    public ShowAuthorCardController(Config config) {
        this.config = config;
    }
    public Result getCard(String name) throws Exception{

        name = URLDecoder.decode(name, "UTF-8");
        String query1 = "match (a:Author) where a.authorName = '" + name + "' return a";
        System.out.println(query1);
        String query2 = "MATCH(a:Author{authorName:'" + name +"'})-[:WRITES]->(p:Paper) RETURN p.title";
        System.out.println(query2);
//3D Medical Volume Reconstruction Using Web Services.
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction(new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchAuthorNodes( tx, query1 );
                }

            } );
            List<String> papers =  session.readTransaction(new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query2 );
                }

            } );
           // System.out.println(papers);
            JsonArray jArray = new JsonArray();
            for(String p: papers)
            {
                JsonPrimitive element = new JsonPrimitive(p);
                jArray.add(element);
            }

           // System.out.println("jarray:" + jArray);
            JsonParser parser = new JsonParser();
            JsonObject obj = null;
            if(authors.size() > 0) {
                obj = parser.parse(authors.get(0)).getAsJsonObject();
                obj.add("publications", jArray);
            }
            //System.out.println(obj.toString());
            System.out.println("jarray:" + jArray);
            //System.out.println("gson :" + gson);
            return ok(new Gson().toJson(obj)).as("applications/json");
        }
    }

    private static List<String> matchAuthorNodes(Transaction tx, String query)
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

    private static List<String> matchPaperNodes(Transaction tx, String query)
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

}
