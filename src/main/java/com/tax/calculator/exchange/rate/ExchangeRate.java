package com.tax.calculator.exchange.rate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRate(
        LocalDate date,
        BigDecimal rate
) {
}
