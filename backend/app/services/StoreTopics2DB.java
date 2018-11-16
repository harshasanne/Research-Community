package services;

import com.google.gson.Gson;
import org.neo4j.driver.v1.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StoreTopics2DB {
    public static ArrayList<ArrayList> getTopics(String txtname) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        InputStream is = new FileInputStream(txtname);
        String line;
        ArrayList<ArrayList> topicList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        line = reader.readLine();
        while (line != null) {
            sb.append(line);
            String[] lineArray= line.split("\t");
            ArrayList<Double> results = new ArrayList();
            String path = lineArray[1].toString();
            String index = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            for(int i = 2;i < lineArray.length; i++)
            {
                results.add(Double.valueOf(lineArray[i]));
            }
            double maxP = results.get(0);
            int topicIndex = 0;
            for(int i = 0; i < results.size(); i++)
            {
                if(results.get(i) > maxP)
                {
                    topicIndex = i;
                    maxP = results.get(i);
                }
            }
            ArrayList<String> pair = new ArrayList<>();
            pair.add(index);
            pair.add(String.valueOf(topicIndex));
            topicList.add(pair);

            sb.append("\n");
            line = reader.readLine();
        }

        reader.close();
        is.close();
        System.out.println(topicList);
        return topicList;

    }

    public static void setCategories(String fileName) throws Exception{

        ArrayList<ArrayList> topicList = new ArrayList<>();

        topicList = getTopics(fileName);

        Driver driver = GraphDatabase.driver(
                "bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"));
        try ( Session session = driver.session() )
        {
            for(ArrayList topic: topicList)
            {
                String index = topic.get(0).toString();
                String topicIndex = topic.get(1).toString();
                String query = "MATCH(p:Paper) " + "WHERE p.index='" + index + "' SET p.category='" + topicIndex
                        + "' RETURN p";
                List<String> papers = session.readTransaction(new TransactionWork<List<String>>() {
                    @Override
                    public List<String> execute(Transaction tx) {
                        return matchPaperNodes(tx, query);
                    }

                });
            }





        }




    }


    private static List<String> matchPaperNodes(Transaction tx, String query)
    {
        List<String> metaDatas = new ArrayList<>();
        StatementResult result = tx.run( query );
        Gson gson = new Gson();
        while ( result.hasNext() )
        {

            Record record = result.next();
            String recordString = gson.toJson(record.asMap());

            metaDatas.add(recordString);
        }
        return metaDatas;
    }

    public static void main(String[] args) {
        try {
            StoreTopics2DB.setCategories("app/services/PapersLDAResults/papersLDAResultsComposition.txt");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
