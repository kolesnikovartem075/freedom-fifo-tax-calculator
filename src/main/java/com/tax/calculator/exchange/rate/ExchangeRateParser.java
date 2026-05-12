package com.tax.calculator.exchange.rate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.calculator.utils.FileReportLoader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateParser {

    public static Map<String, Map<LocalDate, ExchangeRate>> parse(String... paths) throws IOException {
        Map<String, Map<LocalDate, ExchangeRate>> result = new HashMap<>();

        for (String path : paths) {
            var file = FileReportLoader.load(path);
            var jsonNode = getJsonNode(file);
            addRates(jsonNode, result);
        }

        return result;
    }

    private static void addRates(JsonNode jsonNode, Map<String, Map<LocalDate, ExchangeRate>> result) {
        jsonNode.forEach(entry -> {
            ExchangeRate rate = ExchangeRateMapper.map(entry);
            result.computeIfAbsent(rate.currency(), k -> new HashMap<>())
                    .put(rate.date(), rate);
        });
    }

    private static JsonNode getJsonNode(File json) throws IOException {
        return new ObjectMapper().readTree(json);
    }
}
