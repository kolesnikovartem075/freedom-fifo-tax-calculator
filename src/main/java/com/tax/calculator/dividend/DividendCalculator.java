package com.tax.calculator.dividend;

import com.tax.calculator.exchange.rate.ExchangeRates;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class DividendCalculator {

    private final ExchangeRates exchangeRates;

    public List<DividendResult> calculate(List<DividendRow> rows) {
        return rows.stream()
                .map(this::calculate)
                .toList();
    }

    private DividendResult calculate(DividendRow row) {
        var rate = exchangeRates.find(row.currency(), row.paymentDate()).rate();

        var grossAmount = row.grossAmount();
        var grossAmountUah = grossAmount.multiply(rate);

        var foreignTax = calcForeignTax(row, rate);
        var foreignTaxUah = calcForeignTaxUah(row, rate);

        var netAmountUah = row.netAmount().multiply(rate);

        return new DividendResult(
                row.paymentDate(), row.ticker(), row.currency(),
                grossAmount, grossAmountUah,
                foreignTax, foreignTaxUah,
                row.netAmount(), netAmountUah
        );
    }

    private BigDecimal calcForeignTax(DividendRow row, BigDecimal dividendRate) {
        return row.sourceTax().add(convertTaxToDividendCurrency(row, dividendRate));
    }

    private BigDecimal calcForeignTaxUah(DividendRow row, BigDecimal dividendRate) {
        var sourceTaxUah = convertToUah(row.sourceTax(), row.sourceTaxCurrency(), row.paymentDate());
        var brokerTaxUah = convertToUah(row.brokerTax(), row.brokerTaxCurrency(), row.paymentDate());
        return sourceTaxUah.add(brokerTaxUah);
    }

    private BigDecimal convertTaxToDividendCurrency(DividendRow row, BigDecimal dividendRate) {
        if (row.brokerTax().signum() == 0) {
            return BigDecimal.ZERO;
        }
        String taxCurrency = row.brokerTaxCurrency();
        if (taxCurrency == null || taxCurrency.equals(row.currency())) {
            return row.brokerTax();
        }
        // Convert broker tax from its currency to UAH, then back to dividend currency
        var taxRate = exchangeRates.find(taxCurrency, row.paymentDate()).rate();
        var taxUah = row.brokerTax().multiply(taxRate);
        return taxUah.divide(dividendRate, 6, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal convertToUah(BigDecimal amount, String currency, java.time.LocalDate date) {
        if (amount.signum() == 0 || currency == null) {
            return BigDecimal.ZERO;
        }
        var rate = exchangeRates.find(currency, date).rate();
        return amount.multiply(rate);
    }
}
