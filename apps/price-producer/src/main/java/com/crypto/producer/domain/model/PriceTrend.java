package com.crypto.producer.domain.model;

public enum PriceTrend {
    UP("up", "green"),
    DOWN("down", "red"),
    STABLE("stable", "gray");

    private final String value;
    private final String color;

    PriceTrend(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public static PriceTrend fromValue(String value) {
        return switch (value.toLowerCase()) {
            case "up" -> UP;
            case "down" -> DOWN;
            case "stable" -> STABLE;
            default -> throw new IllegalArgumentException("Unknown trend: " + value);
        };
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
