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
        //String jstring = nodes.toString();
        String p = nodes.get(0).findPath("allData").asText();;

//        List<String> paperList = new ArrayList<String>();
//
//        if(nodes != null){
//            for(int i = 0;i < nodes.size();i++){
//                System.out.println(nodes.get(i));
//                p = nodes.get(i).findPath("allData").asText();//findPath("title").asText();
//                paperList.add(p);
//            }
//        }
        return ok(views.html.knowledgeCard.render(p));
    }
}
