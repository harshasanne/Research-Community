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
        System.out.println(nodes);

        return ok(views.html.top20.render(nodes));
    }
}
