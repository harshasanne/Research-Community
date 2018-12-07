package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import forms.TimePeriodForm;
import forms.UserProfileForm;
import models.TimePeriod;
import models.UserProfile;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import util.APICall;
import util.Constants;
import views.html.index;
import views.html.setProfile;
import views.html.simplePaperCategories;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.created;
import static play.mvc.Results.ok;

public class PaperCategoriesController extends Controller {
    private APICall apiCall;
    @Inject
    public PaperCategoriesController(APICall apiCall) {
        this.apiCall = apiCall;
    }

    public Result getForm() throws Exception {

        return ok(views.html.doubleInput.render("simpleCategories","Start Year"
                ,"End Year","Get paper categories by time period"));
    }





    public Result showSimpleCategories (String startYear, String endYear) throws  Exception
    {
        //System.out.println("herehrer");
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/simpleCategories" +
                "/" + URLEncoder.encode(startYear, "UTF-8")+
                "/" + URLEncoder.encode(endYear, "UTF-8"));
        JsonNode p = nodes;

        System.out.println(nodes.size());
        for(int i = 0; i < nodes.size(); i++)
        {
            System.out.println(nodes.get(i).get("p").get("properties").get("title").get("val").asText());
            System.out.println(nodes.get(i).get("p").get("properties").has("category"));
        }
        //System.out.println(nodes.get("p").get("labels"));

        return ok(simplePaperCategories.render(nodes));


    }


}
