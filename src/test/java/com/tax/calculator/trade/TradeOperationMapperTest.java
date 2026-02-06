package com.tax.calculator.trade;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TradeOperationMapperTest {

    @Test
    void shouldMapBuyOperation() {
        var result = TradeOperationMapper.map("Купівля");

        assertThat(result).isEqualTo(TradeOperation.BUY);
    }

    @Test
    void shouldMapSellOperation() {
        var result = TradeOperationMapper.map("Продаж");

        assertThat(result).isEqualTo(TradeOperation.SELL);
    }
}