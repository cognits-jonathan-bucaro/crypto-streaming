package com.crypto.producer.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PriceUpdate(
        UUID eventId,
        String symbol,
        BigDecimal price,
        BigDecimal previousPrice,
        BigDecimal change,
        BigDecimal changePercent,
        PriceTrend trend,
        Instant timestamp
) {

    public static PriceUpdate create(
            CryptoCurrency currency,
            BigDecimal newPrice,
            BigDecimal previousPrice
    ) {
        BigDecimal change = newPrice.subtract(previousPrice);
        BigDecimal changePercent = previousPrice.compareTo(BigDecimal.ZERO) > 0
                ? change.divide(previousPrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        PriceTrend trend = determineTrend(change);

        return new PriceUpdate(
                UUID.randomUUID(),
                currency.symbol(),
                newPrice.setScale(currency.decimalPrecision(), RoundingMode.HALF_UP),
                previousPrice.setScale(currency.decimalPrecision(), RoundingMode.HALF_UP),
                change.setScale(currency.decimalPrecision(), RoundingMode.HALF_UP),
                changePercent.setScale(4, RoundingMode.HALF_UP),
                trend,
                Instant.now()
        );
    }

    private static PriceTrend determineTrend(BigDecimal change) {
        int comparison = change.compareTo(BigDecimal.ZERO);

        return switch (comparison) {
            case 1 -> PriceTrend.UP;
            case -1 -> PriceTrend.DOWN;
            default -> PriceTrend.STABLE;
        };
    }

    public static PriceUpdate fromFieldMap(Map<String, String> fields) {
        return new PriceUpdate(
                UUID.fromString(fields.get("eventId")),
                fields.get("symbol"),
                new BigDecimal(fields.get("price")),
                new BigDecimal(fields.get("previousPrice")),
                new BigDecimal(fields.get("change")),
                new BigDecimal(fields.get("changePercent")),
                PriceTrend.fromValue(fields.get("trend")),
                Instant.parse(fields.get("timestamp"))
        );
    }
}
