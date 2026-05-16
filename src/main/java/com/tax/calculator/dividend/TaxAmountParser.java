package com.tax.calculator.dividend;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TaxAmountParser {

    private static final Pattern TAX_PATTERN = Pattern.compile("^(-?[\\d.]+)([A-Z]{3})$");

    static TaxAmount parse(String value) {
        if (value == null || value.isBlank() || value.equals("-")) {
            return TaxAmount.ZERO;
        }

        Matcher matcher = TAX_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Cannot parse tax amount: " + value);
        }

        BigDecimal amount = new BigDecimal(matcher.group(1)).abs();
        String currency = matcher.group(2);
        return new TaxAmount(amount, currency);
    }
}
