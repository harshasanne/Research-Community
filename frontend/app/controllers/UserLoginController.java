package controllers;

import javax.inject.Inject;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;
import util.APICall;
import util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import play.data.Form;
import play.data.FormFactory;
import java.net.URLEncoder;
import views.html.paperYear;
import models.*;

public class UserLoginController extends Controller {
    private APICall apiCall;
    private final FormFactory formFactory;

    @Inject
    public UserLoginController(FormFactory formFactory,APICall apiCall) {
        this.apiCall = apiCall;
        this.formFactory = formFactory;
    }

    public Result login() { return ok(views.html.login.render()); }

    public Result loginResult()
    {
        String username = session().get("username");
        String password = session().get("password");
        System.out.print("username:" + username);
        String jsonString = "{" +
                "\"username\":\"" + username + "\"," +
                "\"password\":\"" + password +"\""+
                "}";

        JsonNode nodes = apiCall.callAPIPost(Constants.BACKEND + "/loginResult",jsonString );
         return null;
    }
}