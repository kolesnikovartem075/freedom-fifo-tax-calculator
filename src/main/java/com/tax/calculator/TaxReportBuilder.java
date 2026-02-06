package com.tax.calculator;

import com.tax.calculator.position.PositionCalculator;
import com.tax.calculator.position.entity.ClosedPosition;
import com.tax.calculator.trade.unit.TradeStore;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Matches buy and sell trades using FIFO (First In, First Out) method.
 * Iterates over each ticker and pairs sells with the earliest available buys.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaxReportBuilder {

    private final TradeStore tradeStore;
    private final PositionCalculator calculator;

    public static TaxReportBuilder from(PositionCalculator calculator, TradeStore tradeStore) {
        return new TaxReportBuilder(tradeStore, calculator);
    }

    public List<ClosedPosition> collectPositions() {
        List<ClosedPosition> result = new ArrayList<>();

        for (String ticker : tradeStore.getTickers()) {
            result.addAll(getClosedPositions(ticker));
        }

        return result;
    }

    private List<ClosedPosition> getClosedPositions(String ticker) {
        List<ClosedPosition> result = new ArrayList<>();

        var buys = tradeStore.getBuys(ticker);
        var sells = tradeStore.getSells(ticker);

        while (!sells.isEmpty()) {
            var sell = sells.poll();
            var buy = buys.poll();
            if (buy == null) {
                throw new IllegalStateException("No matching buy for sell: ticker=%s".formatted(ticker));
            }

            result.add(calculator.getProfit(ticker, buy, sell));
        }

        return result;
    }
}
