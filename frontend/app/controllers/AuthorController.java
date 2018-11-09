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

public class AuthorController extends Controller {
    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    public AuthorController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
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
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/stats" + "/" + name);
    
        return ok(views.html.successDownload.render());
    }

    public Result getAuthor(String name) throws Exception {
        Author author = new Author();
        author.setName("Jia Zhang");

        JsonNode nodes = apiCall.callAPI(Constants.getAuthorPapersURL + URLEncoder.encode(name, "UTF-8"));
        String jstring = nodes.toString();
        String p = new String();
        List<String> paperList = new ArrayList<String>();
        if(nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                p = nodes.get(i).findPath("title").asText();
                paperList.add(p);
            }
        }

        return ok(views.html.profile.render(author, paperList));
    }

    public Result addFollower() {
        return null;
    }

}
