package com.tax.calculator.position;

import com.tax.calculator.exchange.rate.ExchangeRateParser;
import com.tax.calculator.exchange.rate.ExchangeRates;

import java.io.IOException;

public class CalculatorFactory {

    public static PositionCalculator build(String... paths) throws IOException {
        var rates = ExchangeRateParser.parse(paths);
        var exchangeRates = ExchangeRates.from(rates);

        return new PositionCalculator(exchangeRates);
    }
}
