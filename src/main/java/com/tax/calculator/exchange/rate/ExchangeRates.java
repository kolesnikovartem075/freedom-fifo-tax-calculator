package com.tax.calculator.exchange.rate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRates {

    private final Map<LocalDate, ExchangeRate> rates;

    public static ExchangeRates from(Map<LocalDate, ExchangeRate> map) {
        return new ExchangeRates(map);
    }

    public ExchangeRate find(LocalDateTime dateTime) {
        return find(dateTime.toLocalDate());
    }

    public ExchangeRate find(LocalDate date) {
        ExchangeRate rate = rates.get(date);
        if (rate == null) {
            throw new IllegalArgumentException("Exchange rate not found for date: " + date);
        }

        return rate;
    }
}
