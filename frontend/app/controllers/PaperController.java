package controllers;

import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;
import util.APICall;
import util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import play.data.Form;
import play.data.FormFactory;
import java.net.URLEncoder;
import views.html.paper;
import forms.PaperForm;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.*;
import play.libs.Json;
import java.util.concurrent.CompletionStage;
import models.*;

public class PaperController extends Controller {
    private APICall apiCall;
    private final FormFactory formFactory;
    private final WSClient ws;
    private final HttpExecutionContext ec;

    @Inject
    public PaperController(FormFactory formFactory, WSClient ws, HttpExecutionContext ec, APICall apiCall) {
        this.formFactory = formFactory;
        this.ws = ws;
        this.ec = ec;
        this.apiCall = apiCall;
    }

    public Result getForm() {
        Form<PaperForm> paperForm = formFactory.form(PaperForm.class);
        return ok(views.html.paper.render(paperForm));
    }

    public CompletionStage<Result> postPaper() {
        String apiString = Constants.postPaperURL;
        PaperForm paperInfo = formFactory.form(PaperForm.class).bindFromRequest().get();

        Map<String, String> info = new HashMap<String, String>();
        info.put("author", paperInfo.getAuthor());
        info.put("title", paperInfo.getTitle());
        info.put("abstract_", paperInfo.getAbstract());
        info.put("journal", paperInfo.getJournal());
        info.put("year", String.valueOf(paperInfo.getYear()));
        System.out.println("test from frontend" + info.get("title"));
        System.out.println(apiString);

        return ws.url(apiString)
                .addHeader("Content-Type", "application/json")
                .post(Json.toJson(info))
                .thenApplyAsync((WSResponse r) -> {
                    return ok(views.html.index.render(session().get("username"), ""));
                }, ec.current());
    }
    public Result getTop20Papers() throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/top20" + "/" );
        String d = nodes.get("data").asText();
        List<String> expertsList = new ArrayList<>();
        List<String> expertsList1 = new ArrayList<>();
        String name;
        Integer count;
        String year;
        List<Evolution> citeList=new ArrayList<>();
        for (JsonNode data : nodes.get("data")) {
            year = data.get("journal").asText();
            List<Keyword> keywordList = new ArrayList<>();
            for (JsonNode cite : data.get("citaion")) {
                name = cite.get("title").asText();
                count = cite.get("citation").asInt();
                keywordList.add(new Keyword(name,count));
            }
            citeList.add(new Evolution(year,keywordList));
        }

        return ok(views.html.top20.render(citeList));
    }
    public Result formTop20PapersWithYears() {
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.top20PapersForm.render(paperForm));
    }

    public Result getTop20PapersWithYears() throws Exception {
        Paper paperForm = formFactory.form(Paper.class).bindFromRequest().get();

        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/top20years" + "/"+paperForm.startYear
                +"/"+ paperForm.endYear );
        String name;
        Integer count;
        String year;
        String title;
        System.out.println(nodes);

        List<Citaion> citeList=new ArrayList<>();
        for (JsonNode data : nodes.get("data")) {
            title = data.get("Journal").asText();
            year = data.get("year").asText();
            List<Keyword> keywordList = new ArrayList<>();
            for (JsonNode cite : data.get("CitationData")) {
                name = cite.get("title").asText();
                count = cite.get("citationCount").asInt();
                keywordList.add(new Keyword(name,count));
            }
            citeList.add(new Citaion(title,year,keywordList));
        }


        return ok(views.html.top10.render(citeList));
    }
    public Result top_k_papers() throws Exception {
    Paper paperForm = formFactory.form(Paper.class).bindFromRequest().get();

        JsonNode p = apiCall.callAPI(Constants.BACKEND + "/getKrelated" + "/"+paperForm.title
                +"/"+ paperForm.journalName );
        JsonNode v = p.get("data");

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Map<String, List> map = new HashMap<>();

        System.out.println(v+"''''''''''''''''''''''''");
            for (JsonNode edge : v.get("relationships")) {
                edges.add(new Edge(edge.get("startNode").asLong(), edge.get("endNode").asLong()));
            }
            for (JsonNode node : v.get("nodes")) {
                JsonNode properties = node.get("properties");
                String label = node.get("labels").get(0).asText();
                if (label.equals("Author")) {
                    nodes.add(new Node(properties.get("authorName").asText(), node.get("id").asLong()));
                } else if (label.equals("Paper")) {
                    nodes.add(new Node(properties.get("title").asText(), node.get("id").asLong()));
                }
            }
            map.put("nodes", nodes);
            map.put("edges", edges);

            return ok(views.html.top_k_papers.render(Json.toJson(map).toString()));
        
    }
    public Result top_k_papers_form() {
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.top_k_papers_form.render(paperForm));
    }

    

}
