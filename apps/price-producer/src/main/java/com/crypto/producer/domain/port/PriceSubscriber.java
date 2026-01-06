package com.crypto.producer.domain.port;

import com.crypto.producer.domain.event.PriceEvent;

import java.util.function.Consumer;

public interface PriceSubscriber {
    void subscribe(Consumer<PriceEvent> handler);
    void acknowledge(String messageId);
    void shutdown();
}
