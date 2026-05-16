package com.tax.calculator.dividend;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DividendRow(
        LocalDate paymentDate,
        String ticker,
        BigDecimal netAmount,
        BigDecimal perShare,
        String currency,
        int shares,
        BigDecimal sourceTax,
        String sourceTaxCurrency,
        BigDecimal brokerTax,
        String brokerTaxCurrency
) {
    public BigDecimal grossAmount() {
        return perShare.multiply(BigDecimal.valueOf(shares));
    }
}
