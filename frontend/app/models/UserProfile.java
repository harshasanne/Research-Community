package models;

import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import forms.*;

import util.Constants;

import java.util.concurrent.CompletionStage;

public class UserProfile {
    public static CompletionStage<WSResponse> createUserProfle(UserProfileForm form) {

        String jsonString = "{" +
                "\"username\":\"" + form.getUsername() + "\"," +
                "\"title\":\"" + form.getTitle() +"\","+
                "\"affliation\":\"" + form.getAffliation() +"\","+
                "\"email\":\"" + form.getEmail() +"\","+
                "\"RI\":\"" + form.getRI() +"\""+
                "}";

        WSClient ws = play.test.WSTestClient.newClient(9001);

        System.out.println(jsonString);
        WSRequest request = ws.url(Constants.userProfileURL);
        return request.addHeader("Content-Type", "application/json")
                .post(jsonString)
                .thenApply((WSResponse r) -> {
                    //System.out.println(r.asJson());
                    return r;
                });
    }
}