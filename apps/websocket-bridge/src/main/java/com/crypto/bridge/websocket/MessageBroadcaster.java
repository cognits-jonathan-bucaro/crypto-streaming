package com.crypto.bridge.websocket;

import com.crypto.bridge.domain.event.PriceEvent;
import com.crypto.bridge.domain.model.PriceUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageBroadcaster {
    private final SessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public void broadcast(PriceEvent event) {
        Collection<WebSocketSession> sessions =
                sessionRegistry.getInterestedSessions(event.payload().symbol());

        if (sessions.isEmpty()) {
            log.trace("No sessions interested in {}", event.payload().symbol());
            return;
        }

        try {
            String messageJson = objectMapper.writeValueAsString(
                    createWebSocketMessage(event)
            );
            TextMessage textMessage = new TextMessage(messageJson);

            for (WebSocketSession session : sessions) {
                try {
                    if (session.isOpen()) {
                        synchronized (session) {
                            session.sendMessage(textMessage);
                        }
                        log.trace("Sent message to session {}", session.getId());
                    }
                } catch (IOException e) {
                    log.error("Failed to send message to session {}", session.getId(), e);
                }
            }

            log.debug("Broadcast {} update to {} sessions",
                    event.payload().symbol(), sessions.size());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize price event", e);
        }
    }

    private Map<String, Object> createWebSocketMessage(PriceEvent event) {
        PriceUpdate payload = event.payload();
        return Map.of(
                "type", "priceUpdate",
                "eventId", event.eventId().toString(),
                "timestamp", event.timestamp().toString(),
                "payload", Map.of(
                        "symbol", payload.symbol(),
                        "price", payload.price(),
                        "previousPrice", payload.previousPrice(),
                        "change", payload.change(),
                        "changePercent", payload.changePercent(),
                        "trend", payload.trend().getValue(),
                        "timestamp", payload.timestamp().toString()
                )
        );
    }
}
