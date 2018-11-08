package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class loginUtils {
    public final static int LOGIN_SUCCESS = 0;
    public final static int LOGIN_FAILURE = 1;
    public final static int SIGNUP_SUCCESS = 2;


    public static ObjectNode createResponse(
            Object response, boolean ok) {

        ObjectNode result = Json.newObject();
        result.put("isSuccessful", ok);
        if (response instanceof String) {
            result.put("body", (String) response);
        }
        else {
            result.put("body", (JsonNode) response);
        }

        return result;
    }
}
