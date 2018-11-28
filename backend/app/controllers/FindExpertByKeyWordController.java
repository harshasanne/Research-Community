package controllers;

import models.Author;
import models.KeywordNetwork;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.*;
import java.net.URLDecoder;
import java.util.stream.Collectors;

import utils.DBDriver;
import javax.inject.Inject;
import com.typesafe.config.Config;

import com.google.gson.Gson;

public class FindExpertByKeyWordController extends Controller {
    private Config config;

    @Inject
    public FindExpertByKeyWordController(Config config) {
        this.config = config;
    }

    public Result getNetwork(String keyword) throws Exception {
        String[] keywords = URLDecoder.decode(keyword, "UTF-8").split(",");
        String withClause = Arrays.stream(keywords).map(s -> s.trim()).collect(Collectors.joining("', '"));
        withClause = "['" + withClause + "']";
        String getPaperQuery = "with " + withClause + " as words "
          + "match (p:Paper)-[:HAS_KEYWORD]->(k:Keyword) "
          + "where k.keyword in words "
          + "with p, size(words) as inputCnt, count(DISTINCT k) as cnt "
          + "where cnt = inputCnt return p.title limit 10";

        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> papers = session.readTransaction(new TransactionWork<List<String>>() {
                @Override
                public List<String> execute(Transaction transaction) {
                    return processResults(transaction, getPaperQuery);
                }
            });

            withClause = papers.stream().collect(Collectors.joining("', '"));
            withClause = "['" + withClause + "']";
            String getNetworkQuery = "with " + withClause + " as titles "
              + "match (a:Author)-[:WRITES]->(p:Paper) where p.title in titles return p.title, a.authorName";
            List<KeywordNetwork> networks =  session.readTransaction( new TransactionWork<List<KeywordNetwork>>()
            {
                @Override
                public List<KeywordNetwork> execute( Transaction tx )
                {
                    return findNetwork(tx, getNetworkQuery);
                }
            } );
            return ok(new Gson().toJson(networks));
        }
    }

    private static List<KeywordNetwork> findNetwork(Transaction tx, String query) {
        Map<String, List<String>> networks = new HashMap<String, List<String>>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            Record record = result.next();
            String title = record.get(0).asString();
            String author = record.get(1).asString();
            if (!networks.containsKey(title)) {
                networks.put(title, new ArrayList<String>());
            }

            networks.get(title).add(author);
        }

        List<KeywordNetwork> results = new ArrayList<KeywordNetwork>();
        for (String title : networks.keySet()) {
            List<String> authors = networks.get(title);
            List<String> distinctAuthors = new ArrayList<String>(new HashSet<String>(authors));
            String authorString = distinctAuthors.stream().collect(Collectors.joining(", "));
            results.add(new KeywordNetwork(title, authorString));
        }
        return results;
    }

    public Result getExpert(String keyword) throws Exception{
        keyword = URLDecoder.decode(keyword, "UTF-8");
        String query = "MATCH(a:Author)-[:WRITES]->(p:Paper)-[:HAS_KEYWORD]->" +
                "(k:Keyword{keyword:'"+ keyword + "'})" +
                "RETURN a.authorName, count(a.authorName) as c\n" +
                "ORDER BY c desc\n" +
                "limit 1";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return processResults( tx, query );
                }
            } );
            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }
            return ok(new Gson().toJson(authorObjects));
        }
    }

    private static List<String> processResults(Transaction tx, String query)
    {
        List<String> authors = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            authors.add(result.next().get(0).asString());
        }
        return authors;
    }
    public Result getTeam(String keyword) throws Exception{
        keyword = URLDecoder.decode(keyword, "UTF-8");
        String query = "MATCH(a:Author)-[:WRITES]->(p:Paper)-[:HAS_KEYWORD]->" +
                "(k:Keyword{keyword:'"+ keyword + "'})" +
                "RETURN a.authorName, count(a.authorName) as c\n" +
                "ORDER BY c desc\n" +
                "limit 10";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return processResults( tx, query );
                }
            } );
            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }
            return ok(new Gson().toJson(authorObjects));
        }
    }
}
