package com.crypto.bridge.domain.port;

import com.crypto.bridge.domain.event.PriceEvent;

import java.util.function.Consumer;

public interface PriceSubscriber {
    void subscribe(Consumer<PriceEvent> handler);

    void acknowledge(String messageId);

    void shutdown();
}
