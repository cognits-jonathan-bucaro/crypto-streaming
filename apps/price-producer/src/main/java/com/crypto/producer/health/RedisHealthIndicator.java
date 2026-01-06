package com.crypto.producer.health;

import com.crypto.producer.domain.port.PricePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {
    private final PricePublisher pricePublisher;

    @Override
    public Health health() {
        try {
            boolean isHealthy = pricePublisher.isHealthy();

            if (isHealthy) {
                return Health.up()
                        .withDetail("redis", "Connected")
                        .withDetail("stream", "crypto:prices")
                        .build();
            } else {
                return Health.down()
                        .withDetail("redis", "Connection failed")
                        .build();
            }
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withException(e)
                    .build();
        }
    }
}
