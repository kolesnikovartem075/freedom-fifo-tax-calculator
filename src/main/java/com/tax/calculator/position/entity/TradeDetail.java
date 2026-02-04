package com.tax.calculator.position.entity;


import com.tax.calculator.exchange.rate.ExchangeRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeDetail(
        BigDecimal pricePerUnit,
        BigDecimal commissionPerUnit,
        LocalDateTime tradeDate,
        ExchangeRate exchangeRate
) {
}
