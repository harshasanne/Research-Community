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


public class FindTopicsByChannelController extends Controller {

    private APICall apiCall;
    @Inject
    public FindTopicsByChannelController(APICall apiCall) {
        this.apiCall = apiCall;
    }


    public Result getTopics(String channel) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/topicsByChannel" + "/" + URLEncoder.encode(channel, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI

        String name;
        Integer count;
        List<Keyword> keywordList = new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            name = nodes.get(i).findPath("name").asText();
            count = nodes.get(i).findPath("count").asInt();
            keywordList.add(new Keyword(name,count));
        }
        return ok(views.html.keywordsByChannel.render(keywordList,channel));
    }

    public Result getForm() throws Exception {

        return ok(views.html.singleInput.render("topicsByChannel","Channel","Find Topics for a channel"));
    }
}
