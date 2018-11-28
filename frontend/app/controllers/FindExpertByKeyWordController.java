package controllers;


import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import models.Keyword;
import models.KeywordNetwork;
import play.mvc.*;
import util.APICall;
import util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;


public class FindExpertByKeyWordController extends Controller {

    private APICall apiCall;
    @Inject
    public FindExpertByKeyWordController(APICall apiCall) {
        this.apiCall = apiCall;
    }


    public Result getExpert(String keyword) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/expertByKeyword" + "/" + URLEncoder.encode(keyword, "UTF-8"));

        String name="";
        List<Keyword> keywordList = new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            name = nodes.get(i).findPath("name").asText();
        }
        return ok(views.html.expertByKeyword.render(name,keyword));
    }

    public Result getNetwork(String keyword) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/networkByKeyword/" + URLEncoder.encode(keyword, "UTF-8"));
        List<KeywordNetwork> networks = new ArrayList<KeywordNetwork>();
        for (int i = 0; i < nodes.size(); i++) {
            JsonNode node = nodes.get(i);
            networks.add(new KeywordNetwork(node.findPath("title").asText(), node.findPath("authors").asText()));
        }

        return ok(views.html.keywordNetwork.render(networks));
    }

    public Result getKeywordNetworkForm() throws Exception {
        return ok(views.html.keywordNetworkInput.render("networkByKeyword", "Find network among keywords"));
    }
    public Result getForm() throws Exception {

        return ok(views.html.singleInput.render("expertByKeyword","Keyword","Find Expert for a keyord"));
    }
}
