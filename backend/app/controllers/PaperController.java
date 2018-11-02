package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.*;
import java.net.URLDecoder;

import com.google.gson.Gson;

public class PaperController extends Controller {

    private static DBConnector dbConnector;
    private static Driver driver;
    private static Session session;

    @Inject
    public PaperController() {
        dbConnector = DBConnector.getInstance();
        driver = dbConnector.getDriver();
        session = db.getSession();
    }

    public Result getPapers(String name) throws Exception{
        name = URLDecoder.decode(name, "UTF-8");
        String query = "match (a:Paper)-[:REFERS_TO]-(p:Paper) where a.title = '" + name + "' and p.title<>a.title return p.title";
        System.out.println(query);
        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
        try ( Session session = driver.session() )
        {
            List<String> papers =  session.readTransaction( new TransactionWork<List<String>>()
            {
                @Override
                public List<String> execute( Transaction tx )
                {
                    return matchPaperNodes( tx, query );
                }
            } );
            List<Paper> paperObjects = new ArrayList<Paper>();
            for (String p : papers) {
                paperObjects.add(new Paper(p));
            }
            return ok(new Gson().toJson(paperObjects));
        }
    }

    public Result createPaper() {
        Map<String, String[]> form = request().body().asFormUrlEncoded();

        String author = form.get("username");
        String title = form.get("title");
        String abstract_ = form.get("abstract");
        String journal = form.get("journal");
        String year = form.get("year");

        Paper paper = new Paper(title, abstract_, journal, year, author);
        Transaction transaction = session.beginTransaction();
        session.save(paper);
        transaction.commit();
        transaction.close();

        return ok();
    }


    private insert() {
        Transaction transaction = session.beginTransaction();
        int cnt = 0;
        for (Author author : authorMap.values()) {
            session.save(author, 1);
            cnt++;
            if (cnt % 500 == 0) {
                transaction.commit();
                transaction = session.beginTransaction();
                System.out.println("Write " + cnt + " authors.");
            }
        }
        transaction.commit();
        transaction.close();

        JsonNode requestJson = request().body().asJson();

        Session session = dbConnector.getSession();
        Filter filter = new Filter("username", ComparisonOperator.EQUALS, username);
        User user = session.loadAll(User.class, filter).iterator().next();
        user.removeAllInterests();

        for (JsonNode researchInterest : requestJson.findPath("researchInterests")) {
            String interest = researchInterest.toString().replaceAll("\"", "");
            filter = new Filter("keyword", ComparisonOperator.EQUALS, interest);
            Keyword keyword = session.loadAll(Keyword.class, filter).iterator().next();

            Transaction transaction = session.beginTransaction();
            user.hasInterestIn(keyword);
            session.save(user);
            transaction.commit();
            transaction.close();
        }
        return ok(Json.toJson(user));
    }

    private static List<String> matchPaperNodes(Transaction tx, String query)
    {
        List<String> papers = new ArrayList<>();
        StatementResult result = tx.run( query );
        while ( result.hasNext() )
        {
            papers.add(result.next().get(0).asString());
        }
        return papers;
    }
}
