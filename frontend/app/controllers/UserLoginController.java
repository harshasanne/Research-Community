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

    public Result loginResult () throws  Exception
    {
        System.out.print("called");
        String username = request().body().asFormUrlEncoded().get("username")[0];
        String pwd = request().body().asFormUrlEncoded().get("password")[0];
        String ri = request().body().asFormUrlEncoded().get("RI")[0];
        String results = username + "," + pwd + ","+ ri;
        // TODO: We shouldn't hard code url here. someone needs to refactor this code to Constants.java
        JsonNode nodes = apiCall.callAPI(Constants.BACKEND + "/loginResult" + "/" + URLEncoder.encode(results, "UTF-8"));
        // TODO: Harsha, you may want to change the return value a bit to fit into your frontend UI
        //String jstring = nodes.toString();
        Integer status;
        status = nodes.findPath("Status").asInt();

        return ok(views.html.index.render());
    }
}