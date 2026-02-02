package com.tax.calculator.trade;

import java.util.Queue;

public record TradeQueues(Queue<TradeRow> buys, Queue<TradeRow> sells) {
}