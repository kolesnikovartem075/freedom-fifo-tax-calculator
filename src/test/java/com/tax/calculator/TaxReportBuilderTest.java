package com.tax.calculator;

import com.tax.calculator.position.PositionCalculator;
import com.tax.calculator.position.entity.ClosedPosition;
import com.tax.calculator.position.entity.ProfitSummary;
import com.tax.calculator.trade.TradeOperation;
import com.tax.calculator.trade.unit.TradeStore;
import com.tax.calculator.trade.unit.UnitTrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxReportBuilderTest {

    private static final LocalDateTime BASE_DATE = LocalDateTime.of(2024, 3, 15, 10, 0);
    private static final String AAPL_US = "AAPL.US";

    @Mock
    private TradeStore tradeStore;

    @Mock
    private PositionCalculator calculator;

    @InjectMocks
    private TaxReportBuilder builder;

    private UnitTrade earlyBuy;
    private UnitTrade lateBuy;
    private UnitTrade sell;

    @BeforeEach
    void setUp() {
        earlyBuy = unitTrade(TradeOperation.BUY, "100", BASE_DATE);
        lateBuy = unitTrade(TradeOperation.BUY, "120", BASE_DATE.plusMonths(2));
        sell = unitTrade(TradeOperation.SELL, "150", BASE_DATE.plusMonths(5));

        when(tradeStore.getTickers()).thenReturn(Set.of(AAPL_US));
        when(tradeStore.getSells(AAPL_US)).thenReturn(new LinkedList<>(List.of(sell)));
    }

    @Test
    void shouldMatchBuysAndSellsInFifoOrder() {
        when(tradeStore.getBuys(AAPL_US)).thenReturn(new LinkedList<>(List.of(earlyBuy, lateBuy)));
        when(calculator.getProfit(AAPL_US, earlyBuy, sell)).thenReturn(closedPosition(AAPL_US));

        builder.collectPositions();

        verify(calculator).getProfit(AAPL_US, earlyBuy, sell);
        verifyNoMoreInteractions(calculator);
    }

    @Test
    void shouldThrowWhenNoBuyForSell() {
        when(tradeStore.getBuys(AAPL_US)).thenReturn(new LinkedList<>());

        assertThatThrownBy(() -> builder.collectPositions())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(AAPL_US);
    }

    private static UnitTrade unitTrade(TradeOperation op, String price, LocalDateTime date) {
        return new UnitTrade(op, new BigDecimal(price), new BigDecimal("0.50"), date);
    }

    private static ClosedPosition closedPosition(String ticker) {
        return new ClosedPosition(ticker, 1, null, null, BigDecimal.ZERO,
                new ProfitSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }
}