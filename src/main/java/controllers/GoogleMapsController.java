package controllers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.ext.web.RoutingContext;
import services.GoogleMapsServices;
import utils.LogFactory;
import utils.UtilityFunctions;

public class GoogleMapsController {

    private static final Logger logger = LogFactory.getLogger(GoogleMapsController.class);

    public static void geocode(RoutingContext context) {
        String latlong = context.request().params().get("latlng"); // Comma separated latitude and longitude. Ex -> 12.9255259,77.6366633
        // Validate request
        if (UtilityFunctions.isNullOrEmpty(latlong)) {
            UtilityFunctions.sendFailure(context.response(), "Invalid Request", HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        logger.info("latlong = " + latlong);

        GoogleMapsServices.geocode(context.request().params()).subscribe(response -> {
            JsonObject apiResponse = new JsonObject()
                    .put("response", response);
            UtilityFunctions.sendSuccess(context.response(), apiResponse);
        }, error -> {
            logger.error("Error while making Geocode API = " + latlong, error);
            UtilityFunctions.sendFailure(context.response(), "Error while making Geocode call", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        });
    }

    public static void place(RoutingContext context) {
        String location = context.request().params().get("location"); // Comma separated latitude and longitude. Ex -> 12.9255259,77.6366633
        String key = context.request().params().get("key");
        // Validate request
        if (UtilityFunctions.isNullOrEmpty(location) || UtilityFunctions.isNullOrEmpty(key)) {
            UtilityFunctions.sendFailure(context.response(), "Invalid Request", HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        logger.info("location = " + location);

        GoogleMapsServices.place(context.request().params()).subscribe(response -> {
            JsonObject apiResponse = new JsonObject()
                    .put("response", response);
            UtilityFunctions.sendSuccess(context.response(), apiResponse);
        }, error -> {
            logger.error("Error while making Geocode API = " + location, error);
            UtilityFunctions.sendFailure(context.response(), "Error while making Places call", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        });
    }
}
