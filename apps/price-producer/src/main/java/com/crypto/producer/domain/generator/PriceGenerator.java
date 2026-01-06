package com.crypto.producer.domain.generator;

import com.crypto.producer.domain.event.PriceEvent;
import com.crypto.producer.domain.model.CryptoCurrency;
import com.crypto.producer.domain.model.PriceUpdate;
import com.crypto.producer.domain.port.PricePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceGenerator {
    private final RandomWalkStrategy randomWalkStrategy;
    private final PricePublisher pricePublisher;
    private final Map<String, BigDecimal> previousPrices = new ConcurrentHashMap<>();
    private final Set<String> firstPublishDone = ConcurrentHashMap.newKeySet();

    public void generateAndPublish(CryptoCurrency currency) {
        try {
            // Generate new price using random walk
            BigDecimal newPrice = randomWalkStrategy.nextPrice(currency);

            // Get previous price (or use initial if first time)
            BigDecimal previousPrice = previousPrices.getOrDefault(
                    currency.symbol(),
                    BigDecimal.valueOf(currency.initialPrice())
            );

            // Create price update
            PriceUpdate priceUpdate = PriceUpdate.create(currency, newPrice, previousPrice);

            // Wrap in event
            PriceEvent event = PriceEvent.fromPriceUpdate(priceUpdate);

            String symbol = currency.symbol();

            // Publish to Redis
            pricePublisher.publish(event)
                    .thenAccept(messageId -> {
                        // Log first publish at INFO level, subsequent at DEBUG
                        if (firstPublishDone.add(symbol)) {
                            log.info("Started publishing {} prices - Initial: ${}, messageId: {}",
                                    symbol, newPrice, messageId);
                        } else {
                            log.debug("Published {} price: ${} (messageId: {})",
                                    symbol, newPrice, messageId);
                        }
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to publish price for {}", symbol, ex);
                        return null;
                    });

            // Update previous price
            previousPrices.put(symbol, newPrice);

        } catch (Exception e) {
            log.error("Error generating price for {}", currency.symbol(), e);
        }
    }
}
