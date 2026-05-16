package com.tax.calculator.dividend;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DividendResult(
        LocalDate paymentDate,
        String ticker,
        String currency,
        BigDecimal grossAmount,
        BigDecimal grossAmountUah,
        BigDecimal foreignTax,
        BigDecimal foreignTaxUah,
        BigDecimal netAmount,
        BigDecimal netAmountUah
) {
}
