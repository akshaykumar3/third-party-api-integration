package controllers;

import io.vertx.rxjava.ext.web.RoutingContext;

public class HealthCheckController {

    public static void healthCheck(RoutingContext context) {
        context.response().end("I am feeling 200 OK.");
    }
}
