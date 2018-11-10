package controllers;

import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSResponse;
import play.mvc.*;
import util.APICall;
import util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.*;
import play.data.Form;
import play.data.FormFactory;
import java.net.URLEncoder;
import java.util.concurrent.CompletionStage;

import views.html.login;
import views.html.index;
import views.html.paperYear;
import models.*;
import forms.*;

import static play.mvc.Controller.session;

public class UserLoginController extends Controller {
    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    HttpExecutionContext ec;
    @Inject
    public UserLoginController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
    }

    public Result login() { return ok(views.html.login.render(false)); }

    public CompletionStage<Result> loginResult () throws  Exception
    {
        Form<LoginForm> form = formFactory.form(LoginForm.class).bindFromRequest();
        LoginForm userForm = form.get();
        //System.out.println("role:" + userForm.getUsername());
        return User.createUser(userForm)
                .thenApplyAsync((WSResponse r) -> {
                    System.out.println(r.asJson());
                    if (r.asJson().get("isSuccessful").asBoolean()) {
                        System.out.println(" Login Success ");
                        session().clear();
                        /* Store user information in session */
                        session("username", userForm.getUsername());




                       // System.out.println("Session Username : "+ session().get("username"));
                        Long lastVisited = r.asJson().get("body").get("lastVisited").asLong();
                        String dateString = "";
                        if (lastVisited != 0L) {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
                            Date resultDate = new Date(lastVisited);
                            dateString = sdf.format(resultDate);
                        }

                        return created(index.render(session().get("username"), dateString));
                    } else {
                        System.out.println(" Login failed");
                        return badRequest(login.render(true));
                    }
                }, ec.current());
    }
}