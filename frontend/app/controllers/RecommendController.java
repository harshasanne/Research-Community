package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Keyword;
import play.mvc.Controller;
import play.mvc.Result;
import util.APICall;
import util.Constants;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RecommendController extends Controller {
    private APICall apiCall;
    @Inject
    public RecommendController(APICall apiCall) {
        this.apiCall = apiCall;
    }

    public Result getForm(){
        return ok(views.html.singleInput.render("recommend","Keyword","Paper recommendation"));
    }


    public Result getRecommend(String keyword) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/recommend" +
                "/" + URLEncoder.encode(keyword, "UTF-8")+
                "/" + URLEncoder.encode(session().get("username"),"UTF-8")
        );

        String title="";
        List<String> titleList = new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            title = nodes.get(i).asText();
            titleList.add(title);
        }
        return ok(views.html.recommendation.render(titleList,keyword));
    }
}
