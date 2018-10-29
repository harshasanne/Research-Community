package controllers;

import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;
import util.APICall;
import util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
public class AuthorController extends Controller {
    private APICall apiCall;

    @Inject
    public AuthorController(APICall apiCall) {
        this.apiCall = apiCall;
    }

    public Result getPapers(String name) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/author" + "/" + URLEncoder.encode(name, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI
        String jstring = nodes.toString();
        String p = new String();
        List<String> paperList = new ArrayList<String>();
        for(int i = 0;i < nodes.size();i++){
           p = nodes.get(i).findPath("title").asText();
           paperList.add(p);
        }
        return ok(views.html.author.render(paperList));
    }
}
