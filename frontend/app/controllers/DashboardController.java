package controllers;

import models.Publication;
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
import static play.mvc.Controller.session;
import util.Constants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class DashboardController extends Controller {

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

    public Result getMyProfile() throws Exception {

        String username = session("username");
        username = username == null ? "" : username;
        username = "cxy";
        System.out.println(username);
        System.out.println(Constants.getFollowingNewsURL + URLEncoder.encode(username, "UTF-8"));

        JsonNode nodes = apiCall.callAPI(Constants.getFollowingNewsURL + URLEncoder.encode(username, "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
        List<Publication> newsList = new ArrayList<Publication>();
        try {
            for (int i=0; i<nodes.size(); i++) {
                Map<String, String> map = reader.readValue(nodes.get(i));
                String author = map.get("author");
                String title = map.get("title");
                String abstract_ = map.get("abstract_");
                String journal = map.get("journal");
                String year = map.get("year");
                Publication p = new Publication(author, title, abstract_, journal, year);
                newsList.add(p);
            }
        }
        catch (Exception e) {
        }

        nodes = apiCall.callAPI(Constants.getAuthorPapersURL + URLEncoder.encode(username, "UTF-8"));
        mapper = new ObjectMapper();
        reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
        List<String> publicationList = new ArrayList<String>();
        try {
            for (int i=0; i<nodes.size(); i++) {
                Map<String, String> map = reader.readValue(nodes.get(i));
                publicationList.add(map.get("title"));
            }
        }
        catch (Exception e) {
        }

        return ok(views.html.dashboard.render(username, newsList, publicationList));
    }
}