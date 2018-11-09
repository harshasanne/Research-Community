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
        username = "Jia Zhang";
        System.out.println(username);
        System.out.println(Constants.getFollowingNewsURL + URLEncoder.encode(username, "UTF-8"));

        JsonNode nodes = apiCall.callAPI(Constants.getFollowingNewsURL + URLEncoder.encode(username, "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
        List<Publication> news = new ArrayList<Publication>();
        try {
            for (int i=0; i<nodes.size(); i++) {
                Map<String, String> map = reader.readValue(nodes.get(i));
                String author = map.get("author");
                String title = map.get("title");
                String abstract_ = map.get("abstract_");
                String journal = map.get("journal");
                String year = map.get("year");
                Publication p = new Publication(author, title, abstract_, journal, year);
                news.add(p);
            }
        }
        catch (Exception e) {
        }

        List<Publication> publicationList = new ArrayList<Publication>();
        nodes = apiCall.callAPI(Constants.getAuthorPapersURL + URLEncoder.encode(username, "UTF-8"));
        mapper = new ObjectMapper();
        reader = mapper.readerFor(new TypeReference<Map<String, String>>() {});
//        List<String>
//        try {
//            for (int i=0; i<nodes.size(); i++) {
//                Map<String, String> tmp = reader.readValue(nodes.get(i));
//                news.add(tmp);
//                System.out.println(tmp.get("author"));
//            }
//        }
//        catch (Exception e) {
//        }
//
//        String jstring = nodes.toString();
//        System.out.println(jstring);
//        List<Publication> publicationList = new ArrayList<Publication>();
//        if(nodes != null) {
//            for (int i = 0; i < nodes.size(); i++) {
//                String author = nodes.get(i).findPath("author").asText();
//                String title = nodes.get(i).findPath("title").asText();
//                String abstract_ = nodes.get(i).findPath("abstract_").asText();
//                String journal = nodes.get(i).findPath("journal").asText();
//                String year = nodes.get(i).findPath("year").asText();
//                publicationList.add(new Publication(author, title, abstract_, journal, year));
//            }
//        }
return ok();
        //return ok(views.html.dashboard.render(username, news, publications));
    }
}