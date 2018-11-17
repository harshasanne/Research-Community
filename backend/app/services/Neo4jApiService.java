package services;

import play.libs.ws.*;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

public class Neo4jApiService implements WSBodyReadables, WSBodyWritables {

    private static final String username = "neo4j";
    private static final String password = "neo4j656";
    private static String neo4jhostUrl = "http://localhost:7474";

    private final WSClient ws;

    @Inject
    public Neo4jApiService(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<String> callNeo4jApi(String query, Boolean isGraphResult) {

        WSRequest request = ws.url(Neo4jApiService.neo4jhostUrl + "/db/data/transaction/commit");

        WSRequest complexRequest = request.setAuth(Neo4jApiService.username, Neo4jApiService.password)
                .setRequestTimeout(Duration.of(1000, ChronoUnit.MILLIS));

        String queryJson = "{\"statements\":[{\"statement\":\"" + query + "\"" +
                (isGraphResult ? (",\n" +
                        "                  \"resultDataContents\":[\"graph\"]}]}") : ("}]}"));

        System.out.println("body "+queryJson);

        return complexRequest.setContentType("application/json").post(queryJson).
                thenApply((WSResponse r) -> {

                    if (isGraphResult) {

                        return "{ \"data\":" + r.getBody(json()).findPath("results").get(0).findPath("data").get(0).findPath("graph") + ", \"error\":  "
                                +
                                r.getBody(json()).findPath("errors") +
                                '}';
                    } else {
                        System.out.println(r.getBody(json()));
                        return "{ \"data\": { \"nodes\": " + r.getBody(json()).findPath("results").get(0).findPath("data").get(0).findPath("row").get(0) + "}, \"error\":  "
                                +
                                r.getBody(json()).findPath("errors") +
                                '}';
                    }
                });
    }

}
