package services;


import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;
import utils.LogFactory;
import verticles.APIVerticle;

import java.util.Base64;

public class TwitterService {

    private static final WebClient client = WebClient.create(Vertx.currentContext().owner());

    private static final String CLIENT_ID = APIVerticle.config.getString("twitter_consumer_key", "");
    private static final String CLIENT_SECRET = APIVerticle.config.getString("twitter_consumer_secret", "");
    private static final String AUTH_URL = "https://api.twitter.com/oauth2/token";
    private static final String TWEET_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json";

    private static final Logger logger = LogFactory.getLogger(TwitterService.class);

    private static String getEncodedAuth() {
        String key = CLIENT_ID + ":" + CLIENT_SECRET;
        return Base64.getEncoder().encodeToString(key.getBytes());
    }

    private static Single<JsonObject> authenticate() {
        String encodedAuth = getEncodedAuth();
        String authHeader = "Basic " + encodedAuth;
        return client.postAbs(AUTH_URL)
                .as(BodyCodec.jsonObject())
                .addQueryParam("grant_type", "client_credentials")
                .putHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .putHeader("Authorization", authHeader)
                .rxSend()
                .flatMap(jsonObjectHttpResponse -> {
                    if (200 != jsonObjectHttpResponse.statusCode()) {
                        logger.error("Status code = " + jsonObjectHttpResponse.statusCode());
                        return Single.error(new Error("Twitter Status code = " + jsonObjectHttpResponse.statusCode()));
                    }
                    return Single.just(jsonObjectHttpResponse.body());
                });
    }

    public static Single<JsonObject> searchTweets(String query) {
        return authenticate()
                .flatMap(entries -> {
                    String accessToken = entries.getString("access_token");
                    String authHeader = "Bearer " + accessToken;
                    return client.getAbs(TWEET_SEARCH_URL)
                            .as(BodyCodec.jsonObject())
                            .timeout(8888L)
                            .addQueryParam("q", query)
                            .putHeader("Authorization", authHeader)
                            .rxSend()
                            .flatMap(response -> {
                                if (210 < response.statusCode()) {
                                    logger.error("Search Tweet API code = " + response.statusCode());
                                    return Single.error(new Error("Search Tweet API code = " + response.statusCode()));
                                }
                                return Single.just(response.body());
                            });
                });
    }
}
