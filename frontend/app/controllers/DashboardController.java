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

public class DashboardController {

    private APICall apiCall;
    private final FormFactory formFactory;
    private final WSClient ws;
    private final HttpExecutionContext ec;

    @Inject
    public DashboardController(FormFactory formFactory, WSClient ws, HttpExecutionContext ec, APICall apiCall) {
        this.formFactory = formFactory;
        this.ws = ws;
        this.ec = ec;
        this.apiCall = apiCall;
    }

    public Result getMyProfile() {
        JsonNode nodes = apiCall.callAPI(Constants.getFolloweePublicationsURL + URLEncoder.encode(name, "UTF-8"));
        String jstring = nodes.toString();
        String p = new String(), a = new String();
        List<String> paperList = new ArrayList<String>();
        List<String> authorList = new ArrayList<String>();
        if(nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                p = nodes.get(i).findPath("title").asText();
                paperList.add(p);
                a = nodes.get(i).findPath("author").asText();
                authorList.add(a);
            }
        }

        return ok(views.html.dashboard.render(paperList, authorList));
    }
}