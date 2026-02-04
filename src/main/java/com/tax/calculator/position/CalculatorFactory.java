package com.tax.calculator.position;

import com.tax.calculator.exchange.rate.ExchangeRateParser;
import com.tax.calculator.exchange.rate.ExchangeRates;

import java.io.IOException;

public class CalculatorFactory {


    public static PositionCalculator build(String path) throws IOException {
        var rates = ExchangeRateParser.parse(path);
        var exchangeRates = ExchangeRates.from(rates);

        return new PositionCalculator(exchangeRates);
    }
}