package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import util.APICall;
import util.Constants;

import javax.inject.Inject;
import java.net.URLEncoder;
import views.*;
import views.html.authorCard;

public class ShowAuthorCardController extends Controller {

    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    public ShowAuthorCardController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
    }




    public Result getCard(String name) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/showAuthorCard" + "/" + URLEncoder.encode(name, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI

        JsonNode p = nodes;
        System.out.println(nodes);
        System.out.println(nodes.toString());
        //System.out.println(nodes.get("p").get("labels"));

        return ok(authorCard.render(nodes));
    }

    public Result getForm() throws Exception {

        return ok(views.html.singleInput.render("showAuthorCard","title","Show meta data of an author"));
    }
}
