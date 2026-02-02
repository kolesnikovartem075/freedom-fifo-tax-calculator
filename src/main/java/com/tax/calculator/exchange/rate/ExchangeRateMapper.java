package com.tax.calculator.exchange.rate;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExchangeRateMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String DATE_KEY = "Дата";
    private static final String RATE_KEY = "Офіційний курс гривні, грн";


    public static ExchangeRate map(JsonNode entry) {
        var date = LocalDate.parse(entry.get(DATE_KEY).asText(), DATE_FORMAT);
        BigDecimal rate = entry.get(RATE_KEY).decimalValue();

        return new ExchangeRate(date, rate);
    }
}
