package com.crypto.producer.domain.port;

import com.crypto.producer.domain.event.PriceEvent;

import java.util.concurrent.CompletableFuture;

public interface PricePublisher {
    CompletableFuture<String> publish(PriceEvent message);
    boolean isHealthy();
}
