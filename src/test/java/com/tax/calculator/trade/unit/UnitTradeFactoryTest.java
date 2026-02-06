package com.tax.calculator.trade.unit;

import com.tax.calculator.trade.TradeOperation;
import com.tax.calculator.trade.TradeRow;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UnitTradeFactoryTest {

    private static final LocalDateTime TRADE_DATE = LocalDateTime.of(2024, 3, 15, 10, 0);

    @Test
    void shouldSplitTradeIntoUnitTrades() {
        var row = tradeRow(TradeOperation.BUY, 10, "150.00", "3.00");

        var result = UnitTradeFactory.split(row);

        assertThat(result).hasSize(10);
        assertThat(result).allMatch(t -> t.operation().equals(TradeOperation.BUY));
        assertThat(result).allMatch(t -> t.tradeDate().equals(TRADE_DATE));
    }

    @Test
    void shouldCalculateCommissionPerUnit() {
        var row = tradeRow(TradeOperation.BUY, 10, "150.00", "3.00");

        var result = UnitTradeFactory.split(row);

        assertThat(result).allSatisfy(unit -> {
            assertThat(unit.pricePerUnit()).isEqualByComparingTo("150.00");
            assertThat(unit.commissionPerUnit()).isEqualByComparingTo("0.30");
        });
    }

    @Test
    void shouldCreateSingleUnitForQuantityOne() {
        var row = tradeRow(TradeOperation.SELL, 1, "210.17", "1.23");

        var result = UnitTradeFactory.split(row);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().pricePerUnit()).isEqualByComparingTo("210.17");
        assertThat(result.getFirst().commissionPerUnit()).isEqualByComparingTo("1.23");
    }

    private static TradeRow tradeRow(TradeOperation operation, int quantity, String price, String commission) {
        return TradeRow.builder()
                .ticker("AAPL.US")
                .operation(operation)
                .quantity(quantity)
                .price(new BigDecimal(price))
                .total(new BigDecimal(price).multiply(BigDecimal.valueOf(quantity)))
                .commission(new BigDecimal(commission))
                .tradeDate(TRADE_DATE)
                .build();
    }
}