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
    private static final List<String> CURRENCY_KEYS = List.of("Код літерний", "Letter code");


    public static ExchangeRate map(JsonNode entry) {
        var dateKey = findKey(entry, DATE_KEYS, "date");
        var rateKey = findKey(entry, RATE_KEYS, "rate");
        var currencyKey = findKey(entry, CURRENCY_KEYS, "currency");

        var date = LocalDate.parse(entry.get(dateKey).asText(), DATE_FORMAT);
        var currency = entry.get(currencyKey).asText().trim();
        BigDecimal rate = entry.get(rateKey).decimalValue();

        return new ExchangeRate(date, currency, rate);
    }

    private static String findKey(JsonNode entry, List<String> keys, String label) {
        return keys.stream()
                .filter(entry::has)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Entry contains none of the expected %s keys: %s".formatted(label, keys)));
    }
}
