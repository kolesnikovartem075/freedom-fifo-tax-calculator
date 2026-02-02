package com.tax.calculator;

import com.tax.calculator.closed.position.entity.ClosedPosition;
import com.tax.calculator.report.ReportWriter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class CalculationRunner {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private static final String BROKER_REPORT_PATH = "/Users/artemkolesnikov/Downloads/1317768_2023-05-15 23_59_59_2026-01-31 23_59_59_all.xlsx";
    private static final String RATES_PATH = "/Users/artemkolesnikov/Downloads/Офіційний курс гривні щодо іноземних валют.json";

    public static void main(String[] args) throws IOException {
        var calculator = CalculatorFactory.build(RATES_PATH);
        var tradeStore = TradesFactory.build(BROKER_REPORT_PATH);

        var taxReportBuilder = TaxReportBuilder.from(calculator, tradeStore);

        writeReport(taxReportBuilder.collectPositions());
    }

    private static void writeReport(List<ClosedPosition> positions) throws IOException {
        var fileName = getFileName();
        var file = Path.of(fileName).toFile();

        ReportWriter.write(positions, file);
    }

    private static String getFileName() {
        var date = LocalDateTime.now().format(FILE_DATE_FORMAT);
        return "tax-report-%s.xlsx".formatted(date);
    }
}