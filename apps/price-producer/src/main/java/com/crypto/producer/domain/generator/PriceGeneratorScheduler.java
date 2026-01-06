package com.crypto.producer.domain.generator;

import com.crypto.producer.domain.model.CryptoCurrency;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class PriceGeneratorScheduler {
    private final PriceGenerator priceGenerator;

    @Value("${crypto.producer.interval-ms:1000}")
    private long intervalMs;

    public PriceGeneratorScheduler(PriceGenerator priceGenerator) {
        this.priceGenerator = priceGenerator;
    }

    @PostConstruct
    public void logStartup() {
        String currencies = CryptoCurrency.all().stream()
                .map(c -> c.symbol() + " (" + c.displayName() + ")")
                .collect(Collectors.joining(", "));

        log.info("Price generation scheduled every {}ms for {} currencies: {}",
                intervalMs, CryptoCurrency.all().size(), currencies);
    }

    @Scheduled(fixedRateString = "${crypto.producer.interval-ms:1000}")
    public void generatePrices() {
        for (CryptoCurrency currency : CryptoCurrency.all()) {
            generatePrice(currency);
        }
    }

    private void generatePrice(CryptoCurrency currency) {
        try {
            priceGenerator.generateAndPublish(currency);
        } catch (Exception e) {
            log.error("Error generating price for {}", currency.symbol(), e);
        }
    }
}
