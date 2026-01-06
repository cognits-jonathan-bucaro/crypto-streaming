package com.crypto.bridge.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceTrendTest {

    @Test
    void fromValue_shouldReturnUpForUpString() {
        assertEquals(PriceTrend.UP, PriceTrend.fromValue("up"));
        assertEquals(PriceTrend.UP, PriceTrend.fromValue("UP"));
        assertEquals(PriceTrend.UP, PriceTrend.fromValue("Up"));
    }

    @Test
    void fromValue_shouldReturnDownForDownString() {
        assertEquals(PriceTrend.DOWN, PriceTrend.fromValue("down"));
        assertEquals(PriceTrend.DOWN, PriceTrend.fromValue("DOWN"));
    }

    @Test
    void fromValue_shouldReturnStableForStableString() {
        assertEquals(PriceTrend.STABLE, PriceTrend.fromValue("stable"));
        assertEquals(PriceTrend.STABLE, PriceTrend.fromValue("STABLE"));
    }

    @Test
    void fromValue_shouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> PriceTrend.fromValue("invalid"));
    }

    @Test
    void getValue_shouldReturnCorrectString() {
        assertEquals("up", PriceTrend.UP.getValue());
        assertEquals("down", PriceTrend.DOWN.getValue());
        assertEquals("stable", PriceTrend.STABLE.getValue());
    }

    @Test
    void getColor_shouldReturnCorrectColor() {
        assertEquals("green", PriceTrend.UP.getColor());
        assertEquals("red", PriceTrend.DOWN.getColor());
        assertEquals("gray", PriceTrend.STABLE.getColor());
    }
}
