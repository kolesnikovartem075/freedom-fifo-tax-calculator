package com.tax.calculator.trade;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeRow {
    private final String ticker;
    private final TradeOperation operation;
    private final int quantity;
    private final BigDecimal price;
    private final BigDecimal total;
    private final BigDecimal commission;
    private final LocalDateTime tradeDate;
}