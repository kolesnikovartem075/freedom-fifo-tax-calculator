package com.tax.calculator.position;

import com.tax.calculator.exchange.rate.ExchangeRate;
import com.tax.calculator.exchange.rate.ExchangeRates;
import com.tax.calculator.position.entity.ClosedPosition;
import com.tax.calculator.position.entity.ProfitSummary;
import com.tax.calculator.position.entity.TradeDetail;
import com.tax.calculator.trade.unit.UnitTrade;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

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
        ExchangeRate rate = exchangeRates.find(trade.tradeDate());
        return new TradeDetail(trade.pricePerUnit(), trade.commissionPerUnit(), trade.tradeDate(), rate);
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
                .add(buy.commissionPerUnit().multiply(buy.exchangeRate().rate()))
                .add(sell.commissionPerUnit().multiply(sell.exchangeRate().rate()));
    }

    private static BigDecimal getIncomeUah(TradeDetail sell) {
        return sell.pricePerUnit().multiply(sell.exchangeRate().rate());
    }
}