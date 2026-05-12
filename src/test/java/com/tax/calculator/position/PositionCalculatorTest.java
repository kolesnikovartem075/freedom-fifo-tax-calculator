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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionCalculatorTest {

    private static final LocalDateTime BUY_TRADE_DATE = LocalDateTime.of(2024, 1, 15, 10, 0);
    private static final LocalDateTime SELL_TRADE_DATE = LocalDateTime.of(2024, 6, 20, 14, 0);
    private static final LocalDate BUY_SETTLEMENT = LocalDate.of(2024, 1, 16);
    private static final LocalDate SELL_SETTLEMENT = LocalDate.of(2024, 6, 21);

    private static final BigDecimal BUY_RATE = new BigDecimal("37.5");
    private static final BigDecimal SELL_RATE = new BigDecimal("41.0");

    @Mock
    private ExchangeRates exchangeRates;

    @InjectMocks
    private PositionCalculator calculator;

    @BeforeEach
    void setUp() {
        when(exchangeRates.find("USD", BUY_SETTLEMENT))
                .thenReturn(new ExchangeRate(BUY_SETTLEMENT, "USD", BUY_RATE));
        when(exchangeRates.find("USD", SELL_SETTLEMENT))
                .thenReturn(new ExchangeRate(SELL_SETTLEMENT, "USD", SELL_RATE));
        when(exchangeRates.find("EUR", BUY_SETTLEMENT))
                .thenReturn(new ExchangeRate(BUY_SETTLEMENT, "EUR", BUY_RATE));
        when(exchangeRates.find("EUR", SELL_SETTLEMENT))
                .thenReturn(new ExchangeRate(SELL_SETTLEMENT, "EUR", SELL_RATE));
    }

    @Test
    void shouldCalculateProfit() {
        var buy = unitTrade(TradeOperation.BUY, "100", "0.50", BUY_TRADE_DATE, BUY_SETTLEMENT);
        var sell = unitTrade(TradeOperation.SELL, "150", "0.50", SELL_TRADE_DATE, SELL_SETTLEMENT);

        var result = calculator.getProfit("AAPL.US", buy, sell);

        assertThat(result.profitUsd()).isEqualByComparingTo("49.00");
        assertThat(result.profitUah().income()).isEqualByComparingTo("6150");
        assertThat(result.profitUah().expense()).isEqualByComparingTo("3789.25");
        assertThat(result.profitUah().profit()).isEqualByComparingTo("2360.75");
    }

    @Test
    void shouldUseDifferentRatesForTradeAndCommissionCurrencies() {
        var eurBuyRate = new BigDecimal("40.0");
        var eurSellRate = new BigDecimal("44.0");
        when(exchangeRates.find("EUR", BUY_SETTLEMENT))
                .thenReturn(new ExchangeRate(BUY_SETTLEMENT, "EUR", eurBuyRate));
        when(exchangeRates.find("EUR", SELL_SETTLEMENT))
                .thenReturn(new ExchangeRate(SELL_SETTLEMENT, "EUR", eurSellRate));

        var buy = unitTrade(TradeOperation.BUY, "100", "0.50", "USD", "EUR", BUY_TRADE_DATE, BUY_SETTLEMENT);
        var sell = unitTrade(TradeOperation.SELL, "150", "0.50", "USD", "EUR", SELL_TRADE_DATE, SELL_SETTLEMENT);

        var result = calculator.getProfit("AAPL.US", buy, sell);

        // Income = 150 * 41.0 = 6150
        assertThat(result.profitUah().income()).isEqualByComparingTo("6150");
        // Expense = (100 * 37.5) + (0.50 * 40.0) + (0.50 * 44.0) = 3750 + 20 + 22 = 3792
        assertThat(result.profitUah().expense()).isEqualByComparingTo("3792");
        assertThat(result.profitUah().profit()).isEqualByComparingTo("2358");
    }

    @Test
    void shouldCalculateWithPrecision() {
        var usdBuyRate = new BigDecimal("37.5461");
        var usdSellRate = new BigDecimal("41.3017");
        var eurBuyRate = new BigDecimal("40.1234");
        var eurSellRate = new BigDecimal("44.5678");

        when(exchangeRates.find("USD", BUY_SETTLEMENT))
                .thenReturn(new ExchangeRate(BUY_SETTLEMENT, "USD", usdBuyRate));
        when(exchangeRates.find("USD", SELL_SETTLEMENT))
                .thenReturn(new ExchangeRate(SELL_SETTLEMENT, "USD", usdSellRate));
        when(exchangeRates.find("EUR", BUY_SETTLEMENT))
                .thenReturn(new ExchangeRate(BUY_SETTLEMENT, "EUR", eurBuyRate));
        when(exchangeRates.find("EUR", SELL_SETTLEMENT))
                .thenReturn(new ExchangeRate(SELL_SETTLEMENT, "EUR", eurSellRate));

        var buy = unitTrade(TradeOperation.BUY, "185.13", "1.07", "USD", "EUR", BUY_TRADE_DATE, BUY_SETTLEMENT);
        var sell = unitTrade(TradeOperation.SELL, "210.17", "1.23", "USD", "EUR", SELL_TRADE_DATE, SELL_SETTLEMENT);

        var result = calculator.getProfit("AAPL.US", buy, sell);

        assertThat(result.profitUsd()).isEqualByComparingTo("22.74");
        // Income = 210.17 * 41.3017 = 8680.378289
        assertThat(result.profitUah().income()).isEqualByComparingTo("8680.378289");
        // Expense = (185.13 * 37.5461) + (1.07 * 40.1234) + (1.23 * 44.5678)
        //         = 6950.909493 + 42.932038 + 54.818394 = 7048.659925
        assertThat(result.profitUah().expense()).isEqualByComparingTo("7048.659925");
        assertThat(result.profitUah().profit()).isEqualByComparingTo("1631.718364");
    }

    private static UnitTrade unitTrade(TradeOperation op, String price, String commission,
                                       LocalDateTime tradeDate, LocalDate settlementDate) {
        return new UnitTrade(op, new BigDecimal(price), new BigDecimal(commission),
                "USD", "EUR", tradeDate, settlementDate);
    }

    private static UnitTrade unitTrade(TradeOperation op, String price, String commission,
                                       String currency, String commCurrency,
                                       LocalDateTime tradeDate, LocalDate settlementDate) {
        return new UnitTrade(op, new BigDecimal(price), new BigDecimal(commission),
                currency, commCurrency, tradeDate, settlementDate);
    }
}
