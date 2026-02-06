package com.tax.calculator.position;

import com.tax.calculator.exchange.rate.ExchangeRate;
import com.tax.calculator.exchange.rate.ExchangeRates;
import com.tax.calculator.trade.TradeOperation;
import com.tax.calculator.trade.unit.UnitTrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionCalculatorTest {

    private static final LocalDateTime BUY_DATE = LocalDateTime.of(2024, 1, 15, 10, 0);
    private static final LocalDateTime SELL_DATE = LocalDateTime.of(2024, 6, 20, 14, 0);

    private static final BigDecimal BUY_RATE = new BigDecimal("37.5");
    private static final BigDecimal SELL_RATE = new BigDecimal("41.0");

    @Mock
    private ExchangeRates exchangeRates;

    @InjectMocks
    private PositionCalculator calculator;

    @BeforeEach
    void setUp() {
        when(exchangeRates.find(BUY_DATE)).thenReturn(new ExchangeRate(BUY_DATE.toLocalDate(), BUY_RATE));
        when(exchangeRates.find(SELL_DATE)).thenReturn(new ExchangeRate(SELL_DATE.toLocalDate(), SELL_RATE));
    }

    @Test
    void shouldCalculateProfit() {
        var buy = unitTrade(TradeOperation.BUY, "100", "0.50", BUY_DATE);
        var sell = unitTrade(TradeOperation.SELL, "150", "0.50", SELL_DATE);

        var result = calculator.getProfit("AAPL.US", buy, sell);

        assertThat(result.profitUsd()).isEqualByComparingTo("49.00");
        assertThat(result.profitUah().income()).isEqualByComparingTo("6150");
        assertThat(result.profitUah().expense()).isEqualByComparingTo("3789.25");
        assertThat(result.profitUah().profit()).isEqualByComparingTo("2360.75");
    }


    @Test
    void shouldCalculateWithPrecision() {
        when(exchangeRates.find(BUY_DATE))
                .thenReturn(new ExchangeRate(BUY_DATE.toLocalDate(), new BigDecimal("37.5461")));
        when(exchangeRates.find(SELL_DATE))
                .thenReturn(new ExchangeRate(SELL_DATE.toLocalDate(), new BigDecimal("41.3017")));

        var buy = unitTrade(TradeOperation.BUY, "185.13", "1.07", BUY_DATE);
        var sell = unitTrade(TradeOperation.SELL, "210.17", "1.23", SELL_DATE);

        var result = calculator.getProfit("AAPL.US", buy, sell);

        assertThat(result.profitUsd()).isEqualByComparingTo("22.74");
        assertThat(result.profitUah().income()).isEqualByComparingTo("8680.378289");
        assertThat(result.profitUah().expense()).isEqualByComparingTo("7041.884911");
        assertThat(result.profitUah().profit()).isEqualByComparingTo("1638.493378");
    }

    private static UnitTrade unitTrade(TradeOperation op, String price, String commission, LocalDateTime date) {
        return new UnitTrade(op, new BigDecimal(price), new BigDecimal(commission), date);
    }
}