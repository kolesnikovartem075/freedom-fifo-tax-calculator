package com.tax.calculator.trade.unit;

import com.tax.calculator.trade.TradeRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UnitTradeFactory {


    public static Queue<UnitTrade> splitAll(Queue<TradeRow> rows) {
        return rows.stream()
                .flatMap(row -> split(row).stream())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<UnitTrade> split(TradeRow row) {
        var commissionPerUnit = getCommissionPerUnit(row);

        return IntStream.range(0, row.getQuantity())
                .mapToObj(i -> create(commissionPerUnit, row))
                .toList();
    }

    private static UnitTrade create(BigDecimal commissionPerUnit, TradeRow row) {
        return new UnitTrade(
                row.getOperation(),
                row.getPrice(),
                commissionPerUnit,
                row.getTradeDate()
        );
    }

    private static BigDecimal getCommissionPerUnit(TradeRow row) {
        var quantity = BigDecimal.valueOf(row.getQuantity());
        return row.getCommission()
                .divide(quantity, 6, RoundingMode.HALF_UP);
    }
}
