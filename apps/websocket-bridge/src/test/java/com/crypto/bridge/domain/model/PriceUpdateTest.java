package com.crypto.bridge.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PriceUpdateTest {

    @Test
    void create_shouldCalculateChangeAndPercentageCorrectly() {
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        BigDecimal previousPrice = new BigDecimal("40000.00");
        BigDecimal newPrice = new BigDecimal("42000.00");

        PriceUpdate update = PriceUpdate.create(btc, newPrice, previousPrice);

        assertEquals("BTC", update.symbol());
        assertEquals(new BigDecimal("42000.00"), update.price());
        assertEquals(new BigDecimal("40000.00"), update.previousPrice());
        assertEquals(new BigDecimal("2000.00"), update.change());
        assertEquals(new BigDecimal("5.0000"), update.changePercent());
        assertEquals(PriceTrend.UP, update.trend());
        assertNotNull(update.eventId());
        assertNotNull(update.timestamp());
    }

    @Test
    void create_shouldSetDownTrendWhenPriceDecreases() {
        CryptoCurrency eth = new CryptoCurrency.Ethereum();
        BigDecimal previousPrice = new BigDecimal("3000.00");
        BigDecimal newPrice = new BigDecimal("2500.00");

        PriceUpdate update = PriceUpdate.create(eth, newPrice, previousPrice);

        assertEquals(PriceTrend.DOWN, update.trend());
        assertTrue(update.change().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void create_shouldSetStableTrendWhenPriceUnchanged() {
        CryptoCurrency sol = new CryptoCurrency.Solana();
        BigDecimal price = new BigDecimal("100.00");

        PriceUpdate update = PriceUpdate.create(sol, price, price);

        assertEquals(PriceTrend.STABLE, update.trend());
        assertEquals(0, update.changePercent().compareTo(BigDecimal.ZERO));
    }

    @Test
    void create_shouldHandleZeroPreviousPrice() {
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        BigDecimal previousPrice = BigDecimal.ZERO;
        BigDecimal newPrice = new BigDecimal("42000.00");

        PriceUpdate update = PriceUpdate.create(btc, newPrice, previousPrice);

        assertEquals(0, update.changePercent().compareTo(BigDecimal.ZERO));
    }

    @Test
    void fromFieldMap_shouldCreatePriceUpdateFromMap() {
        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();

        Map<String, String> fields = Map.of(
                "eventId", eventId.toString(),
                "symbol", "BTC",
                "price", "42000.00",
                "previousPrice", "40000.00",
                "change", "2000.00",
                "changePercent", "5.0000",
                "trend", "up",
                "timestamp", timestamp.toString()
        );

        PriceUpdate update = PriceUpdate.fromFieldMap(fields);

        assertEquals(eventId, update.eventId());
        assertEquals("BTC", update.symbol());
        assertEquals(new BigDecimal("42000.00"), update.price());
        assertEquals(PriceTrend.UP, update.trend());
        assertEquals(timestamp, update.timestamp());
    }
}
