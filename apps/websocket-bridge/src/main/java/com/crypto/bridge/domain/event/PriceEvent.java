package com.crypto.bridge.domain.event;

import com.crypto.bridge.domain.model.PriceUpdate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PriceEvent(
        UUID eventId,
        String eventType,
        Instant timestamp,
        PriceUpdate payload
) {
    public PriceEvent {
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        if (payload == null) {
            eventType = "PRICE_UPDATE";
        }
    }

    public static PriceEvent fromPriceUpdate(PriceUpdate priceUpdate) {
        return new PriceEvent(
                UUID.randomUUID(),
                "PRICE_UPDATE",
                Instant.now(),
                priceUpdate
        );
    }

    public static PriceEvent fromFieldMap(Map<String, String> fields) {
        PriceUpdate priceUpdate = PriceUpdate.fromFieldMap(fields);

        return new PriceEvent(
                UUID.fromString(fields.get("eventId")),
                fields.get("eventType"),
                Instant.parse(fields.get("timestamp")),
                priceUpdate
        );
    }

    public Map<String, String> toFieldMap() {
        return Map.of(
                "eventId", eventId.toString(),
                "eventType", eventType,
                "timestamp", timestamp.toString(),
                "symbol", payload.symbol(),
                "price", payload.price().toString(),
                "previousPrice", payload.previousPrice().toString(),
                "change", payload.change().toString(),
                "changePercent", payload.changePercent().toString(),
                "trend", payload.trend().getValue()
        );
    }
}

