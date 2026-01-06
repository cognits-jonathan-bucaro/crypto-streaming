package com.crypto.producer.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${crypto.redis.host:localhost}")
    private String redisHost;

    @Value("${crypto.redis.port:6379}")
    private int redisPort;

    @Value("${crypto.redis.stream.name:crypto:prices}")
    private String streamName;

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(String.format("redis://%s:%d", redisHost, redisPort));
    }

    @Bean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public String streamName() {
        return streamName;
    }

    @PreDestroy
    public void cleanup() {
        // Cleanup handled by Spring
    }
}
