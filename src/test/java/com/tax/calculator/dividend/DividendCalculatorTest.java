package com.tax.calculator.dividend;

import com.tax.calculator.exchange.rate.ExchangeRate;
import com.tax.calculator.exchange.rate.ExchangeRates;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DividendCalculatorTest {

    private static final LocalDate DATE = LocalDate.of(2024, 9, 4);
    private static final BigDecimal USD_RATE = new BigDecimal("41.2");
    private static final BigDecimal EUR_RATE = new BigDecimal("45.5");

    private final ExchangeRates rates = ExchangeRates.from(Map.of(
            "USD", Map.of(DATE, new ExchangeRate(DATE, "USD", USD_RATE)),
            "EUR", Map.of(DATE, new ExchangeRate(DATE, "EUR", EUR_RATE))
    ));

    private final DividendCalculator calculator = new DividendCalculator(rates);

    @Test
    void shouldCalculateUsdDividendWithBrokerTax() {
        var row = new DividendRow(
                DATE, "INTC.US", new BigDecimal("0.21"),
                new BigDecimal("0.125"), "USD", 2,
                BigDecimal.ZERO, null,
                new BigDecimal("0.04"), "USD"
        );

        var results = calculator.calculate(List.of(row));

        assertThat(results).hasSize(1);
        var result = results.getFirst();
        // gross = 0.125 * 2 = 0.25
        assertThat(result.grossAmount()).isEqualByComparingTo("0.250");
        // gross UAH = 0.25 * 41.2 = 10.30
        assertThat(result.grossAmountUah()).isEqualByComparingTo("10.30");
        // foreign tax = 0 + 0.04 = 0.04
        assertThat(result.foreignTax()).isEqualByComparingTo("0.04");
        // foreign tax UAH = 0.04 * 41.2 = 1.648
        assertThat(result.foreignTaxUah()).isEqualByComparingTo("1.648");
    }

    @Test
    void shouldCalculateEurDividendWithSourceTax() {
        var row = new DividendRow(
                DATE, "RY4C.EU", new BigDecimal("0.40"),
                new BigDecimal("0.178"), "EUR", 3,
                new BigDecimal("0.13"), "EUR",
                BigDecimal.ZERO, null
        );

        var results = calculator.calculate(List.of(row));

        var result = results.getFirst();
        // gross = 0.178 * 3 = 0.534
        assertThat(result.grossAmount()).isEqualByComparingTo("0.534");
        // gross UAH = 0.534 * 45.5 = 24.297
        assertThat(result.grossAmountUah()).isEqualByComparingTo("24.297");
        // foreign tax = 0.13 EUR
        assertThat(result.foreignTax()).isEqualByComparingTo("0.13");
        // foreign tax UAH = 0.13 * 45.5 = 5.915
        assertThat(result.foreignTaxUah()).isEqualByComparingTo("5.915");
    }
}
