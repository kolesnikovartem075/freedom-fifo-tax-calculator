package com.tax.calculator.position.entity;

import java.math.BigDecimal;

public record ClosedPosition(
        String ticker,
        int quantity,
        TradeDetail buy,
        TradeDetail sell,
        BigDecimal profitUsd,
        BigDecimal profitUah
) {
}
