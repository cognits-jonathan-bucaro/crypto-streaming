package com.crypto.producer.adapter;

import com.crypto.producer.domain.event.PriceEvent;
import com.crypto.producer.domain.port.PricePublisher;
import io.lettuce.core.XAddArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamPublisher implements PricePublisher {
    private final StatefulRedisConnection<String, String> connection;
    private final String streamName;
    private final AtomicLong publishCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    @Value("${crypto.redis.stream.max-length:10000}")
    private long maxLength;

    @Override
    public CompletableFuture<String> publish(PriceEvent message) {
        XAddArgs args = XAddArgs.Builder
                .maxlen(maxLength)
                .approximateTrimming();

        return connection.async()
                .xadd(streamName, args, message.toFieldMap())
                .toCompletableFuture()
                .whenComplete((messageId, throwable) -> {
                    if (throwable != null) {
                        errorCount.incrementAndGet();
                        log.error("Failed to publish event to Redis stream", throwable);
                    } else {
                        long count = publishCount.incrementAndGet();

                        // Log stats every 100 messages
                        if (count % 100 == 0) {
                            log.info("Published {} messages to Redis stream '{}' (errors: {})",
                                    count, streamName, errorCount.get());
                        }

                        log.debug("Published event {} to stream {}", messageId, streamName);
                    }
                });
    }

    @Override
    public boolean isHealthy() {
        try {
            connection.sync().ping();
            return true;
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return false;
        }
    }
}
