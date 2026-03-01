package com.tax.calculator.exchange.rate;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExchangeRateMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final List<String> DATE_KEYS = List.of("Дата", "Date");
    private static final List<String> RATE_KEYS = List.of("Офіційний курс гривні, грн", "Official hrivnya exchange rates, UAH");


    public static ExchangeRate map(JsonNode entry) {
        var dateKey = getDateKey(entry);
        var rateKey = getRateKey(entry);

        var date = LocalDate.parse(entry.get(dateKey).asText(), DATE_FORMAT);
        BigDecimal rate = entry.get(rateKey).decimalValue();

        return new ExchangeRate(date, rate);
    }

    private static String getRateKey(JsonNode entry) {
        return RATE_KEYS.stream().filter(entry::has)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Entry contains none of the expected rate keys: " + RATE_KEYS));
    }

    private static String getDateKey(JsonNode entry) {
        return DATE_KEYS.stream()
                .filter(entry::has)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Entry contains none of the expected date keys: " + DATE_KEYS));
    }
}
