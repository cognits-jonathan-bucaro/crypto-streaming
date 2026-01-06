package com.crypto.bridge.domain.event;

import com.crypto.bridge.domain.model.CryptoCurrency;
import com.crypto.bridge.domain.model.PriceTrend;
import com.crypto.bridge.domain.model.PriceUpdate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PriceEventTest {

    @Test
    void fromPriceUpdate_shouldCreatePriceEvent() {
        PriceUpdate priceUpdate = PriceUpdate.create(
                new CryptoCurrency.Bitcoin(),
                new BigDecimal("42000.00"),
                new BigDecimal("40000.00")
        );

        PriceEvent event = PriceEvent.fromPriceUpdate(priceUpdate);

        assertEquals("PRICE_UPDATE", event.eventType());
        assertEquals(priceUpdate, event.payload());
        assertNotNull(event.eventId());
        assertNotNull(event.timestamp());
    }

    @Test
    void toFieldMap_shouldConvertEventToMap() {
        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();
        PriceUpdate priceUpdate = new PriceUpdate(
                UUID.randomUUID(),
                "BTC",
                new BigDecimal("42000.00"),
                new BigDecimal("40000.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("5.0000"),
                PriceTrend.UP,
                Instant.now()
        );

        PriceEvent event = new PriceEvent(eventId, "PRICE_UPDATE", timestamp, priceUpdate);
        Map<String, String> fields = event.toFieldMap();

        assertEquals(eventId.toString(), fields.get("eventId"));
        assertEquals("PRICE_UPDATE", fields.get("eventType"));
        assertEquals(timestamp.toString(), fields.get("timestamp"));
        assertEquals("BTC", fields.get("symbol"));
        assertEquals("42000.00", fields.get("price"));
        assertEquals("up", fields.get("trend"));
    }

    @Test
    void fromFieldMap_shouldCreatePriceEventFromMap() {
        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();

        Map<String, String> fields = Map.of(
                "eventId", eventId.toString(),
                "eventType", "PRICE_UPDATE",
                "timestamp", timestamp.toString(),
                "symbol", "ETH",
                "price", "2500.00",
                "previousPrice", "2400.00",
                "change", "100.00",
                "changePercent", "4.1667",
                "trend", "up"
        );

        PriceEvent event = PriceEvent.fromFieldMap(fields);

        assertEquals(eventId, event.eventId());
        assertEquals("PRICE_UPDATE", event.eventType());
        assertEquals(timestamp, event.timestamp());
        assertEquals("ETH", event.payload().symbol());
        assertEquals(new BigDecimal("2500.00"), event.payload().price());
    }
}
