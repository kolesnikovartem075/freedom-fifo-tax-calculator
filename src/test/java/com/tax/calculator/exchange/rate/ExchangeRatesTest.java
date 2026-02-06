package com.tax.calculator.exchange.rate;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeRatesTest {

    private static final LocalDate DATE = LocalDate.of(2024, 3, 15);
    private static final ExchangeRate RATE = new ExchangeRate(DATE, new BigDecimal("41.3017"));

    private final ExchangeRates exchangeRates = ExchangeRates.from(Map.of(DATE, RATE));

    @Test
    void shouldFindRateByDate() {
        var result = exchangeRates.find(LocalDateTime.of(DATE, LocalTime.of(1,1,1)));

        assertThat(result.rate()).isEqualByComparingTo("41.3017");
    }

    @Test
    void shouldThrowWhenRateNotFound() {
        var missingDate = LocalDateTime.of(2025, 1, 1, 10, 0);

        assertThatThrownBy(() -> exchangeRates.find(missingDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2025-01-01");
    }
}