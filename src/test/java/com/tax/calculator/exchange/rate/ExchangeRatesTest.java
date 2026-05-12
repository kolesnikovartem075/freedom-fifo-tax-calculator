package com.tax.calculator.exchange.rate;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeRatesTest {

    private static final LocalDate DATE = LocalDate.of(2024, 3, 15);
    private static final ExchangeRate USD_RATE = new ExchangeRate(DATE, "USD", new BigDecimal("41.3017"));
    private static final ExchangeRate EUR_RATE = new ExchangeRate(DATE, "EUR", new BigDecimal("44.5678"));

    private final ExchangeRates exchangeRates = ExchangeRates.from(Map.of(
            "USD", Map.of(DATE, USD_RATE),
            "EUR", Map.of(DATE, EUR_RATE)
    ));

    @Test
    void shouldFindRateByDate() {
        var result = exchangeRates.find("USD", DATE);

        assertThat(result.rate()).isEqualByComparingTo("41.3017");
    }

    @Test
    void shouldFindEurRate() {
        var result = exchangeRates.find("EUR", DATE);

        assertThat(result.rate()).isEqualByComparingTo("44.5678");
    }

    @Test
    void shouldThrowWhenRateNotFound() {
        var missingDate = LocalDate.of(2025, 1, 1);

        assertThatThrownBy(() -> exchangeRates.find("USD", missingDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2025-01-01");
    }

    @Test
    void shouldThrowWhenCurrencyNotFound() {
        assertThatThrownBy(() -> exchangeRates.find("GBP", DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GBP");
    }
}
