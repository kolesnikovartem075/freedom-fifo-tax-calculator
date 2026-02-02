package com.tax.calculator.exchange.rate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.calculator.utils.FileReportLoader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;


public class ExchangeRateParser {

    public static Map<LocalDate, ExchangeRate> parse(String path) throws IOException {
        var file = FileReportLoader.load(path);
        var jsonNode = getJsonNode(file);
        return mapFromJson(jsonNode);
    }

    private static Map<LocalDate, ExchangeRate> mapFromJson(JsonNode jsonNode) {
        return jsonNode.valueStream()
                .map(ExchangeRateMapper::map)
                .collect(Collectors.toMap(
                        ExchangeRate::date,
                        identity()
                ));
    }

    private static JsonNode getJsonNode(File json) throws IOException {
        return new ObjectMapper().readTree(json);
    }
}