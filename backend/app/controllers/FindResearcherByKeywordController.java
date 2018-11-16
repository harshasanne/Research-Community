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

public class FindResearcherByKeywordController extends Controller{
    private Config config;

    @Inject
    public FindResearcherByKeywordController(Config config) {
        this.config = config;
    }

    public Result getResearcher(String keywords) throws Exception{
        keywords = URLDecoder.decode(keywords, "UTF-8");

//
//        String[] keyList= keywords.split(",");
//        ArrayList<String> finalKeywordList = new ArrayList();
//        for(String k:keyList)
//        {
//            finalKeywordList.add(k);
//        }

        String query = "MATCH(a:Author)-[r:HAS_KEYWORD]->(k:Keyword) " + "WHERE a.RI =~ '.*"+keywords+".*' or k='" + keywords +
                "' RETURN a.authorName, count(a.authorName) as c\n" +
                "ORDER BY c desc\n" +
                "limit 10";
        System.out.println(query);
//        Map<String, List<String>> map = new HashMap<>();
//        map.put("finalList", finalKeywordList);
//        org.neo4j.ogm.model.Result result = DBConnector.session().query(query, map);
//        Iterator<Map<String, Object>> keywordIterator = result.iterator();
//        while (keywordIterator.hasNext()) {
//            String author = keywordIterator.next().get("author").toString();
//            authors.add(author);
//        }



//3D Medical Volume Reconstruction Using Web Services.
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return findResearcher( tx, query );
                }
            } );
            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }
            System.out.println(authorObjects.get(0));
            return ok(new Gson().toJson(authorObjects));
        }
    }

    private static List<String> findResearcher(Transaction tx, String query)
    {
        List<String> authors = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            authors.add(result.next().get(0).asString());
        }
        return authors;
    }
}
