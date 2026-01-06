package com.crypto.bridge.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceWebSocketHandler extends TextWebSocketHandler {
    private final SessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionRegistry.registerSession(session);
        sendWelcomeMessage(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            JsonNode json = objectMapper.readTree(payload);

            String action = json.get("action").asText();

            switch (action) {
                case "subscribe" -> handleSubscribe(session, json);
                case "unsubscribe" -> handleUnsubscribe(session, json);
                case "ping" -> handlePing(session);
                default -> log.warn("Unknown action: {}", action);
            }

        } catch (Exception e) {
            log.error("Error handling message from session {}", session.getId(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.unregisterSession(session.getId());
        log.info("Connection closed: {} with status {}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error for session {}", session.getId(), exception);
    }

    private void handleSubscribe(WebSocketSession session, JsonNode json) {
        String symbol = json.get("symbol").asText();
        sessionRegistry.subscribeToSymbol(session.getId(), symbol);
    }

    private void handleUnsubscribe(WebSocketSession session, JsonNode json) {
        // Implementation for unsubscribe
        log.debug("Unsubscribe requested for session {}", session.getId());
    }

    private void handlePing(WebSocketSession session) {
        try {
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
        } catch (IOException e) {
            log.error("Failed to send pong", e);
        }
    }

    private void sendWelcomeMessage(WebSocketSession session) {
        try {
            String welcome = objectMapper.writeValueAsString(Map.of(
                    "type", "welcome",
                    "message", "Connected to crypto price stream",
                    "sessionId", session.getId()
            ));
            session.sendMessage(new TextMessage(welcome));
        } catch (IOException e) {
            log.error("Failed to send welcome message", e);
        }
    }
}
