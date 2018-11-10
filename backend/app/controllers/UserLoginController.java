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

        String query1 = "MATCH(a:Author{authorName:'" + username +"'}) WHERE EXISTS(a.password) RETURN a.password";
        String query2 = "MATCH(a:Author{authorName:'" + username +"'}) SET a.password='" + pwd + "' RETURN a.authorName";
        String query3 = "create (a:Author{authorName:'" + username + "', password:'"+ pwd + "'}) RETURN a.authorName";
        System.out.println(query1);
        System.out.println(query2);
        Driver driver = DBDriver.getDriver(this.config);

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
                        if(!result2.hasNext()) {
                            StatementResult result3 = tx.run(query3);
                            System.out.println("sign up succuss");
                            return "2";
                        }
                        else
                        {
                            System.out.println("registered the password");
                            return "3";

                        }
                    }
                    else {
                        String getPassword = "";

                        while(result1.hasNext())
                        {
                            Record record = result1.next();



                            getPassword = record.get(0).asString();

                            break;
                        }
                        System.out.println("pwd: " + getPassword);

                        if(getPassword.equals(pwd))
                        {
                            System.out.print("login success");
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
