package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.typesafe.config.Config;
import models.UserProfile;
import models.UserProfile;
import org.neo4j.driver.v1.*;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DBDriver;
import utils.userProfileUtils;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class UserProfileController  extends Controller {

    private com.typesafe.config.Config config;

    @Inject

    private FormFactory formFactory;

    @Inject
    public UserProfileController(FormFactory formFactory, Config config) {
        this.formFactory = formFactory;
        this.config = config;
    }
    public Result setProfile() throws Exception{

        Form<UserProfile> userProfileForm = formFactory.form(UserProfile.class).bindFromRequest();
        UserProfile userProfile = userProfileForm.get();



        String username = userProfile.getUsername();
        String title = userProfile.getTitle();
        String affliation = userProfile.getAffliation();
        String RI = userProfile.getRI();
        String email = userProfile.getEmail();
        String query1 = "MATCH(a:Author{authorName:'" + username +"'}) " + "SET a.title='"+ title
                + "',a.affliation='"+ affliation +"', a.RI='" +RI+ "', a.email='" + email +"' RETURN a.authorName";

        System.out.println(query1);

        Driver driver = DBDriver.getDriver(this.config);
        //int res = 0;
        String res = null;
        try ( Session session = driver.session() )
        {

            res =  session.readTransaction( new TransactionWork<String>()
            {
                @Override

                public String execute( Transaction tx )
                {
                    StatementResult result1 = tx.run( query1 );

                    if(!result1.hasNext()) {
                        return "0";
                    }
                    else {
                        return "1";

                    }
                }
            } );

        }
        JsonNode userProfileObj = Json.toJson(userProfile);

        int resInt = Integer.valueOf(res);
        if(resInt == userProfileUtils.SET_SUCCESS)
        {

            return created(userProfileUtils.createResponse(userProfileObj, true));
        }
        else
        {

            return created(userProfileUtils.createResponse(userProfileObj, false));
        }


    }




}
