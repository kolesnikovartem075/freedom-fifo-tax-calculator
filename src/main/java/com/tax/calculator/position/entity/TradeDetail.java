package com.tax.calculator.position.entity;


import com.tax.calculator.exchange.rate.ExchangeRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TradeDetail(
        BigDecimal pricePerUnit,
        BigDecimal commissionPerUnit,
        LocalDateTime tradeDate,
        LocalDate settlementDate,
        ExchangeRate exchangeRate,
        ExchangeRate commissionExchangeRate
) {
}
