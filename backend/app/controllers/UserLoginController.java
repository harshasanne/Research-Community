package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.lang.*;
import com.google.gson.Gson;
import java.lang.*;
import utils.loginUtils;

public class UserLoginController  extends Controller{

    //public Result getResearcher(String keywords) throws Exception
    public Result createUser(String username_with_password) throws Exception{
        // userId = URLDecoder.decode(userId, "UTF-8");
        // password = URLDecoder.decode(password, "UTF-8");

        String username = username_with_password.split(",")[0];
        String pwd = username_with_password.split(",")[1];
        String ri = username_with_password.split(",")[2];
        //System.out.println("be infor:"+ userId + password + ri);
        //request().body().asFormUrlEncoded().get("username")[0];
        //ri = URLDecoder.decode(ri, "UTF-8");
        //JsonNode json = request().body().asJson();
        String query1 = "MATCH(a:User{userId:'" + username +"'}) RETURN a.userId";
        String query2 = "create (a:User{userId:'" + username + "', password:'"+ pwd + "', ri:'"+ ri+"'}) RETURN a.userId";

        System.out.println(query1);
        System.out.print(query2);
        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "ptf"));
        String greeting = null;
        try ( Session session = driver.session() )
        {

            greeting =  session.readTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result1 = tx.run( query1 );

                    if(!result1.hasNext()) {

                        StatementResult result2 = tx.run(query2);
                        System.out.println("sign up and log in succuss");
                        return "0";
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
                            return "[\"Status\":0]";
                        }
                        else {
                            System.out.println("error password or username");
                            return "[\"Status\":1]";
                        }
                        //System.out.println("username exists");

                    }
                }
            } );

        }
        return ok(greeting);


    }



}
