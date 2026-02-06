package com.tax.calculator.trade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TradeBookTest {

    private static final LocalDateTime TRADE_DATE = LocalDateTime.of(2024, 3, 15, 10, 0);


    @Test
    void shouldGroupTradesByTicker() {
        var rows = List.of(
                tradeRow("AAPL.US", TradeOperation.BUY),
                tradeRow("MSFT.US", TradeOperation.BUY),
                tradeRow("AAPL.US", TradeOperation.SELL)
        );

        var tradeBook = TradeBook.fromRows(rows);

        assertThat(tradeBook.getTickers()).containsExactly("AAPL.US", "MSFT.US");
    }

    @Test
    void shouldSeparateBuysAndSells() {
        var rows = List.of(
                tradeRow("AAPL.US", TradeOperation.BUY),
                tradeRow("AAPL.US", TradeOperation.BUY),
                tradeRow("AAPL.US", TradeOperation.SELL)
        );

        var tradeBook = TradeBook.fromRows(rows);
        var queues = tradeBook.find("AAPL.US");

        assertThat(queues.buys()).hasSize(2);
        assertThat(queues.sells()).hasSize(1);
    }

    private static TradeRow tradeRow(String ticker, TradeOperation operation) {
        return TradeRow.builder()
                .ticker(ticker)
                .operation(operation)
                .quantity(1)
                .price(new BigDecimal("100"))
                .total(new BigDecimal("100"))
                .commission(new BigDecimal("0.50"))
                .tradeDate(TRADE_DATE)
                .build();
    }
}