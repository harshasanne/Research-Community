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
import models.*;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.*;
import play.libs.Json;
import java.util.concurrent.CompletionStage;

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
        Form<Author> authorForm = formFactory.form(Author.class);
        Form<Paper> paperForm = formFactory.form(Paper.class);
        return ok(views.html.paper.render(authorForm, paperForm));
    }

    public CompletionStage<Result> postPaper() {
        String apiString = Constants.postPaperURL;
        Author authorInfo = formFactory.form(Author.class).bindFromRequest().get();
        Paper paperInfo = formFactory.form(Paper.class).bindFromRequest().get();

        Map<String, String> info = new HashMap<String, String>();
        info.put("author", authorInfo.getName());
        info.put("title", paperInfo.getTitle());
        info.put("year", String.valueOf(paperInfo.getstartYear()));
        info.put("journal", paperInfo.getjournalName());
        info.put("abstract", paperInfo.getAbstract());

        return ws.url(apiString)
                .addHeader("Content-Type", "application/json")
                .post(Json.toJson(info))
                .thenApplyAsync((WSResponse r) -> {
                    return ok(views.html.index.render());
                }, ec.current());
    }
}
