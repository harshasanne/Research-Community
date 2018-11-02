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

public class FindResearcherByKeywordController extends Controller{
    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    public FindResearcherByKeywordController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
    }

    public Result getResearcher(String keyword) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/ResearcherByKeyword" + "/" + URLEncoder.encode(keyword, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI
        //String jstring = nodes.toString();
        String name="";
        List<Keyword> keywordList = new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            name = nodes.get(i).findPath("name").asText();
        }
        return ok(views.html.researcherByKeyword.render(name,keyword));
    }
}

