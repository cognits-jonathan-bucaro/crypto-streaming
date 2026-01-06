package com.crypto.producer.domain.model;

import java.util.List;

public sealed interface CryptoCurrency {
    static CryptoCurrency fromSymbol(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "BTC" -> new Bitcoin();
            case "ETH" -> new Ethereum();
            case "SOL" -> new Solana();
            default -> throw new IllegalArgumentException("Unknown symbol: " + symbol);
        };
    }

    static List<CryptoCurrency> all() {
        return List.of(new Bitcoin(), new Ethereum(), new Solana());
    }

    String symbol();

    String displayName();

    int decimalPrecision();

    double initialPrice();

    double volatilityFactor();

    record Bitcoin() implements CryptoCurrency {

        @Override
        public String symbol() {
            return "BTC";
        }

        @Override
        public String displayName() {
            return "Bitcoin";
        }

        @Override
        public int decimalPrecision() {
            return 2;
        }

        @Override
        public double initialPrice() {
            return 42000.00;
        }

        @Override
        public double volatilityFactor() {
            return 0.002;
        }
    }

    record Ethereum() implements CryptoCurrency {

        @Override
        public String symbol() {
            return "ETH";
        }

        @Override
        public String displayName() {
            return "Ethereum";
        }

        @Override
        public int decimalPrecision() {
            return 2;
        }

        @Override
        public double initialPrice() {
            return 2500.00;
        }

        @Override
        public double volatilityFactor() {
            return 0.003;
        }
    }

    record Solana() implements CryptoCurrency {
        @Override
        public String symbol() {
            return "SOL";
        }

        @Override
        public String displayName() {
            return "Solana";
        }

        @Override
        public int decimalPrecision() {
            return 2;
        }

        @Override
        public double initialPrice() {
            return 100.00;
        }

        @Override
        public double volatilityFactor() {
            return 0.004;
        }
    }
}
