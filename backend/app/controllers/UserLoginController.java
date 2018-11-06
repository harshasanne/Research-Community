package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import models.Paper;
import org.neo4j.driver.v1.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.data.FormFactory;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.lang.*;
import com.google.gson.Gson;
import java.lang.*;

import utils.DBDriver;
import utils.loginUtils;
//import form.*;
import models.*;


import javax.inject.Inject;

public class UserLoginController  extends Controller{

private com.typesafe.config.Config config;

    @Inject

    private FormFactory formFactory;

    @Inject
    public UserLoginController(FormFactory formFactory,Config config) {
        this.formFactory = formFactory;
        this.config = config;
    }
    public Result createUser() throws Exception{

        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        User user = userForm.get();



        String username = user.getUsername();
        String pwd = user.getPassword();
        String ri = user.getRI();
        String query1 = "MATCH(a:User{userId:'" + username +"'}) RETURN a.userId";
        String query2 = "create (a:User{userId:'" + username + "', password:'"+ pwd + "', ri:'"+ ri+"'}) RETURN a.userId";

        System.out.println(query1);
        System.out.print(query2);
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

                        StatementResult result2 = tx.run(query2);
                        System.out.println("sign up succuss");
                        return "2";
                    }
                    else {
                        String getPassword = "";
                        //System.out.println(result.next().get(0).asString());
                        while(result1.hasNext())
                        {
                            Record record = result1.next();

                            getPassword = record.get(0).asString();
                            break;
                        }
                        if(getPassword.equals(pwd))
                        {
                            System.out.print("login success");
                            return "0";
                        }
                        else {
                            System.out.println("error password or username");
                            return "1";
                        }
                        //System.out.println("username exists");

                    }
                }
            } );

        }
        JsonNode userObj = Json.toJson(user);

        int resInt = Integer.valueOf(res);
        if(resInt == loginUtils.LOGIN_SUCCESS)
        {

            return created(loginUtils.createResponse(userObj, true));
        }
        if(resInt == loginUtils.LOGIN_FAILURE)
        {

            return created(loginUtils.createResponse(userObj, false));
        }
        else
        {
            return created(loginUtils.createResponse(userObj, true));
        }


    }



}
