package com.tax.calculator.trade.unit;

import java.util.Queue;

public record TickerTrades(
        String ticker,
        Queue<UnitTrade> buys,
        Queue<UnitTrade> sells) {
}