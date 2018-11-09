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

import play.data.DynamicForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;


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
        return ok(views.html.author.render(paperList));
    }

    public Result getCollaborators(String name) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/collaborator" + "/" +URLEncoder.encode(name, "UTF-8"));
        List<String> collaborators = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++) {
            collaborators.add(nodes.get(i).findPath("name").asText());
        }

        //TODO: Harsha, change the return type however suitable for your graph
        return ok(views.html.author.render(collaborators));
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
     public Result statsFollowers() {
        Form<Author> authorForm = formFactory.form(Author.class);
        return ok(views.html.statsFollowers.render(authorForm));
    }
      public Result getstatsFollowers() throws Exception {
        Form<Author> paperForm = formFactory.form(Author.class).bindFromRequest();
        String name = paperForm.get().getName().replace(" ", "%20");
        System.out.println(name+"herheehrhe");
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/stats" + "/" + name);
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
        List<Statistics> newsList = new ArrayList<Statistics>();
        try {
            for (int i=0; i<nodes.size(); i++) {
                Map<String, String> map = reader.readValue(nodes.get(i));
                String followerName = map.get("followerName");
                String numberOfFollowers = map.get("numberOfFollowers");
                String numberOfPapers = map.get("numberOfPapers");
                String numberOfKeywords = map.get("numberOfKeywords");
                String Keywords = map.get("Keywords");
                Statistics p = new Statistics(followerName, numberOfFollowers, numberOfPapers, numberOfKeywords, Keywords);
                newsList.add(p);
            }
        }
        catch (Exception e) {
        }
        System.out.println(nodes);
        System.out.println(nodes.get(0).findPath("followerName").asText()+"hereherehere");
        
        return ok(views.html.followerDetails.render(newsList));
    }
    public Result getAuthorForm() {
        Form<Author> authorForm = formFactory.form(Author.class);
        return ok(views.html.getAuthorForm.render(authorForm));
    }


    public Result getAuthor() throws Exception {
        String username = "cxy"; //TODO
        
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
