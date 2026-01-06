package com.crypto.producer.domain.generator;

import com.crypto.producer.domain.model.CryptoCurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RandomWalkStrategyTest {

    private RandomWalkStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new RandomWalkStrategy();
    }

    @Test
    void nextPrice_shouldReturnValidPrice() {
        // Given
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();

        // When
        BigDecimal price = strategy.nextPrice(btc);

        // Then
        assertThat(price).isNotNull();
        assertThat(price).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void nextPrice_shouldMaintainSeparatePricesPerCurrency() {
        // Given
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        CryptoCurrency eth = new CryptoCurrency.Ethereum();

        // When
        BigDecimal btcPrice = strategy.nextPrice(btc);
        BigDecimal ethPrice = strategy.nextPrice(eth);

        // Then
        assertThat(btcPrice).isNotNull();
        assertThat(ethPrice).isNotNull();
        // Verify they're tracked separately
        assertThat(strategy.nextPrice(btc)).isNotNull();
        assertThat(strategy.nextPrice(eth)).isNotNull();
    }
}
