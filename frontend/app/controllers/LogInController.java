package controllers;
import play.libs.Json;
//import models.User;
import play.libs.ws.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;
import forms.*;

import java.util.concurrent.CompletionStage;

import views.html.*;

public class LogInController extends Controller {
    private final WSClient ws;
    private final FormFactory formFactory;

    @Inject
    public LogInController(WSClient ws, FormFactory formFactory) {
        this.ws = ws;
        this.formFactory = formFactory;
    }

    public Result login() { return ok(views.html.login.render(false)); }

    public Result index() {
        return ok(views.html.index.render(true));
    }

    public CompletionStage<Result> loginResult() {
        Form<LogInForm> form = formFactory.form(LogInForm.class).bindFromRequest();
        LogInForm userInfor = form.get();
        System.out.println("fdjs");
        System.out.println(Json.toJson(userInfor).toString());
        return null;
//        return LogInForm.createUser(userInfor)
//                .thenApply((WSResponse r) -> {
//                    System.out.println(r.asJson().get("body"));
//                    if (r.asJson().get("isSuccessfull").asBoolean()){
//                        System.out.println(" Profile created : Sending him back to login page");
//                        return created(login.render(false));
//                    } else {
//                        System.out.println(" Profile not created : Sending him back to signup page");
//                        return badRequest(login.render(true));
//                    }
//                });
    }
}
