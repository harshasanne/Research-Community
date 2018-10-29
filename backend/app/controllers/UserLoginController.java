package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.lang.*;
import com.google.gson.Gson;

public class UserLoginController {
    public boolean createUser(String userId, String password, String ri) throws Exception{
        userId = URLDecoder.decode(userId, "UTF-8");
        password = URLDecoder.decode(password, "UTF-8");
        ri = URLDecoder.decode(ri, "UTF-8");
        String query1 = "MATCH(a:user{userId:'" + userId +"'}) RETURN a.userId";
        String query2 = "create (a:User{userId:'" + userId + "', password:'"+ password + "',ri:'" + ri + "'})";

        System.out.println(query);
        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "ptf"));
        boolean greeting = false;
        try ( Session session = driver.session() )
        {

            greeting =  session.readTransaction( new TransactionWork<String>()
            {
                @Override
                public boolean execute( Transaction tx )
                {
                    StatementResult result1 = tx.run( query1 );
                    if(result1 == null) {
                        StatementResult result2 = tx.run(query2);
                        return true;
                    }
                    else
                        return false;
                }
            } );

        }
        return greeting;


    }


    public boolean MatchUserPassword(String userId, String password) throws Exception{
        userId = URLDecoder.decode(userId, "UTF-8");
        password = URLDecoder.decode(password, "UTF-8");


        //String query = "create (a:User{userId:'" + userId + "', password:'"+ password + "',ri:'" + ri + "'})";
        String query = "MATCH(a:user{userId:'" + userId +"'}) RETURN a.password";
        System.out.println(query);
        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "ptf"));
        boolean greeting = false;
        String getPassword = null;
        try ( Session session = driver.session() )
        {

            greeting =  session.readTransaction( new TransactionWork<String>()
            {
                @Override
                public boolean execute( Transaction tx )
                {

                    StatementResult result = tx.run( query );
                    while(result.hasNext())
                    {
                        Record record = result.next();

                        getPassword = record.get(0).asString();
                        break;
                    }
                    if(getPassword.equals(password))
                    {
                        return true;
                    }
                    else
                        return false;
                }
            } );

        }
        return greeting;

    }

    public static void main(String[] args)
    {
        try(UserLoginController ulc = new UserLoginController())
        {
            System.out.println(ulc.createUser("ptf", "ptf", "ptf"));

        }
    }
}
