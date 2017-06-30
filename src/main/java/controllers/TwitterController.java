package controllers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.ext.web.RoutingContext;
import services.TwitterService;
import utils.LogFactory;
import utils.UtilityFunctions;

/**
 * Controller to handle Twitter related APIs
 */
public class TwitterController {

    private static final Logger logger = LogFactory.getLogger(TwitterController.class);

    public static void searchTweets(RoutingContext context) {
        String query = context.request().params().get("query");
        if (UtilityFunctions.isNullOrEmpty(query)) {
            UtilityFunctions.sendFailure(context.response(), "Invalid Request", HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        TwitterService.searchTweets(query).subscribe(json -> UtilityFunctions.sendSuccess(context.response(), json), error -> {
            error.printStackTrace();
            logger.error("Error while making Twitter search API = " + query, error);
            UtilityFunctions.sendFailure(context.response(), "Error while making Twitter search call", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        });
    }
}
