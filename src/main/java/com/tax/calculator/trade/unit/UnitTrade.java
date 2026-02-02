package com.tax.calculator.trade.unit;

import com.tax.calculator.trade.TradeOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UnitTrade(
        TradeOperation operation,
        BigDecimal pricePerUnit,
        BigDecimal commissionPerUnit,
        LocalDateTime tradeDate) {
}