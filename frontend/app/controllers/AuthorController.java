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
import views.html.paperYear;
import models.*;

import java.util.concurrent.CompletionStage;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;
import play.libs.ws.*;
import com.google.gson.Gson;

import play.data.DynamicForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthorController extends Controller {
    private APICall apiCall;
    private final FormFactory formFactory;
    private final WSClient ws;
    private final HttpExecutionContext ec;

    @Inject
    public AuthorController(FormFactory formFactory, WSClient ws, HttpExecutionContext ec, APICall apiCall) {
        this.formFactory = formFactory;
        this.ws = ws;
        this.ec = ec;
        this.apiCall = apiCall;
    }

    public Result getPapers(String name) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/author" + "/" + URLEncoder.encode(name, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI
        String p = new String();
        List<String> paperList = new ArrayList<String>();
        if(nodes != null){
        for(int i = 0;i < nodes.size();i++){
           p = nodes.get(i).findPath("title").asText();
           paperList.add(p);
        }
    }
        return ok();
    }
    public Result collaboratorForm() {
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.collaborationForm.render(paperForm));
    }

    public Result getCollaborators() throws Exception {
        Form<Paper> paperForm = formFactory.form(Paper.class).bindFromRequest();
        String name = paperForm.get().journalName.replace(" ", "%20");
        JsonNode p = apiCall.callAPI(Constants.BACKEND + "/collaborator" + "/" + name);
        JsonNode n = p.get("data");
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Map<String, List> map = new HashMap<>();
        System.out.println(p+"...............");
        for (JsonNode edge : n.get("relationships")) {
            edges.add(new Edge(edge.get("startNode").asLong(), edge.get("endNode").asLong()));
        }
        for (JsonNode node : n.get("nodes")) {
            JsonNode properties = node.get("properties");
            nodes.add(new Node(properties.get("authorName").asText(), properties.get("index").asLong()));
        }
        map.put("nodes", nodes);
        map.put("edges", edges);

        String d  = Json.toJson(map).toString();
        

        return ok(views.html.author.render(d));
    }

    public Result getPath(String source, String destination) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/path/" + URLEncoder.encode(source, "UTF-8")
            + "/" + URLEncoder.encode(destination, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            sb.append(nodes.get(i).findPath("name").asText());
            if (i != nodes.size() -  1) {
                sb.append("<->");
            }
        }

        return ok(views.html.shortestPath.render(sb.toString(), source, destination));
    }

    public Result getPathForm() throws Exception {
        return ok(views.html.shortestPathInput.render("path", "Find shortest path between authors"));
    }

    public Result publicationPerYear() {
        Form<Author> authorForm = formFactory.form(Author.class);
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.paperYear.render(authorForm,paperForm));
    }
      public Result getPapersYear() throws Exception {
        Form<Author> authName = formFactory.form(Author.class).bindFromRequest();
        Paper paperForm = formFactory.form(Paper.class).bindFromRequest().get();
        String authorName = authName.get().getName().replace(" ", "%20");
       //System.out.println(paperForm.startYear+"........");        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/authorName" + "/" + authorName+"?from="+paperForm.startYear+"&to="+paperForm.endYear);
    
        return redirect(routes.HomeController.index());
    }

     public Result journalAuthors() {
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.journalHistogram.render(paperForm));
    }
      public Result getJournalAuthors() throws Exception {
        Form<Paper> paperForm = formFactory.form(Paper.class).bindFromRequest();
        String name = paperForm.get().journalName.replace(" ", "%20");
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/journalName" + "/" + name);
    
        return ok(views.html.successDownload.render());
    }
    public Result researcherNetwork() {
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.researcherForm.render(paperForm));
    }
      public Result getResearcherNetwork() throws Exception {
        Form<Paper> paperForm = formFactory.form(Paper.class).bindFromRequest();
        String name = paperForm.get().journalName.replace(" ", "%20");
        JsonNode p = apiCall.callAPI(Constants.BACKEND + "/collaborator" + "/" + name);
        JsonNode n = p.get("data");
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Map<String, List> map = new HashMap<>();
        System.out.println(n.get("relationships")+"...............");
        for (JsonNode edge : n.get("relationships")) {
            edges.add(new Edge(edge.get("startNode").asLong(), edge.get("endNode").asLong()));
        }
        for (JsonNode node : n.get("nodes")) {
            JsonNode properties = node.get("properties");
            nodes.add(new Node(properties.get("authorName").asText(), properties.get("index").asLong()));
        }
        map.put("nodes", nodes);
        map.put("edges", edges);

        String d  = Json.toJson(map).toString();
        

        return ok(views.html.researchesNetwork.render(d));
    }
     public Result statsFollowers() {
        Form<Author> authorForm = formFactory.form(Author.class);
        return ok(views.html.statsFollowers.render(authorForm));
    }
      public Result getstatsFollowers() throws Exception {
        Form<Author> paperForm = formFactory.form(Author.class).bindFromRequest();
        String name =paperForm.get().getName();
        System.out.println(name);
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/stats" + "/" + URLEncoder.encode(name, "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
        List<Statistics> newsList = new ArrayList<Statistics>();
        try {
            for (int i=0; i<nodes.size(); i++) {
                Map<String, String> map = reader.readValue(nodes.get(i));
                String followerName = map.get("followerName");
                String numberOfPapers = map.get("numberOfPapers");
                String numberOfKeywords = map.get("numberOfKeywords");
                Statistics p = new Statistics(followerName, numberOfPapers, numberOfKeywords);
                newsList.add(p);
            }
        }
        catch (Exception e) {
        }

        System.out.println(nodes);

        return ok(views.html.followerDetails.render(newsList));
    }
    public Result getstatsUserFollowers() throws Exception {
        String name = session("username");
        System.out.println(name);
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/stats" + "/" + URLEncoder.encode(name, "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
        List<Statistics> newsList = new ArrayList<Statistics>();
        try {
            for (int i=0; i<nodes.size(); i++) {
                Map<String, String> map = reader.readValue(nodes.get(i));
                String followerName = map.get("followerName");
                String numberOfPapers = map.get("numberOfPapers");
                String numberOfKeywords = map.get("numberOfKeywords");
                Statistics p = new Statistics(followerName, numberOfPapers, numberOfKeywords);
                newsList.add(p);
            }
        }
        catch (Exception e) {
        }
        System.out.println(nodes);
        return ok(views.html.followerDetails.render(newsList));
    }
    public Result getAuthorForm() {
        Form<Author> authorForm = formFactory.form(Author.class);
        return ok(views.html.getAuthorForm.render(authorForm));
    }


    public Result getAuthor() throws Exception {
        String username = session("username");
        
        Form<Author> paperForm = formFactory.form(Author.class).bindFromRequest();
        String name = paperForm.get().getName();
        Author author = new Author();
        author.setName(name);
        System.out.println(name);
        JsonNode nodes = apiCall.callAPI(Constants.getAuthorPapersURL + URLEncoder.encode(name, "UTF-8"));
        String jstring = nodes.toString();
        List<String> paperList = new ArrayList<String>();
        if(nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {

                String p = nodes.get(i).findPath("title").asText();

                paperList.add(p);
            }
        }

        nodes = apiCall.callAPI(Constants.getAllFollowingsURL + URLEncoder.encode(username, "UTF-8"));
        Set<String> followings = new HashSet<String>();
        if(nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                String authorName = nodes.get(i).findPath("name").asText();
                followings.add(authorName);
            }
        }
        boolean alreadyFollowed = followings.contains(name);

        return ok(views.html.profile.render(author, username, alreadyFollowed, paperList));
    }

    public CompletionStage<Result> follow() {
        DynamicForm requestData = formFactory.form().bindFromRequest();
        System.out.println("test" + requestData.get("username"));

        String username = requestData.get("username");
        String name = requestData.get("author");

        Map<String, String> info = new HashMap<String, String>();
        info.put("author", name);
        info.put("follower", username);
        String apiString = Constants.followURL;

        return ws.url(apiString)
            .addHeader("Content-Type", "application/json")
            .post(Json.toJson(info))
            .thenApplyAsync((WSResponse r) -> {
                //return redirect(routes.AuthorController.getAuthor(name));
                return ok();
            }, ec.current());
    }

    public CompletionStage<Result> unfollow() {
        DynamicForm requestData = formFactory.form().bindFromRequest();
        System.out.println("test" + requestData.get("username"));

        String username = requestData.get("username");
        String name = requestData.get("author");

        Map<String, String> info = new HashMap<String, String>();
        info.put("author", name);
        info.put("follower", username);
        String apiString = Constants.unfollowURL;

        return ws.url(apiString)
            .addHeader("Content-Type", "application/json")
            .post(Json.toJson(info))
            .thenApplyAsync((WSResponse r) -> {
                //return redirect(routes.AuthorController.getAuthor(name));
                return ok();
            }, ec.current());
    }
}
