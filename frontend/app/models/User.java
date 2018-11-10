package models;

import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import forms.LoginForm;
import services.RouteToBackEnd;
import java.util.concurrent.CompletionStage;

public class User {
    public static CompletionStage<WSResponse> createUser(LoginForm form) {

        String jsonString = "{" +
                "\"username\":\"" + form.getUsername() + "\"," +

                "\"password\":\"" + form.getPassword() +"\""+
                "}";

        WSClient ws = play.test.WSTestClient.newClient(9001);

        System.out.println(jsonString);
        WSRequest request = ws.url(RouteToBackEnd.getLoginURL());
        return request.addHeader("Content-Type", "application/json")
                .post(jsonString)
                .thenApply((WSResponse r) -> {
                    //System.out.println(r.asJson());
                    return r;
                });
    }
}
