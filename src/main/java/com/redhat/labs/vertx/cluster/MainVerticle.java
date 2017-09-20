package com.redhat.labs.vertx.cluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import static java.util.UUID.randomUUID;

/**
 * A very simple Verticle which just has a timed event firing with a different UUID for each run.
 */
public class MainVerticle extends AbstractVerticle {

    private static final String UUID = System.getenv("HOSTNAME");
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start() {

        Router router = Router.router(vertx);

        router.route().handler(StaticHandler.create("webroot"));
        vertx.createHttpServer().requestHandler(router::accept).listen(8778);



        // Schedule the timerFired method to run every 2 seconds + a randomized delay.
        long delay = Math.round(Math.random()*100 + 2000);
        vertx.setPeriodic(delay, this::timerFired);

        // Configure a consumer to read events from the event bus and print them to the console.
        vertx.eventBus().consumer("hello.address", this::handleMessage);
    }

    void timerFired(long timerId) {
        vertx.eventBus().publish("hello.address", "Hello "+UUID);
    }

    void handleMessage(Message<String> msg) {
        LOG.error(msg.body());
    }
}
