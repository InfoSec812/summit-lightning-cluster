package com.redhat.labs.vertx.cluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import static java.util.UUID.randomUUID;

public class MainVerticle extends AbstractVerticle {

    private static final String UUID = randomUUID().toString();
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start() {
        long delay = Math.round(Math.random()*100 + 2000);
        vertx.setPeriodic(delay, this::timerFired);

        vertx.eventBus().consumer("hello.address", this::handleMessage);
    }

    void timerFired(long timerId) {
        vertx.eventBus().publish("hello.address", "Hello "+UUID);
    }

    void handleMessage(Message<String> msg) {
        LOG.error(msg.body());
    }
}
