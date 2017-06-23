package utils;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.core.http.HttpServerResponse;

// Utility functions to be used throughout the project.
public class UtilityFunctions {

    private static final Logger logger = LogFactory.getLogger(UtilityFunctions.class);

    public static boolean isNullOrEmpty(String input) {
        return null == input || input.isEmpty();
    }

    public static boolean isPresent(String input) {
        return !isNullOrEmpty(input);
    }

    public static void sendFailure(HttpServerResponse httpServerResponse, String reason, int statusCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("status", "FAILURE");
        jsonObject.put("reason", reason);
        httpServerResponse.putHeader("content-type", "application/json");
        httpServerResponse.setStatusCode(statusCode);
        httpServerResponse.end(jsonObject.toString());
    }

    public static void sendSuccess(HttpServerResponse httpServerResponse, JsonObject response) {
        response.put("status", "SUCCESS");
        httpServerResponse.putHeader("content-type", "application/json");
        httpServerResponse.setStatusCode(HttpResponseStatus.OK.code());
        httpServerResponse.end(response.toString());
        logger.info("response = " + response.toString());
    }
}
