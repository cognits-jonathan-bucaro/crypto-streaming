package com.crypto.producer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceUpdateTest {

    @Test
    void create_shouldCalculatePositiveChange() {
        // Given
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        BigDecimal newPrice = new BigDecimal("51000.00");
        BigDecimal previousPrice = new BigDecimal("50000.00");

        // When
        PriceUpdate update = PriceUpdate.create(btc, newPrice, previousPrice);

        // Then
        assertThat(update.change()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(update.trend()).isEqualTo(PriceTrend.UP);
    }

    @Test
    void create_shouldCalculateNegativeChange() {
        // Given
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        BigDecimal newPrice = new BigDecimal("48000.00");
        BigDecimal previousPrice = new BigDecimal("50000.00");

        // When
        PriceUpdate update = PriceUpdate.create(btc, newPrice, previousPrice);

        // Then
        assertThat(update.change()).isEqualByComparingTo(new BigDecimal("-2000.00"));
        assertThat(update.trend()).isEqualTo(PriceTrend.DOWN);
    }

    @Test
    void create_shouldHandleNoChange() {
        // Given
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        BigDecimal price = new BigDecimal("50000.00");

        // When
        PriceUpdate update = PriceUpdate.create(btc, price, price);

        // Then
        assertThat(update.change()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(update.trend()).isEqualTo(PriceTrend.STABLE);
    }

    @Test
    void create_shouldSetCorrectSymbol() {
        // Given
        CryptoCurrency eth = new CryptoCurrency.Ethereum();
        BigDecimal newPrice = new BigDecimal("3000.00");
        BigDecimal previousPrice = new BigDecimal("2900.00");

        // When
        PriceUpdate update = PriceUpdate.create(eth, newPrice, previousPrice);

        // Then
        assertThat(update.symbol()).isEqualTo("ETH");
    }
}
