package controllers;

import models.Paper;
import models.Author;
import org.neo4j.driver.v1.*;
import play.mvc.*;
import dbConnector.DBConnector;

import java.util.*;
import java.net.URLDecoder;

import com.google.gson.Gson;

import play.data.DynamicForm;
import play.data.FormFactory;

import utils.DBDriver;
import javax.inject.Inject;
import com.typesafe.config.Config;

public class FollowController extends Controller {

    private FormFactory formFactory;
    private Config config;

    @Inject
    public FollowController(FormFactory formFactory, Config config) {
        this.formFactory = formFactory;
        this.config = config;
    }

    public Result getNews(String name) throws Exception {
        name = URLDecoder.decode(name, "UTF-8");
        String query = "MATCH (user: Author {authorName: '"+ name + "'})-[:FOLLOWS]->(a: Author), (a: Author)-[:WRITES]->(p: Paper) RETURN a.authorName, p.title, p.abstract, p.journal, p.year ORDER BY p.year DESC LIMIT 10";
        System.out.println(query);
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<Paper> papers =  session.readTransaction( new TransactionWork<List<Paper>>()
            {
                @Override
                public List<Paper> execute( Transaction tx )
                {
                    return findRecentPaper( tx, query );
                }
            } );
            return ok(new Gson().toJson(papers));
        }
    }

    private static List<Paper> findRecentPaper(Transaction tx, String query)
    {
        List<Paper> papers = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            Record t = result.next();
            papers.add(new Paper(t.get(0).asString(), t.get(1).asString(), t.get(2).asString(), t.get(3).asString(), t.get(4).asString()));
        }
        return papers;
    }

    public Result getAllFollowers(String name) throws Exception {
        name = URLDecoder.decode(name, "UTF-8");
        String query = "MATCH (a: Author)-[:FOLLOWS]->(:Author {authorName: '" + name + "'}) RETURN a.authorName";
        System.out.println(query);
        return getAuthors(query);
    }

    public Result getAllFollowings(String name) throws Exception {
        name = URLDecoder.decode(name, "UTF-8");
        String query = "MATCH (: Author {authorName: '" + name + "'})-[:FOLLOWS]->(a: Author) RETURN a.authorName";
        System.out.println(query);
        return getAuthors(query);
    }

    private Result getAuthors(String query) {
        Driver driver = DBDriver.getDriver(this.config);

        try ( Session session = driver.session() )
        {
            List<Author> authors =  session.readTransaction( new TransactionWork<List<Author>>()
            {
                @Override
                public List<Author> execute( Transaction tx )
                {
                    return getAuthors( tx, query );
                }
            } );
            return ok(new Gson().toJson(authors));
        }
    }

    private static List<Author> getAuthors(Transaction tx, String query)
    {
        List<Author> authors = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            Record t = result.next();
            authors.add(new Author(t.get(0).asString()));
        }
        return authors;
    }

    public Result postFollowship() throws Exception {
        DynamicForm requestData = formFactory.form().bindFromRequest();
        System.out.println("test" + requestData.get("follower"));

        String follower = requestData.get("follower");
        String author = requestData.get("author");
        String query = "MERGE (a:Author {authorName:'" + author + "'})\n" +
                "MERGE (b:Author {authorName:'" + follower + "'})\n" +
                "ON CREATE SET a.authorName='" + author + "', b.authorauthorName='" + follower + "'\n" +
                "CREATE UNIQUE (a) <- [:FOLLOWS] - (b)";
        System.out.println(query);

        return modifyFollowship(query);
    }

    public Result postUnfollowship() throws Exception {
        DynamicForm requestData = formFactory.form().bindFromRequest();
        System.out.println("test" + requestData.get("follower"));

        String follower = requestData.get("follower");
        String author = requestData.get("author");
        String query = "MATCH (: Author {authorName: '" + follower + "'})-[f:FOLLOWS]->(: Author {authorName: '" + author + "'}) DELETE f;";
        System.out.println(query);

        return modifyFollowship(query);
    }

    private Result modifyFollowship(String query) throws Exception {
        Driver driver = DBDriver.getDriver(this.config);
        String res = null;
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>()
            {
                @Override
                public Void execute( Transaction tx )
                {
                    tx.run( query );
                    return null;
                }
            } );
            return ok();
        }
    }

}
