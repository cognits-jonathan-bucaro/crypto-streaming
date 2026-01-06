package com.crypto.bridge.consumer;

import com.crypto.bridge.adapter.RedisStreamConsumer;
import com.crypto.bridge.websocket.MessageBroadcaster;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceStreamConsumer {
    private final RedisStreamConsumer redisConsumer;
    private final MessageBroadcaster broadcaster;

    @PostConstruct
    public void start() {
        redisConsumer.subscribe(event -> {
            try {
                broadcaster.broadcast(event);
            } catch (Exception e) {
                log.error("Error broadcasting event", e);
            }
        });
        log.info("PriceStreamConsumer started - bridging Redis to WebSocket");
    }

    @PreDestroy
    public void stop() {
        redisConsumer.shutdown();
        log.info("PriceStreamConsumer stopped");
    }
}
