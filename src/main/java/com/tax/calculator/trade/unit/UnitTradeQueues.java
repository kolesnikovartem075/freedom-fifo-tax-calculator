package com.tax.calculator.trade.unit;

import java.util.Queue;

public record UnitTradeQueues(
        Queue<UnitTrade> buys,
        Queue<UnitTrade> sells
) {
}