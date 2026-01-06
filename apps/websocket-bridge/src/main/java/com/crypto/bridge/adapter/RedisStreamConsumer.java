package com.crypto.bridge.adapter;

import com.crypto.bridge.domain.event.PriceEvent;
import com.crypto.bridge.domain.port.PriceSubscriber;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XGroupCreateArgs;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamConsumer implements PriceSubscriber {

    private final StatefulRedisConnection<String, String> connection;
    private final String streamName;
    private final AtomicBoolean running = new AtomicBoolean(false);
    @Value("${crypto.redis.stream.consumer-group:price-consumers}")
    private String groupName;
    @Value("${crypto.redis.stream.consumer-id:${HOSTNAME:consumer-1}}")
    private String consumerId;
    @Value("${crypto.redis.stream.batch-size:100}")
    private int batchSize;
    private Consumer<PriceEvent> eventHandler;
    private Thread consumerThread;

    @PostConstruct
    public void init() {
        ensureConsumerGroupExists();
    }

    @Override
    public void subscribe(Consumer<PriceEvent> handler) {
        this.eventHandler = handler;
        this.running.set(true);

        consumerThread = new Thread(this::consumeLoop, "redis-consumer");
        consumerThread.start();

        log.info("Started Redis Stream consumer: group={}, consumer={}, stream={}",
                groupName, consumerId, streamName);
    }

    private void consumeLoop() {
        RedisCommands<String, String> sync = connection.sync();

        while (running.get()) {
            try {
                // Read new messages with XREADGROUP
                List<StreamMessage<String, String>> messages = sync.xreadgroup(
                        io.lettuce.core.Consumer.from(groupName, consumerId),
                        XReadArgs.Builder.block(5000).count(batchSize),
                        XReadArgs.StreamOffset.lastConsumed(streamName)
                );

                if (messages != null && !messages.isEmpty()) {
                    processMessages(messages);
                }

            } catch (Exception e) {
                log.error("Error in consumer loop", e);
                sleepQuietly(1000);
            }
        }
    }

    private void processMessages(List<StreamMessage<String, String>> messages) {
        for (StreamMessage<String, String> message : messages) {
            try {
                PriceEvent event = parseMessage(message);
                eventHandler.accept(event);
                acknowledge(message.getId());
                log.debug("Processed message: {}", message.getId());
            } catch (Exception e) {
                log.error("Failed to process message {}", message.getId(), e);
            }
        }
    }

    private PriceEvent parseMessage(StreamMessage<String, String> message) {
        Map<String, String> fields = message.getBody();
        return PriceEvent.fromFieldMap(fields);
    }

    @Override
    public void acknowledge(String messageId) {
        try {
            connection.sync().xack(streamName, groupName, messageId);
        } catch (Exception e) {
            log.error("Failed to acknowledge message {}", messageId, e);
        }
    }

    @Override
    public void shutdown() {
        running.set(false);
        if (consumerThread != null) {
            try {
                consumerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Redis Stream consumer stopped");
    }

    private void ensureConsumerGroupExists() {
        try {
            connection.sync().xgroupCreate(
                    XReadArgs.StreamOffset.from(streamName, "0"),
                    groupName,
                    XGroupCreateArgs.Builder.mkstream()
            );
            log.info("Created consumer group: {}", groupName);
        } catch (Exception e) {
            // Group might already exist
            log.debug("Consumer group already exists or creation failed: {}", e.getMessage());
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
