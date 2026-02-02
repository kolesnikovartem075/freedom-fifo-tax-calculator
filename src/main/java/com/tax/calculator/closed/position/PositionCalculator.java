package com.tax.calculator.closed.position;

import com.tax.calculator.closed.position.entity.ClosedPosition;
import com.tax.calculator.closed.position.entity.TradeDetail;
import com.tax.calculator.exchange.rate.ExchangeRate;
import com.tax.calculator.exchange.rate.ExchangeRates;
import com.tax.calculator.trade.unit.UnitTrade;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class PositionCalculator {


    private final ExchangeRates exchangeRates;


    public ClosedPosition getProfit(String ticker, UnitTrade buy, UnitTrade sell) {
        TradeDetail buyDetail = buildDetail(buy);
        TradeDetail sellDetail = buildDetail(sell);

        BigDecimal profitUsd = calcProfitUsd(buyDetail, sellDetail);
        BigDecimal profitUah = calcProfitUah(buyDetail, sellDetail);

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

    private BigDecimal calcProfitUah(TradeDetail buy, TradeDetail sell) {
        BigDecimal sellPriceUah = sell.pricePerUnit().multiply(sell.exchangeRate().rate());
        BigDecimal buyPriceUah = buy.pricePerUnit().multiply(buy.exchangeRate().rate());

        BigDecimal buyCommissionUah = buy.commissionPerUnit().multiply(buy.exchangeRate().rate());
        BigDecimal sellCommissionUah = sell.commissionPerUnit().multiply(sell.exchangeRate().rate());

        return sellPriceUah.subtract(buyPriceUah)
                .subtract(buyCommissionUah)
                .subtract(sellCommissionUah)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
