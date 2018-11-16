package models;

import forms.TimePeriodForm;
import forms.UserProfileForm;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import util.Constants;

import java.util.concurrent.CompletionStage;

public class TimePeriod {
    public static CompletionStage<WSResponse> getSimpleCategories(TimePeriodForm form) {

        String jsonString = "{" +
                "\"startYear\":\"" + form.getStartYear() + "\"," +

                "\"endYear\":\"" + form.getEndYear() +"\""+
                "}";

        WSClient ws = play.test.WSTestClient.newClient(9001);

        System.out.println(jsonString);
        WSRequest request = ws.url(Constants.showSimpleCategoriesURL);
        return request.addHeader("Content-Type", "application/json")
                .post(jsonString)
                .thenApply((WSResponse r) -> {
                    //System.out.println(r.asJson());
                    return r;
                });
    }
}
