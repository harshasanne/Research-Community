package forms;

import play.data.validation.Constraints;

import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import services.RouteToBackend;

import play.data.validation.Constraints.Validatable;

import java.util.concurrent.CompletionStage;

public class LogInForm  {

    protected String userId;

    protected String password;

    protected String RI;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRI() {
        return RI;
    }

    public void setRI(String RI) {
        this.RI = RI;
    }


    public static CompletionStage<WSResponse> createUser(LogInForm form) {

        String jsonString = "{" +
                "\"userId\":\"" + form.getUserId() + "\"," +
                "\"password\":\"" + form.getPassword() +"\","+
                "\"RI\":\"" + form.getRI() +"\""+
                "}";

        WSClient ws = play.test.WSTestClient.newClient(9001);
        System.out.println("tobk:" + jsonString);
        //System.out.println(jsonString);
        WSRequest request = ws.url(RouteToBackend.getLogInURL());
        return request.addHeader("Content-Type", "application/json")
                .post(jsonString)
                .thenApply((WSResponse r) -> {
                    //System.out.println(r.asJson());
                    return r;
                });
    }
}