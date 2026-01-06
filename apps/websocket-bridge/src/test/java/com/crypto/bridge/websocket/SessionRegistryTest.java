package com.crypto.bridge.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionRegistryTest {

    private SessionRegistry sessionRegistry;

    @BeforeEach
    void setUp() {
        sessionRegistry = new SessionRegistry();
    }

    @Test
    void registerSession_shouldAddSessionToRegistry() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session-1");

        sessionRegistry.registerSession(session);

        assertEquals(1, sessionRegistry.getSessionCount());
    }

    @Test
    void unregisterSession_shouldRemoveSessionFromRegistry() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session-1");

        sessionRegistry.registerSession(session);
        sessionRegistry.unregisterSession("session-1");

        assertEquals(0, sessionRegistry.getSessionCount());
    }

    @Test
    void subscribeToSymbol_shouldAddSymbolToSessionSubscriptions() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        sessionRegistry.registerSession(session);
        sessionRegistry.subscribeToSymbol("session-1", "BTC");

        Collection<WebSocketSession> interestedSessions = sessionRegistry.getInterestedSessions("BTC");
        assertEquals(1, interestedSessions.size());
        assertTrue(interestedSessions.contains(session));
    }

    @Test
    void getInterestedSessions_shouldReturnSessionsSubscribedToSymbol() {
        WebSocketSession session1 = mock(WebSocketSession.class);
        when(session1.getId()).thenReturn("session-1");
        when(session1.isOpen()).thenReturn(true);

        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("session-2");
        when(session2.isOpen()).thenReturn(true);

        sessionRegistry.registerSession(session1);
        sessionRegistry.registerSession(session2);
        sessionRegistry.subscribeToSymbol("session-1", "BTC");
        sessionRegistry.subscribeToSymbol("session-2", "ETH");

        Collection<WebSocketSession> btcSessions = sessionRegistry.getInterestedSessions("BTC");
        assertEquals(1, btcSessions.size());
        assertTrue(btcSessions.contains(session1));
    }

    @Test
    void getInterestedSessions_shouldReturnSessionsWithNoSubscriptions() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(true);

        sessionRegistry.registerSession(session);

        Collection<WebSocketSession> interestedSessions = sessionRegistry.getInterestedSessions("BTC");
        assertEquals(1, interestedSessions.size());
        assertTrue(interestedSessions.contains(session));
    }

    @Test
    void getInterestedSessions_shouldExcludeClosedSessions() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session-1");
        when(session.isOpen()).thenReturn(false);

        sessionRegistry.registerSession(session);

        Collection<WebSocketSession> interestedSessions = sessionRegistry.getInterestedSessions("BTC");
        assertEquals(0, interestedSessions.size());
    }

    @Test
    void getAllSessions_shouldReturnOnlyOpenSessions() {
        WebSocketSession openSession = mock(WebSocketSession.class);
        when(openSession.getId()).thenReturn("session-1");
        when(openSession.isOpen()).thenReturn(true);

        WebSocketSession closedSession = mock(WebSocketSession.class);
        when(closedSession.getId()).thenReturn("session-2");
        when(closedSession.isOpen()).thenReturn(false);

        sessionRegistry.registerSession(openSession);
        sessionRegistry.registerSession(closedSession);

        Collection<WebSocketSession> allSessions = sessionRegistry.getAllSessions();
        assertEquals(1, allSessions.size());
        assertTrue(allSessions.contains(openSession));
    }

    @Test
    void subscribeToSymbol_shouldHandleNonExistentSession() {
        assertDoesNotThrow(() -> sessionRegistry.subscribeToSymbol("non-existent", "BTC"));
    }
}
