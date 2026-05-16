package com.tax.calculator.position;

import com.tax.calculator.exchange.rate.ExchangeRate;
import com.tax.calculator.exchange.rate.ExchangeRates;
import com.tax.calculator.position.entity.ClosedPosition;
import com.tax.calculator.position.entity.ProfitSummary;
import com.tax.calculator.position.entity.TradeDetail;
import com.tax.calculator.trade.unit.UnitTrade;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Calculates profit for a closed position (single share).
 * Computes profit in the trade currency and UAH using NBU exchange rates on trade dates.
 * Uses the trade currency rate for price conversion and the commission currency rate for commission conversion.
 */
@RequiredArgsConstructor
public class PositionCalculator {

    private final ExchangeRates exchangeRates;

    public ClosedPosition getProfit(String ticker, UnitTrade buy, UnitTrade sell) {
        TradeDetail buyDetail = buildDetail(buy);
        TradeDetail sellDetail = buildDetail(sell);

        BigDecimal profitUsd = calcProfitUsd(buyDetail, sellDetail);
        ProfitSummary profitUah = calcProfitSummary(buyDetail, sellDetail);

        return new ClosedPosition(ticker, 1, buyDetail, sellDetail, profitUsd, profitUah);
    }

    private TradeDetail buildDetail(UnitTrade trade) {
        ExchangeRate tradeRate = exchangeRates.find(trade.currency(), trade.tradeDate().toLocalDate());
        ExchangeRate commissionRate = exchangeRates.find(trade.commissionCurrency(), trade.tradeDate().toLocalDate());
        return new TradeDetail(
                trade.pricePerUnit(),
                trade.commissionPerUnit(),
                trade.tradeDate(),
                tradeRate,
                commissionRate
        );
    }

    private BigDecimal calcProfitUsd(TradeDetail buy, TradeDetail sell) {
        return sell.pricePerUnit()
                .subtract(buy.pricePerUnit())
                .subtract(buy.commissionPerUnit())
                .subtract(sell.commissionPerUnit());
    }

    private ProfitSummary calcProfitSummary(TradeDetail buy, TradeDetail sell) {
        BigDecimal income = getIncomeUah(sell);
        BigDecimal expense = getExpenseUah(buy, sell);
        BigDecimal profit = income.subtract(expense);

        return new ProfitSummary(income, expense, profit);
    }

    private static BigDecimal getExpenseUah(TradeDetail buy, TradeDetail sell) {
        return buy.pricePerUnit().multiply(buy.exchangeRate().rate())
                .add(buy.commissionPerUnit().multiply(buy.commissionExchangeRate().rate()))
                .add(sell.commissionPerUnit().multiply(sell.commissionExchangeRate().rate()));
    }

    private static BigDecimal getIncomeUah(TradeDetail sell) {
        return sell.pricePerUnit().multiply(sell.exchangeRate().rate());
    }
}