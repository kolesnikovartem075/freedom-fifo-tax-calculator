package com.tax.calculator;

import com.tax.calculator.closed.position.entity.ClosedPosition;
import com.tax.calculator.report.ReportWriter;
import com.tax.calculator.utils.FileReportLoader;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class CalculationRunner {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java -jar tax-calculator.jar <broker-report.xlsx> <rates.json>");
            return;
        }

        var brokerReportPath = FileReportLoader.getAbsolutePath(args[0]);
        var ratesPath = FileReportLoader.getAbsolutePath(args[1]);

        var calculator = CalculatorFactory.build(brokerReportPath);
        var tradeStore = TradesFactory.build(ratesPath);

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