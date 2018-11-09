package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import models.Evolution;
import models.Keyword;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.APICall;
import util.Constants;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class EvolutionByChannelController extends Controller {

    private APICall apiCall;
    @Inject
    public EvolutionByChannelController(APICall apiCall) {
        this.apiCall = apiCall;
    }


    public Result getEvloutions(String channel,String startYear,String endYear) throws Exception {
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/evolution" + "/" + URLEncoder.encode(channel, "UTF-8")+
                "/" + URLEncoder.encode(startYear, "UTF-8")+
                "/" + URLEncoder.encode(endYear, "UTF-8"));

        String name;
        Integer count;
        String year;
        List<Evolution> evoList=new ArrayList<>();
        for(int i = 0;i < nodes.size();i++){
            year = nodes.get(i).findPath("year").asText();
            JsonNode keywordNode = nodes.get(i).findPath("keywords");
            List<Keyword> keywordList = new ArrayList<>();
            for(int j=0;j<keywordNode.size();j++){
                name = keywordNode.get(j).findPath("name").asText();
                count = keywordNode.get(j).findPath("count").asInt();
                keywordList.add(new Keyword(name,count));
            }
            evoList.add(new Evolution(year,keywordList));
        }
        return ok(views.html.evolutionByChannel.render(evoList,channel,startYear,endYear));
    }

    public Result getForm() throws Exception {

        return ok(views.html.trippleInput.render("evolution","Channel","Start Year"
                ,"End Year","Get Topics Trending for a channel"));
    }
}
