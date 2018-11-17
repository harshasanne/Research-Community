package controllers;

import models.PaperLocation;
import models.AuthorLocation;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.*;
import java.net.URLDecoder;

import com.google.gson.Gson;

import play.data.DynamicForm;
import play.data.FormFactory;

import utils.DBDriver;
import javax.inject.Inject;
import com.typesafe.config.Config;

public class MapController extends Controller {

    private FormFactory formFactory;
    private Config config;

    @Inject
    public MapController(FormFactory formFactory, Config config) {
        this.formFactory = formFactory;
        this.config = config;
    }

    public Result getAuthorLocations(String country, String keyword) throws Exception {
        country = URLDecoder.decode(country, "UTF-8");
        keyword = URLDecoder.decode(keyword, "UTF-8");
        String query  = "MATCH (a:Author)-[:WRITES]->(p:Paper)\n" +
                "WHERE p.abstract =~ '.*" + keyword + ".*' AND a.country = '" + country + "'\n" +
                "RETURN a.authorName, a.address limit 10";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<AuthorLocation> papers =  session.readTransaction( new TransactionWork<List<AuthorLocation>>()
            {
                @Override
                public List<AuthorLocation> execute( Transaction tx )
                {
                    return findAuthors(tx, query);
                }
            } );
            return ok(new Gson().toJson(papers));
        }
    }

    private static List<AuthorLocation> findAuthors(Transaction tx, String query) {
        List<AuthorLocation> authors = new ArrayList<AuthorLocation>();
        StatementResult result = tx.run(query);
        while (result.hasNext()) {
            Record t = result.next();
            authors.add(new AuthorLocation(t.get(0).asString(), t.get(1).asString()));
        }
        return authors;
    }

    public Result getPaperLocations(String conference, int startYear, int endYear) throws Exception {
        conference = URLDecoder.decode(conference, "UTF-8");
        Driver driver = DBDriver.getDriver(this.config);

        List<PaperLocation> locations = new ArrayList<PaperLocation>();
        for(int i = startYear; i <= endYear; i++) {
            String query = "MATCH (p:Paper)\n" +
                    "WHERE p.journal = '" + conference + "' and p.year='" + String.valueOf(i) + "'\n" +
                    "RETURN p.title, p.year, p.address\n" +
                    "LIMIT 10";
            System.out.println(query);
            try ( Session session = driver.session() )
            {
                List<PaperLocation> papers = session.readTransaction(new TransactionWork<List<PaperLocation>>()
                {
                    @Override
                    public List<PaperLocation> execute( Transaction tx )
                    {
                        return findPapers( tx, query );
                    }
                } );
                locations.addAll(papers);

            }
        }
        return ok(new Gson().toJson(locations));
    }

    private static List<PaperLocation> findPapers(Transaction tx, String query) {
        List<PaperLocation> papers = new ArrayList<PaperLocation>();
        StatementResult result = tx.run(query);
        while (result.hasNext()) {
            Record t = result.next();
            papers.add(new PaperLocation(t.get(0).asString(), t.get(1).asString(), t.get(2).asString()));
        }
        return papers;
    }
}
