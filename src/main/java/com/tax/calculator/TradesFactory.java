package com.tax.calculator;

import com.tax.calculator.trade.BrokerReportParser;
import com.tax.calculator.trade.TradeBook;
import com.tax.calculator.trade.unit.TradeStore;

import java.io.IOException;

public class TradesFactory {

    public static TradeStore build(String path) throws IOException {
        var rows = BrokerReportParser.parse(path);
        var tradeBook = TradeBook.fromRows(rows);

        return TradeStore.from(tradeBook);
    }
}
