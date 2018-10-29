package controllers;

//import models.User;
import play.libs.ws.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;
import forms.*;

import java.util.concurrent.CompletionStage;

import views.html.login;

public class LogInController extends Controller {
    private final WSClient ws;
    private final FormFactory formFactory;

    @Inject
    public LogInController(WSClient ws, FormFactory formFactory) {
        this.ws = ws;
        this.formFactory = formFactory;
    }

    public Result login() { return ok(views.html.login.render()); }


    public CompletionStage<Result> loginResult() {
        Form<LogInForm> form = formFactory.form(LogInForm.class).bindFromRequest();
        LogInForm info = form.get();
        System.out.println(Json.toJson(info).toString());

        System.out.println("stop here");

        //WSRequest request = ws.url("http://localhost:9000/dummy");

        return request.addHeader("Content-Type", "application/json")
                .post(Json.toJson(info).toString())
                .thenApply((WSResponse r) -> {
                    if (r.getStatus() == 200) {
                        JsonNode jsonNode = r.asJson();

                        System.out.println("fax===");

                        LogInForm lf = new LogInForm();
                        lf.setUserId(jsonNode.get("userid").asText());
                        lf.setPassword(jsonNode.get("password").asText());
                        lf.setRI(jsonNode.get("ri").asText());
                        // p.setFax(jsonNode.get("fx").asText());
                        //p.
                        // u.setPassword(jsonNode.get("password").asText());
                        return ok(views.html.dashboard.render(lf));
                    } else {
                        System.out.println("error connect");
                        //return ok(resetprofile.render(true));
                    }
                });

    }
}
