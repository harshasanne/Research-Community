package controllers;


import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import models.Keyword;
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
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/expertByKeyword" + "/" + URLEncoder.encode(keyword, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI

        String name="";
        List<Keyword> keywordList = new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            name = nodes.get(i).findPath("name").asText();
        }
        return ok(views.html.expertByKeyword.render(name,keyword));
    }


    public Result getForm() throws Exception {

        return ok(views.html.singleInput.render("expertByKeyword","Keyword","Find Expert for a keyord"));
    }
}
