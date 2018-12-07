package controllers;

import com.google.gson.*;
import com.typesafe.config.Config;
import models.TimePeriod;
import models.User;
import org.neo4j.driver.v1.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DBDriver;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class PaperCategoriesController extends Controller {

    private com.typesafe.config.Config config;

    @Inject

    private FormFactory formFactory;

    @Inject
    public PaperCategoriesController(FormFactory formFactory, Config config) {
        this.formFactory = formFactory;
        this.config = config;
    }
    public Result showCategories(String startYear, String endYear) throws Exception{



        String query = "match(p:Paper) where p.year >= '" + startYear + "' and p.year <= '" + endYear + "' return p";
System.out.println(query);
//3D Medical Volume Reconstruction Using Web Services.
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction(new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes1( tx, query );
                }
            } );


            return ok(papers.toString()).as("applications/json");

        }
    }
    public Result multiParamsCategories(String startYear, String endYear, String channels, String keywords) throws Exception{



        String[] tempList= keywords.split(",");
        ArrayList<String> keyList = new ArrayList();
        for(String k:tempList)
        {
            keyList.add(k);
        }


        String query = "MATCH(p:Paper)-[:HAS_KEYWORD]->(k:Keyword) where p.year >= '" + startYear + "' and p.year <= '" + endYear + "' and p.journal = '" + channels +
                "' and k.keyword IN {keyList}  return p";
        System.out.println(query);
//3D Medical Volume Reconstruction Using Web Services.
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction(new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes2( tx, query, "keyList", keyList);
                }
            } );


            return ok(papers.toString()).as("applications/json");

        }
    }



    private static List<String> matchPaperNodes1(Transaction tx, String query)
    {
        List<String> metaDatas = new ArrayList<>();
        StatementResult result = tx.run( query);

        Gson gson = new Gson();
        while ( result.hasNext() )
        {

            Record record = result.next();
            String recordString = gson.toJson(record.asMap());

            metaDatas.add(recordString);
        }
        return metaDatas;
    }

    private static List<String> matchPaperNodes2(Transaction tx, String query, String paramsName, ArrayList keyList)
    {
        List<String> metaDatas = new ArrayList<>();
        StatementResult result = tx.run( query,parameters( paramsName, keyList));

        Gson gson = new Gson();
        while ( result.hasNext() )
        {

            Record record = result.next();
            String recordString = gson.toJson(record.asMap());

            metaDatas.add(recordString);
        }
        return metaDatas;
    }
}
