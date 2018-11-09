package controllers;

import com.google.gson.Gson;
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

            System.out.println(authors.get(0).toString());
            return ok(authors.get(0).toString()).as("applications/json");
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

}
