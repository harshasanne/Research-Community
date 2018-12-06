package controllers;

import com.typesafe.config.Config;
import models.Author;
import models.Paper;
import models.PaperMeta;
import org.neo4j.driver.v1.*;

import play.mvc.*;

import java.util.*;
import java.net.URLDecoder;

import com.google.gson.*;
import utils.DBDriver;
import com.typesafe.config.Config;

import javax.inject.Inject;
import java.io.*;

import static org.neo4j.driver.v1.Values.parameters;

public class FindResearcherByKeywordController extends Controller{
    private Config config;

    @Inject
    public FindResearcherByKeywordController(Config config) {
        this.config = config;
    }

    public Result getResearcher(String keywords,String authorName) throws Exception{
        keywords = URLDecoder.decode(keywords, "UTF-8");
System.out.println("queryAuthorName:" + authorName);

        String[] tempList= keywords.split(",");
        ArrayList<String> keyList = new ArrayList();
        for(String k:tempList)
        {
            keyList.add(k);
        }


        String query0 = "MATCH(a:Author{authorName:'" + authorName +"'}) return a.RI";

        String query1 = "MATCH (author:Author)-[:WRITES]->(p:Paper)-[r:HAS_KEYWORD]->(key:Keyword) WHERE key.keyword IN {keyList} RETURN author.authorName as author,COUNT(p.title) as cnt  ORDER BY cnt DESC Limit 5";

        String query3 = "MATCH (author:Author)-[s:searched]->(sk:searchedKey) WHERE sk.keyword IN {keyList} RETURN author.authorName";
        String query4 = "MATCH (author:Author)-[s:searched]->(sk:searchedKey) WHERE author.authorName IN {keyAuthorList} RETURN sk.keyword, sk.cnt as cnt ORDER BY cnt DESC";

        System.out.println(query1);

        Map<String, Object> params = new HashMap<>();

        params.put("keyList", keyList);


//3D Medical Volume Reconstruction Using Web Services.
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> RIList =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findRI( tx, query0 );
                }
            } );

            ArrayList<String> newKeyList = new ArrayList<>();
            newKeyList.addAll(keyList);
            if(RIList.get(0)== null) {
                newKeyList.addAll(RIList);
            }
            Set<String> hsKey = new HashSet<>();
            hsKey.addAll(newKeyList);
            newKeyList.clear();
            newKeyList.addAll(hsKey);
            System.out.println("newKeyList: " + newKeyList);


            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findResearcher( tx, query1, "keyList",newKeyList );
                }
            } );
            System.out.println("reauthors:" + authors);

            for(String keyword: keyList) {
                String query2 = "MERGE (a:Author{authorName:'" + authorName + "'}) MERGE(sk:searchedKey{keyword:'" + keyword + "'}) ON CREATE SET sk.cnt = 1 ON MATCH SET sk.cnt = sk.cnt + 1 MERGE (a)-[s:searched]->(sk)    return sk.cnt";
                List<String> scounts = session.writeTransaction(new TransactionWork<List<String>>() {
                    @Override
                    public List<String> execute(Transaction tx) {
                        return mergeSearchedKey(tx, query2);
                    }
                });
            }


            List<String> keyAuthorList =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findKeyAuthor( tx, query3, "keyList",keyList );
                }
            } );
            System.out.println(keyAuthorList);



            Set<String> hs = new HashSet<>();
            hs.addAll(keyAuthorList);
            keyAuthorList.clear();
            keyAuthorList.addAll(hs);
            System.out.println(keyAuthorList);


            List<String> searchedKeys =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findSearchedKeys( tx, query4, "keyAuthorList",keyAuthorList );
                }
            } );
            System.out.println("sk:" + searchedKeys);

            Set<String> setKeys = new LinkedHashSet(searchedKeys);


            setKeys.removeAll(new HashSet<String>(newKeyList));

           // System.out.println(searchedKeys);

            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }


            JsonArray keysArray = new JsonArray();
            for(String k: setKeys)
            {
                JsonPrimitive element = new JsonPrimitive(k);
                keysArray.add(element);
            }

            JsonArray authorsArray = new JsonArray();
            for(String a: authors)
            {
                JsonPrimitive element = new JsonPrimitive(a);
                authorsArray.add(element);
            }


            //JsonParser parser = new JsonParser();
            JsonObject obj = new JsonObject();

            obj.add("searchedKeys", keysArray);
            obj.add("authorNames", authorsArray);



            System.out.println("jarray:" + keysArray);
            System.out.println("obj:" + obj);

            return ok(new Gson().toJson(obj)).as("applications/json");

        }
    }

    private static List<String> findRI(Transaction tx, String query)
    {
        List<String> RIs = new ArrayList<>();
        StatementResult result = tx.run( query);


        while ( result.hasNext() )
        {

            //System.out.println(result.next());
            String resString = result.next().get(0).asString();
            RIs.add(resString);
            //System.out.println(resString);
            //System.out.println(result.next().get(0).asString());
        }
        return RIs;
    }

    private static List<String> findResearcher(Transaction tx, String query, String paramsName, ArrayList keyList)
    {
        List<String> authors = new ArrayList<>();
        StatementResult result = tx.run( query,parameters( paramsName, keyList));


        while ( result.hasNext() )
        {

            //System.out.println(result.next());
            String resString = result.next().get(0).asString();
            authors.add(resString);
            //System.out.println(resString);
            //System.out.println(result.next().get(0).asString());
        }
        return authors;
    }

    private static List<String> findKeyAuthor(Transaction tx, String query, String paramsName, ArrayList keyList)
    {
        List<String> keyAuthor = new ArrayList<>();
        StatementResult result = tx.run( query,parameters( paramsName, keyList));


        while ( result.hasNext() )
        {

            //System.out.println(result.next());
            String resString = result.next().get(0).asString();
            keyAuthor.add(resString);
           // System.out.println(resString);
            //System.out.println(result.next().get(0).asString());
        }
        return keyAuthor;
    }

    private static List<String> mergeSearchedKey(Transaction tx, String query)
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

    private static List<String> findSearchedKeys(Transaction tx, String query, String paramsName, List keyList)
    {
        List<String> searchedKeys = new ArrayList<>();
        StatementResult result = tx.run( query,parameters( paramsName, keyList));


        while ( result.hasNext() )
        {


            String resString = result.next().get(0).asString();
            searchedKeys.add(resString);
           // System.out.println(resString);

        }
        return searchedKeys;
    }


}
