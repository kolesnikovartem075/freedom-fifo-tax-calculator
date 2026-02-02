package com.tax.calculator.trade.unit;

import com.tax.calculator.trade.TradeBook;
import com.tax.calculator.trade.TradeQueues;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class TradeStore {

    private final Map<String, UnitTradeQueues> trades;

    private TradeStore(Map<String, UnitTradeQueues> trades) {
        this.trades = trades;
    }

    public static TradeStore from(TradeBook tradeBook) {
        Map<String, UnitTradeQueues> result = new LinkedHashMap<>();

        for (var entry : tradeBook.getTrades().entrySet()) {
            result.put(entry.getKey(), toUnitQueues(entry.getValue()));
        }

        return new TradeStore(result);
    }

    public Set<String> getTickers() {
        return trades.keySet();
    }

    public Queue<UnitTrade> getBuys(String ticker) {
        return trades.get(ticker).buys();
    }

    public Queue<UnitTrade> getSells(String ticker) {
        return trades.get(ticker).sells();
    }

    private static UnitTradeQueues toUnitQueues(TradeQueues queues) {
        Queue<UnitTrade> buys = UnitTradeFactory.splitAll(queues.buys());
        Queue<UnitTrade> sells = UnitTradeFactory.splitAll(queues.sells());
        return new UnitTradeQueues(buys, sells);
    }
}