package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import forms.LoginForm;
import forms.PaperForm;
import forms.UserProfileForm;
import models.UserProfile;
import models.UserProfile;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import util.APICall;
import util.Constants;
import views.html.index;
import views.html.login;
import views.html.*;


import javax.inject.Inject;

import java.net.URLEncoder;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;

public class UserProfileController extends Controller {
    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    HttpExecutionContext ec;
    @Inject
    public UserProfileController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
    }

    public Result getForm() {
        Form<UserProfileForm> userProfileForm = formFactory.form(UserProfileForm.class);
        return ok(setProfile.render(userProfileForm,false));
    }



    public Result getProfile(String name) throws Exception {
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/getProfile" + "/" + URLEncoder.encode(name, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI

        JsonNode p = nodes;

        System.out.println(nodes.toString());
        //System.out.println(nodes.get("p").get("labels"));

        return ok(views.html.knowledgeCard.render(nodes));
    }

    public CompletionStage<Result> setProfile () throws  Exception
    {
        Form<UserProfileForm> form = formFactory.form(UserProfileForm.class);
        UserProfileForm userProfileForm = formFactory.form(UserProfileForm.class).bindFromRequest().get();
        userProfileForm.setUsername(session().get("username"));
        //System.out.println("role:" + userProfileForm.getTitle());
        return UserProfile.createUserProfle(userProfileForm)
                .thenApplyAsync((WSResponse r) -> {
                    System.out.println(r.asJson());
                    if (r.asJson().get("isSuccessful").asBoolean()) {
                        System.out.println(" Set Profile Success ");

                        return created(index.render(session().get("username")));
                    } else {
                        System.out.println(" Set Profile failed");
                        return badRequest(setProfile.render(form,true));
                    }
                }, ec.current());
    }
}
