package com.tax.calculator.dividend;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaxAmountParserTest {

    @Test
    void shouldParseUsdTax() {
        var result = TaxAmountParser.parse("-0.04USD");

        assertThat(result.amount()).isEqualByComparingTo("0.04");
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void shouldParseEurTax() {
        var result = TaxAmountParser.parse("0.13EUR");

        assertThat(result.amount()).isEqualByComparingTo("0.13");
        assertThat(result.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldParseZeroTax() {
        var result = TaxAmountParser.parse("0USD");

        assertThat(result.amount()).isEqualByComparingTo("0");
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void shouldParseDash() {
        var result = TaxAmountParser.parse("-");

        assertThat(result.amount()).isEqualByComparingTo("0");
        assertThat(result.currency()).isNull();
    }

    @Test
    void shouldParseBlank() {
        var result = TaxAmountParser.parse(" ");

        assertThat(result.amount()).isEqualByComparingTo("0");
    }
}
