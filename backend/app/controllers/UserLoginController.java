package controllers;

import models.Paper;
import org.neo4j.driver.v1.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.lang.*;
import com.google.gson.Gson;
import java.lang.*;

public class UserLoginController implements AutoCloseable{
    private Driver driver = null;
    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public String createUser(String userId, String password, String ri) throws Exception{
       // userId = URLDecoder.decode(userId, "UTF-8");
       // password = URLDecoder.decode(password, "UTF-8");
        ri = URLDecoder.decode(ri, "UTF-8");
        String query1 = "MATCH(a:User{userId:'" + userId +"'}) RETURN a.userId";
        String query2 = "create (a:User{userId:'" + userId + "', password:'"+ password + "',ri:'" + ri + "'}) RETURN a.userId";

        System.out.println(query1);
        System.out.print(query2);
        driver = GraphDatabase.driver(
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

                        return null;
                    }
                    else {

                        return null;
                    }
                }
            } );

        }
        return greeting;


    }


    public String MatchUserPassword(String userId, String password) throws Exception{
        //userId = URLDecoder.decode(userId, "UTF-8");
         //password = URLDecoder.decode(password, "UTF-8");


        //String query = "create (a:User{userId:'" + userId + "', password:'"+ password + "',ri:'" + ri + "'})";
        String query = "MATCH(a:User{userId:'" + userId +"'}) RETURN a.password";
        System.out.println(query);
        driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "ptf"));
        String greeting = "";

        try ( Session session = driver.session() )
        {

            greeting =  session.readTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    String getPassword = "";

                    StatementResult result = tx.run( query );
                    //System.out.println(result.next().get(0).asString());
                    while(result.hasNext())
                    {
                        Record record = result.next();

                        getPassword = record.get(0).asString();
                        break;
                    }
                    if(getPassword.equals(password))
                    {
                        System.out.print("ok");
                        return getPassword;
                    }
                    else
                        return "";
                }
            } );

        }
        return greeting;

    }

    public static void main(String[] args)
    {
        try(UserLoginController ulc = new UserLoginController())
        {
            System.out.println(ulc.MatchUserPassword("pfff", "ptf"));

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
