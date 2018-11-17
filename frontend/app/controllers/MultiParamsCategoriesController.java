package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;
import util.APICall;
import util.Constants;
import views.html.simplePaperCategories;

import javax.inject.Inject;
import java.net.URLEncoder;

import static play.mvc.Results.ok;

public class MultiParamsCategoriesController {
    private APICall apiCall;
    @Inject
    public MultiParamsCategoriesController(APICall apiCall) {
        this.apiCall = apiCall;
    }

    public Result getForm() throws Exception {

        return ok(views.html.quadrupleInput.render("multiParamsCategories","Start Year"
                ,"End Year","channels","keywords","Get paper categories by time period"));
    }





    public Result showCategories (String startYear, String endYear,String channels, String keywords) throws  Exception
    {
        //System.out.println("herehrer");
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/multiParamsCategories" +
                "/" + URLEncoder.encode(startYear, "UTF-8")+
                "/" + URLEncoder.encode(endYear, "UTF-8")+
                "/" + URLEncoder.encode(channels, "UTF-8")+
                "/" + URLEncoder.encode(keywords, "UTF-8"));
        JsonNode p = nodes;

        System.out.println(nodes.size());
        //System.out.println(nodes.get("p").get("labels"));

        return ok(simplePaperCategories.render(nodes));


    }
}
