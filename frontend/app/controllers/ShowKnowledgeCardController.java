package controllers;

import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;
import util.APICall;
import util.Constants;
import views.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import play.data.Form;
import play.data.FormFactory;
import java.net.URLEncoder;
import views.html.paperYear;
import models.*;


public class ShowKnowledgeCardController extends Controller{

    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    public ShowKnowledgeCardController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
    }




    public Result getCard(String name) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/showKnowledgeCard" + "/" + URLEncoder.encode(name, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI

          JsonNode p = nodes;

        System.out.println(nodes.toString());
       // System.out.println(nodes.get("p").get("labels"));

        return ok(views.html.knowledgeCard.render(nodes));
    }

    public Result getForm() throws Exception {

        return ok(views.html.singleInput.render("showKnowledgeCard","title","Show meta data of a paper"));
    }
}
