package com.tax.calculator.exchange.rate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * Lookup for official NBU exchange rates by date.
 * Throws if rate is missing for the requested date.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRates {

    private final Map<String, Map<LocalDate, ExchangeRate>> ratesByCurrency;

    public static ExchangeRates from(Map<String, Map<LocalDate, ExchangeRate>> ratesByCurrency) {
        return new ExchangeRates(ratesByCurrency);
    }

    public ExchangeRate find(String currency, LocalDate date) {
        var currencyRates = ratesByCurrency.get(currency);
        if (currencyRates == null) {
            throw new IllegalArgumentException("No exchange rates loaded for currency: " + currency);
        }

        ExchangeRate rate = currencyRates.get(date);
        if (rate == null) {
            throw new IllegalArgumentException(
                    "Exchange rate not found for currency %s on date: %s".formatted(currency, date));
        }

        return rate;
    }
}
