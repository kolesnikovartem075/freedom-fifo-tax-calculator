package com.tax.calculator;

import com.tax.calculator.position.CalculatorFactory;
import com.tax.calculator.position.entity.ClosedPosition;
import com.tax.calculator.report.ReportWriter;
import com.tax.calculator.trade.TradesFactory;
import com.tax.calculator.utils.FileReportLoader;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class TaxReportRunner {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void main(String[] args) throws IOException {
        var rates = FileReportLoader.getRatesPath();
        var brokerReport = FileReportLoader.getBrokerReport();

        var calculator = CalculatorFactory.build(rates);
        var tradeStore = TradesFactory.build(brokerReport);

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

    private static String requiredProperty(String name) {
        var value = System.getProperty(name);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required JVM property: -D" + name + "=<path>"
            );
        }

        return value;
    }

}