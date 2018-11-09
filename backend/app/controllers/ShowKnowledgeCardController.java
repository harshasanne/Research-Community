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


            return ok(papers.get(0).toString()).as("applications/json");
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
    public static void main(String[] args) {

    }
}
