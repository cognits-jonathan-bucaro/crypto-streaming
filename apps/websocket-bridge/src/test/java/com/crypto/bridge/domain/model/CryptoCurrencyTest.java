package com.crypto.bridge.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CryptoCurrencyTest {

    @Test
    void fromSymbol_shouldReturnBitcoinForBTC() {
        CryptoCurrency currency = CryptoCurrency.fromSymbol("BTC");

        assertInstanceOf(CryptoCurrency.Bitcoin.class, currency);
        assertEquals("BTC", currency.symbol());
        assertEquals("Bitcoin", currency.displayName());
    }

    @Test
    void fromSymbol_shouldReturnEthereumForETH() {
        CryptoCurrency currency = CryptoCurrency.fromSymbol("ETH");

        assertInstanceOf(CryptoCurrency.Ethereum.class, currency);
        assertEquals("ETH", currency.symbol());
        assertEquals("Ethereum", currency.displayName());
    }

    @Test
    void fromSymbol_shouldReturnSolanaForSOL() {
        CryptoCurrency currency = CryptoCurrency.fromSymbol("SOL");

        assertInstanceOf(CryptoCurrency.Solana.class, currency);
        assertEquals("SOL", currency.symbol());
        assertEquals("Solana", currency.displayName());
    }

    @Test
    void fromSymbol_shouldBeCaseInsensitive() {
        assertEquals("BTC", CryptoCurrency.fromSymbol("btc").symbol());
        assertEquals("ETH", CryptoCurrency.fromSymbol("eth").symbol());
        assertEquals("SOL", CryptoCurrency.fromSymbol("sol").symbol());
    }

    @Test
    void fromSymbol_shouldThrowExceptionForInvalidSymbol() {
        assertThrows(IllegalArgumentException.class, () -> CryptoCurrency.fromSymbol("INVALID"));
    }

    @Test
    void all_shouldReturnAllCryptocurrencies() {
        List<CryptoCurrency> all = CryptoCurrency.all();

        assertEquals(3, all.size());
        assertTrue(all.stream().anyMatch(c -> c instanceof CryptoCurrency.Bitcoin));
        assertTrue(all.stream().anyMatch(c -> c instanceof CryptoCurrency.Ethereum));
        assertTrue(all.stream().anyMatch(c -> c instanceof CryptoCurrency.Solana));
    }

    @Test
    void bitcoin_shouldHaveCorrectProperties() {
        CryptoCurrency btc = new CryptoCurrency.Bitcoin();

        assertEquals("BTC", btc.symbol());
        assertEquals("Bitcoin", btc.displayName());
        assertEquals(2, btc.decimalPrecision());
        assertEquals(42000.00, btc.initialPrice());
        assertEquals(0.002, btc.volatilityFactor());
    }

    @Test
    void ethereum_shouldHaveCorrectProperties() {
        CryptoCurrency eth = new CryptoCurrency.Ethereum();

        assertEquals("ETH", eth.symbol());
        assertEquals("Ethereum", eth.displayName());
        assertEquals(2, eth.decimalPrecision());
        assertEquals(2500.00, eth.initialPrice());
        assertEquals(0.003, eth.volatilityFactor());
    }

    @Test
    void solana_shouldHaveCorrectProperties() {
        CryptoCurrency sol = new CryptoCurrency.Solana();

        assertEquals("SOL", sol.symbol());
        assertEquals("Solana", sol.displayName());
        assertEquals(2, sol.decimalPrecision());
        assertEquals(100.00, sol.initialPrice());
        assertEquals(0.004, sol.volatilityFactor());
    }
}
