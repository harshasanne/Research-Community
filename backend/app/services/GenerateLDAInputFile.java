package services;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.*;
import com.typesafe.config.Config;

import org.neo4j.driver.v1.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.data.FormFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


import utils.DBDriver;
import utils.loginUtils;

import models.*;


import javax.inject.Inject;
public class GenerateLDAInputFile {
    private com.typesafe.config.Config config;

    @Inject
    public GenerateLDAInputFile(Config config) {
        this.config = config;
    }
    public static void generate() throws Exception{
        String query = "MATCH(p:Paper) RETURN  p";
        //Driver driver = DBDriver.getDriver(this.config);
        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"));
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction(new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query );
                }

            } );

            JsonParser parser = new JsonParser();
            int i = 0;
            for(String p:papers)
            {
                System.out.println(i);
                i++;
                JsonObject obj = parser.parse(p).getAsJsonObject();

                String filename = obj.getAsJsonObject("p").getAsJsonObject("properties").getAsJsonObject("index").get("val").getAsString() + ".txt";
                System.out.println(filename);
                String title = obj.getAsJsonObject("p").getAsJsonObject("properties").getAsJsonObject("title").get("val").getAsString();
                String abst = "";
                if(obj.getAsJsonObject("p").getAsJsonObject("properties").has("abstract"))
                     abst = obj.getAsJsonObject("p").getAsJsonObject("properties").getAsJsonObject("abstract").get("val").getAsString();
                String content = title + abst;
                System.out.println(content);
                String path = "app/services/PapersLDAInputFiles/" + filename;
                File file = new File(path);

                try {
                    if(!file.exists())
                    {
                        file.createNewFile();



                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                //prepare the output file
                FileOutputStream os = new FileOutputStream(file);
                PrintStream ps = new PrintStream(os);

                ps.print(content);
                os.close();
                ps.close();
            }


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
        try {


            GenerateLDAInputFile.generate();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
