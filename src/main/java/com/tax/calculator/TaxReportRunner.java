package com.tax.calculator;

import com.tax.calculator.dividend.DividendCalculator;
import com.tax.calculator.dividend.DividendParser;
import com.tax.calculator.dividend.DividendResult;
import com.tax.calculator.exchange.rate.ExchangeRateParser;
import com.tax.calculator.exchange.rate.ExchangeRates;
import com.tax.calculator.position.PositionCalculator;
import com.tax.calculator.position.entity.ClosedPosition;
import com.tax.calculator.report.ReportWriter;
import com.tax.calculator.trade.TradesFactory;
import com.tax.calculator.utils.FileReportLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class TaxReportRunner {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static void main(String[] args) throws IOException {
        log.info("Freedom FIFO Tax Calculator v1.0-BETA");

        var ratesPaths = FileReportLoader.getRatesPaths();
        var brokerReport = FileReportLoader.getBrokerReport();

        var rates = ExchangeRateParser.parse(ratesPaths);
        var exchangeRates = ExchangeRates.from(rates);

        var calculator = new PositionCalculator(exchangeRates);
        var tradeStore = TradesFactory.build(brokerReport);
        var taxReportBuilder = TaxReportBuilder.from(calculator, tradeStore);
        List<ClosedPosition> positions = taxReportBuilder.collectPositions();

        var dividendCalculator = new DividendCalculator(exchangeRates);
        var dividendRows = DividendParser.parse(brokerReport);
        List<DividendResult> dividends = dividendCalculator.calculate(dividendRows);

        writeReport(positions, dividends);
    }

    private static void writeReport(List<ClosedPosition> positions,
                                    List<DividendResult> dividends) throws IOException {
        var fileName = getFileName();
        var file = Path.of(fileName);
        Files.createDirectories(file.getParent());

        ReportWriter.write(positions, dividends, file.toFile());
        log.info("Report written: {}", file);
    }

    private static String getFileName() {
        var date = LocalDateTime.now().format(FILE_DATE_FORMAT);
        return "reports/tax-report-%s.xlsx".formatted(date);
    }

}
