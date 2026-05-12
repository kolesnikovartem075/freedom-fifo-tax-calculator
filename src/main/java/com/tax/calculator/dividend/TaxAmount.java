package com.tax.calculator.dividend;

import java.math.BigDecimal;

record TaxAmount(BigDecimal amount, String currency) {

    static final TaxAmount ZERO = new TaxAmount(BigDecimal.ZERO, null);
}
