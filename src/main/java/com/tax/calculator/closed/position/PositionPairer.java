package com.tax.calculator.closed.position;


import com.tax.calculator.closed.position.entity.ClosedPosition;
import com.tax.calculator.trade.unit.UnitTrade;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
public class PositionPairer {

    private final PositionCalculator positionCalculator;

    public List<ClosedPosition> matchAll(String ticker, Queue<UnitTrade> buys, Queue<UnitTrade> sells) {
        List<ClosedPosition> result = new ArrayList<>();

        while (!sells.isEmpty()) {
            var sell = sells.poll();
            var buy = buys.poll();

            if (buy == null) {
                throw new IllegalStateException("No matching buy for sell: ticker=%s".formatted(ticker));
            }

            result.add(positionCalculator.getProfit(ticker, buy, sell));
        }

        return result;
    }
}