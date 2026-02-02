package com.tax.calculator.trade;

public class TradeOperationMapper {


    private static final String BUY = "Купівля";

    public static TradeOperation map(String value) {
        return value.trim().equalsIgnoreCase(BUY)
                ? TradeOperation.BUY
                : TradeOperation.SELL;
    }
}
