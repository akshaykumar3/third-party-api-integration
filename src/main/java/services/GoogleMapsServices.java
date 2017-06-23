package services;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Observable;
import utils.LogFactory;


public class GoogleMapsServices {

    private static final String GOOGLE_MAPS_HOST = "maps.googleapis.com";
    private static final String GEOCODE_REQUEST_URI = "/maps/api/geocode/json";
    private static final String PLACES_REQUEST_URI = "/maps/api/place/nearbysearch/json";

    private static final Logger logger = LogFactory.getLogger(GoogleMapsServices.class);
    private static final WebClient webClient = WebClient.create(Vertx.currentContext().owner());

    public static Observable<JsonObject> geocode(MultiMap params) {
        String url = "https://" + GOOGLE_MAPS_HOST + GEOCODE_REQUEST_URI;
        return getMapsData(url, params);
    }

    public static Observable<JsonObject> place(MultiMap params) {
        String url = "https://" + GOOGLE_MAPS_HOST + PLACES_REQUEST_URI;
        return getMapsData(url, params);
    }

    private static Observable<JsonObject> getMapsData(String url, MultiMap params) {
        StringBuilder sb = new StringBuilder(url)
                .append("?");
        params.getDelegate().entries().forEach(entry -> {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("&");
        });

        logger.info("URL = " + sb.toString());

        return webClient
                .getAbs(sb.toString())
                .as(BodyCodec.jsonObject())
                .putHeader("Content-Type", "application/json")
                .rxSend()
                .toObservable()
                .flatMap(httpResponse -> {
                    if (200 > httpResponse.statusCode() || 300 <= httpResponse.statusCode()) {
                        logger.error("Error for geocode API:");
                        return Observable.error(new Exception("Error with geocode API"));
                    }
                    return Observable.just(httpResponse.body());
                });
    }
}
