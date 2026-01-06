package com.crypto.producer.domain.generator;

import com.crypto.producer.domain.event.PriceEvent;
import com.crypto.producer.domain.model.CryptoCurrency;
import com.crypto.producer.domain.port.PricePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceGeneratorTest {

    @Mock
    private RandomWalkStrategy randomWalkStrategy;

    @Mock
    private PricePublisher pricePublisher;

    private PriceGenerator priceGenerator;

    @BeforeEach
    void setUp() {
        priceGenerator = new PriceGenerator(randomWalkStrategy, pricePublisher);
    }

    @Test
    void generateAndPublish_shouldCallStrategyAndPublisher() {
        // Given
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();
        BigDecimal newPrice = new BigDecimal("51000.00");
        when(randomWalkStrategy.nextPrice(btc)).thenReturn(newPrice);
        when(pricePublisher.publish(any(PriceEvent.class)))
                .thenReturn(CompletableFuture.completedFuture("msg-id"));

        // When
        priceGenerator.generateAndPublish(btc);

        // Then
        verify(randomWalkStrategy).nextPrice(btc);
        verify(pricePublisher).publish(any(PriceEvent.class));
    }
}
