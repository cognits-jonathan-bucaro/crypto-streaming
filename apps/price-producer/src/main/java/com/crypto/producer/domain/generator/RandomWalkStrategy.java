package com.crypto.producer.domain.generator;

import com.crypto.producer.domain.model.CryptoCurrency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RandomWalkStrategy {
    private final Random random = new Random();
    private final Map<String, BigDecimal> currentPrices = new ConcurrentHashMap<>();

    public BigDecimal nextPrice(CryptoCurrency currency) {
        BigDecimal currentPrice = currentPrices.getOrDefault(
            currency.symbol(),
            BigDecimal.valueOf(currency.initialPrice())
        );

        BigDecimal newPrice;
        if (random.nextDouble() < 0.7) {
            double changePercent = (random.nextDouble() * 4.0) - 2.0; // Range: -2.0 to +2.0
            BigDecimal change = currentPrice.multiply(BigDecimal.valueOf(changePercent / 100));
            newPrice = currentPrice.add(change);
        } else {
            newPrice = currentPrice;
        }

        BigDecimal minPrice = BigDecimal.valueOf(currency.initialPrice() * 0.5);
        BigDecimal maxPrice = BigDecimal.valueOf(currency.initialPrice() * 2.0);

        // Log when hitting price bounds
        if (newPrice.compareTo(minPrice) < 0) {
            log.debug("{} price hit lower bound: ${} -> ${} (min: ${})",
                    currency.symbol(), newPrice, minPrice, minPrice);
            newPrice = minPrice;
        } else if (newPrice.compareTo(maxPrice) > 0) {
            log.debug("{} price hit upper bound: ${} -> ${} (max: ${})",
                    currency.symbol(), newPrice, maxPrice, maxPrice);
            newPrice = maxPrice;
        }

        newPrice = newPrice.setScale(currency.decimalPrecision(), RoundingMode.HALF_UP);

        currentPrices.put(currency.symbol(), newPrice);

        return newPrice;
    }
}
