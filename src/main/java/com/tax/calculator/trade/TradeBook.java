package com.tax.calculator.trade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * Groups raw broker trade rows by ticker into buy and sell queues.
 * Preserves insertion order using LinkedHashMap.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeBook {

    private final Map<String, TradeQueues> trades = new LinkedHashMap<>();

    public static TradeBook fromRows(List<TradeRow> rows) {
        var entity = new TradeBook();
        rows.forEach(entity::addToGroup);
        return entity;
    }

    public Set<String> getTickers() {
        return trades.keySet();
    }

    public TradeQueues find(String ticker) {
        return trades.get(ticker);
    }


    private void addToGroup(TradeRow row) {
        var tickerTrades = getOrCreate(row.getTicker());
        var queue = getQueue(row, tickerTrades);
        queue.add(row);
    }

    private static Queue<TradeRow> getQueue(TradeRow row, TradeQueues tickerTrades) {
        return row.getOperation() == TradeOperation.BUY
                ? tickerTrades.buys()
                : tickerTrades.sells();
    }

    private TradeQueues getOrCreate(String ticker) {
        return trades.computeIfAbsent(ticker,
                k -> new TradeQueues(new LinkedList<>(), new LinkedList<>()));
    }
}
