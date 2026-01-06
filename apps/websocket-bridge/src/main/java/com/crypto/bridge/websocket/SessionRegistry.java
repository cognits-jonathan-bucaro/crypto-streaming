package com.crypto.bridge.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SessionRegistry {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> symbolSubscriptions = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        symbolSubscriptions.put(session.getId(), ConcurrentHashMap.newKeySet());
        log.info("Registered session: {} (total: {})", session.getId(), sessions.size());
    }

    public void unregisterSession(String sessionId) {
        sessions.remove(sessionId);
        symbolSubscriptions.remove(sessionId);
        log.info("Unregistered session: {} (total: {})", sessionId, sessions.size());
    }

    public void subscribeToSymbol(String sessionId, String symbol) {
        Set<String> symbols = symbolSubscriptions.get(sessionId);
        if (symbols != null) {
            symbols.add(symbol);
            log.debug("Session {} subscribed to {}", sessionId, symbol);
        }
    }

    public Collection<WebSocketSession> getInterestedSessions(String symbol) {
        return symbolSubscriptions.entrySet().stream()
                .filter(entry -> entry.getValue().contains(symbol) || entry.getValue().isEmpty())
                .map(entry -> sessions.get(entry.getKey()))
                .filter(Objects::nonNull)
                .filter(WebSocketSession::isOpen)
                .toList();
    }

    public Collection<WebSocketSession> getAllSessions() {
        return sessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .toList();
    }

    public int getSessionCount() {
        return sessions.size();
    }
}
