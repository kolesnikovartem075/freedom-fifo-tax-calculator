package com.tax.calculator.position.entity;

import java.math.BigDecimal;

public record ProfitSummary(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal profit
) {
}