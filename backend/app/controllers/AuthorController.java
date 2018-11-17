package controllers;

import models.Author;
import models.Paper;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import utils.DBDriver;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.google.gson.Gson;
import com.typesafe.config.Config;

import javax.inject.Inject;
import java.io.*;
import java.util.concurrent.CompletionStage;
import services.Neo4jApiService;

public class AuthorController extends Controller {
    private Config config;
    private final Neo4jApiService neo4jApiService;


    @Inject
    public AuthorController(Neo4jApiService neo4jApiService,Config config) {
        this.config = config;
        this.neo4jApiService = neo4jApiService;
    }

    public Result getPapers(String name) throws Exception{
        System.out.println("reached the controller");
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Author)-[:WRITES]->(p:Paper) where a.authorName = '" + name + "' return p.title";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchNodes( tx, query );
                }
            } );
            List<Paper> paperObjects = new ArrayList<Paper>();
            for (String p : papers) {
                paperObjects.add(new Paper(p));
            }
            return ok(new Gson().toJson(paperObjects));
        }
    }

    public Result getPath(String source, String destination) throws Exception {
        source = URLDecoder.decode(source, "UTF-8");
        destination = URLDecoder.decode(destination, "UTF-8");
        String getPathQuery = "match (a:Author {authorName: '" + source + "'}), "
          + "(b:Author {authorName: '" + destination + "'}), "
          + "p = shortestPath((a)-[:COAUTHORS*]-(b)) return p;";
        Driver driver = DBDriver.getDriver(this.config);
        try ( Session session = driver.session() )
        {
            List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    List<String> values = new ArrayList<>();
                    StatementResult result = tx.run(getPathQuery);
                    while ( result.hasNext() )
                    {
                        Record record = result.next();
                        Path path = record.get(0).asPath();
                        boolean isStart = true;
                        for (Path.Segment seg : path) {
                            Node start = seg.start();
                            Node end = seg.end();
                            if (isStart) {
                                values.add(start.get("authorName").asString());
                                isStart = false;
                            }
                            values.add(end.get("authorName").asString());
                        }
                    }
                    return values;
                }
            } );

            List<Author> authorObjects = new ArrayList<Author>();
            for (String a : authors) {
                authorObjects.add(new Author(a));
            }
            return ok(new Gson().toJson(authorObjects));
        }
    }
    
    public CompletionStage<Result> getCollaborators(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (author:Author{authorName:'" + name + "'})-[coauth:CO_AUTHOR]-(coauthor) return collect(distinct(author)),collect(coauth),collect(distinct(coauthor))";

        return neo4jApiService.callNeo4jApi(query, true).thenApply((response) -> {
            return ok(response).as("application/json");
        });
    }
    // public Result getCollaborators(String name) throws Exception {
    //     name = URLDecoder.decode(name, "UTF-8");
    //     String query = "match (author:Author{authorName:'" + authorName + "'})-[coauth:CO_AUTHOR]-(coauthor) return collect(distinct(author)),collect(coauth),collect(distinct(coauthor))";

    //     Driver driver = DBDriver.getDriver(this.config);
    //     try ( Session session = driver.session() )
    //     {
    //         List<String> authors =  session.readTransaction( new TransactionWork<List<String>>()
    //         {
    //             @Override
    //             public List<String> execute( Transaction tx )
    //             {
    //                 return matchNodes( tx, query );
    //             }
    //         } );

    //         List<Author> authorObjects = new ArrayList<Author>();
    //         for (String a : authors) {
    //             authorObjects.add(new Author(a));
    //         }
    //         return ok(new Gson().toJson(authorObjects));
    //     }
    // }

    private static List<String> matchNodes(Transaction tx, String query)
    {
        List<String> values = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            values.add(result.next().get(0).asString());
        }
        return values;
    }
       
    
}
