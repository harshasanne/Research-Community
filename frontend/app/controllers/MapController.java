package controllers;

import models.AuthorLocation;
import models.PaperLocation;
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

public class MapController extends Controller {

    private APICall apiCall;
    private final FormFactory formFactory;
    private final WSClient ws;
    private final HttpExecutionContext ec;

    @Inject
    public MapController(FormFactory formFactory, WSClient ws, HttpExecutionContext ec, APICall apiCall) {
        this.formFactory = formFactory;
        this.ws = ws;
        this.ec = ec;
        this.apiCall = apiCall;
    }

    public Result getAuthorForm() throws Exception {
        return ok(views.html.authorLocationFilter.render("map/author", "Author Location Distribution", "Country", "Keyword"));
    }

    public Result getAuthorLocations(String country, String keyword) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/map/author/" + URLEncoder.encode(country, "UTF-8")+
                "/" + URLEncoder.encode(keyword, "UTF-8"));

        List<AuthorLocation> locations = new ArrayList<AuthorLocation>();
        for(int i = 0;i < nodes.size(); i++){
            JsonNode node = nodes.get(i);
            String name = node.findPath("name").asText();
            String address = node.findPath("address").asText().replace(" ", "+");
            locations.add(new AuthorLocation(name, address));
        }
        return ok(views.html.authorLocationDisplay.render(locations));
    }


    public Result getPaperForm() throws Exception {
        return ok(views.html.paperLocationFilter.render("map/paper", "Paper Location Distribution", "Conference", "Start Year", "End Year"));
    }

    public Result getPaperLocations(String conference, int startYear, int endYear) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/map/paper/" + URLEncoder.encode(conference, "UTF-8") + "/" + startYear + "/" + endYear);

        List<PaperLocation> locations = new ArrayList<PaperLocation>();
        for(int i = 0;i < nodes.size(); i++){
            JsonNode node = nodes.get(i);
            String title = node.findPath("title").asText();
            String address = node.findPath("address").asText().replace(" ", "+");
            locations.add(new PaperLocation(title, address));
        }
        return ok(views.html.paperLocationDisplay.render(locations));
    }

    private long[] getCoordinates (String address) {
        final String credential = "AIzaSyCnmczszk6HGSsc4JPJD4nhQK8HUXMumcA";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + credential;
        System.out.println(url);
        JsonNode apiResult = apiCall.callAPI(url);
        System.out.println(apiResult.get(0).findPath("results").asText());
        System.out.println(apiResult.get(0).findPath("results").findPath("geometry").asText());
        System.out.println(apiResult.get(0).findPath("results").findPath("geometry").findPath("locaation").asText());
        long lat = apiResult.findPath("results").findPath("geometry").findPath("locaation").findPath("lat").longValue();
        long lng = apiResult.findPath("results").findPath("geometry").findPath("locaation").findPath("lng").longValue();
        return new long[] {lat, lng};
    }

}