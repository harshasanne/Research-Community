package util;

import javax.inject.Inject;
import play.Logger;
import play.libs.Json;
import play.libs.ws.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.concurrent.CompletionStage;

public class APICall {
    private final WSClient ws;

    @Inject
    public APICall(WSClient ws) {
        this.ws = ws;
    }

    public static enum ResponseType {
        SUCCESS, GETERROR, SAVEERROR, DELETEERROR, RESOLVEERROR, TIMEOUT, CONVERSIONERROR, UNKNOWN
    }
    public JsonNode callAPI(String apiString) {
        Logger.info(apiString);
        CompletionStage<JsonNode> jsonPromise = ws.url(apiString).get()
          .thenApply(r -> r.asJson());
        try {
            return jsonPromise.toCompletableFuture().get();
        } catch (Exception e) {
            return createResponse(ResponseType.TIMEOUT);
        }
    }



    public JsonNode createResponse(ResponseType type) {
        ObjectNode jsonData = Json.newObject();
        switch (type) {
            case SUCCESS:
                jsonData.put("success", "Success!");
                break;
            case GETERROR:
                jsonData.put("error", "Cannot get data from server");
                break;
            case SAVEERROR:
                jsonData.put("error", "Cannot be saved. The data must be invalid!");
                break;
            case DELETEERROR:
                jsonData.put("error", "Cannot be deleted on server");
                break;
            case RESOLVEERROR:
                jsonData.put("error", "Cannot be resolved on server");
                break;
            case TIMEOUT:
                jsonData.put("error", "No response/Timeout from server");
                break;
            case CONVERSIONERROR:
                jsonData.put("error", "Conversion error");
                break;
            default:
                jsonData.put("error", "Unknown errors");
                break;
        }
        return jsonData;
    }
}
