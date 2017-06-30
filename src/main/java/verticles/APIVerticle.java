package verticles;

import controllers.GoogleMapsController;
import controllers.TwitterController;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import utils.LogFactory;
import controllers.S3Controller;

// This Verticle is to be used to receive API calls.
public class APIVerticle extends AbstractVerticle {

    private static final Logger logger = LogFactory.getLogger(APIVerticle.class);

    public static JsonObject config;

    // Declare all the API routes here.
    private void prepareRoutes(Router router) {

        // Get consolidated body for POST, PUT and PATCH calls
        router.post().handler(BodyHandler.create());
        router.patch().handler(BodyHandler.create());
        router.put().handler(BodyHandler.create());

        // Log all the API calls with request body and query params.
        logRequest(router);

        router.get("/api/v1/s3/download").handler(S3Controller::download);
        router.post("/api/v1/s3/upload").handler(S3Controller::upload);

        router.get("/api/v1/google/maps/geocode").handler(GoogleMapsController::geocode);
        router.get("/api/v1/google/maps/place").handler(GoogleMapsController::place);

        router.get("/api/v1/twitter/search/tweets").handler(TwitterController::searchTweets);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // Set the config to be used throughout the project.
        APIVerticle.config = config();

        // Create a router object.
        Router router = Router.router(vertx);

        prepareRoutes(router);

        // Start the server
        HttpServerOptions options = new HttpServerOptions();
        options.setCompressionSupported(true);
        HttpServer server = vertx.createHttpServer(options);
        server.requestHandler(router::accept).listen(config().getInteger("port"));

        super.start(startFuture);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }

    private void logRequest(Router router) {
        router.route().handler(routingContext -> {
            logger.info("Started API - "+ routingContext.request().method()+" "+routingContext.request().path());

            if (routingContext.getBodyAsString() != null) {
                logger.info("Request body is " + routingContext.getBodyAsString());
            }

            if (routingContext.request() != null && routingContext.request().query() != null) {
                logger.info("Request query are " + routingContext.request().query());
            }
            routingContext.next();
        });
    }
}
