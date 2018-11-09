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


    public Result getTopics(String channel,String year) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/topicsByChannel" + "/" + URLEncoder.encode(channel, "UTF-8")+
                "/" + URLEncoder.encode(year, "UTF-8"));

        String name;
        Integer count;
        List<Keyword> keywordList = new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            name = nodes.get(i).findPath("name").asText();
            count = nodes.get(i).findPath("count").asInt();
            keywordList.add(new Keyword(name,count));
        }
        return ok(views.html.keywordsByChannel.render(keywordList,channel,year));
    }

    public Result getForm() throws Exception {

        return ok(views.html.doubleInput.render("topicsByChannel","Channel","Year","Find Topics for a channel"));
    }
}
