package com.playground.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private StatefulRedisConnection<String, String> connection;
    private StatefulRedisPubSubConnection<String, String> connectionPubSub;
    private Map<String, List<String>> receivedMessages = new HashMap<>();

    public Controller(StatefulRedisConnection<String, String> connection,
                      StatefulRedisPubSubConnection<String, String> connectionPubSub) {
        this.connection = connection;
        this.connectionPubSub = connectionPubSub;
        this.connectionPubSub.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                receivedMessages.computeIfAbsent(channel, k -> new ArrayList<>());
                receivedMessages.get(channel).add(message);
                log.info("Received on channel '{}' a message: {}", channel, message);
            }
        });
    }

    @GetMapping("/channels")
    public ResponseEntity get() {
        return ResponseEntity.ok(this.connection.sync().pubsubChannels());
    }

    @GetMapping("/messages")
    public ResponseEntity listener() {
        return ResponseEntity.ok(this.receivedMessages);
    }

    @GetMapping("/subscribe/{channel}")
    public void subscribe(@PathVariable("channel") String channel) {
        this.connectionPubSub.sync().subscribe(channel);
    }

    @GetMapping("/publish/{channel}/{message}")
    public void publish(@PathVariable("channel") String channel, @PathVariable("message") String message) {
        this.connection.sync().publish(channel, message);
    }
}
