package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;

import org.neo4j.driver.v1.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.data.FormFactory;

import java.lang.*;


import utils.DBDriver;
import utils.loginUtils;

import com.google.gson.Gson;
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
        System.out.println("login Username:" + username);
        String pwd = user.getPassword();

        Long currentTime = System.currentTimeMillis();
        String query1 = "MATCH(a:Author{authorName:'" + username +"'}) WHERE EXISTS(a.password) RETURN a.password, a.lastVisited";
        String query2 = "MATCH(a:Author{authorName:'" + username +"'}) SET a.password='" + pwd + "', a.lastVisited="
          + currentTime + " RETURN a.authorName";
        String query3 = "create (a:Author{authorName:'" + username + "', password:'"+ pwd + "', lastVisited:" +
          currentTime + "}) RETURN a.authorName";
        String updateLastVisitedQuery = "MATCH(a:Author{authorName:'" + username +"'}) SET a.lastVisited="
          + currentTime + " RETURN a.authorName";

        System.out.println(query1);
        Driver driver = DBDriver.getDriver(this.config);

        String res = null;
        try ( Session session = driver.session() )
        {

            res =  session.readTransaction( new TransactionWork<String>()
            {
                @Override

                public String execute( Transaction tx )
                {
                    user.setLastVisited(0L);
                    StatementResult result1 = tx.run( query1 );

                    if(!result1.hasNext()) {
                        System.out.println(query2);

                        StatementResult result2 = tx.run(query2);
                        if(!result2.hasNext()) {
                            System.out.println(query3);
                            StatementResult result3 = tx.run(query3);
                            System.out.println("sign up succuss");
                            return "2";
                        }
                        else
                        {
                            user.setLastVisited(currentTime);
                            System.out.println("registered the password");
                            return "3";

                        }
                    }
                    else {
                        String getPassword = "";
                        long lastVisited = 0L;
                        Record record = null;
                        while(result1.hasNext())
                        {
                            record = result1.next();

                            getPassword = record.get(0).asString();
                            lastVisited = record.get(1).asLong();
                            break;
                        }
                        System.out.println("pwd: " + getPassword);

                        if(getPassword.equals(pwd))
                        {
                            System.out.print("login success");
                            user.setLastVisited(lastVisited);
                            tx.run(updateLastVisitedQuery);
                            return "0";
                        }
                        else {
                            System.out.println("error password or username");
                            return "1";
                        }

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
        if(resInt == loginUtils.SIGNUP_SUCCESS)
        {
            return created(loginUtils.createResponse(userObj, true));
        }
        else
        {


            return created(loginUtils.createResponse(userObj, true));
        }


    }



}
