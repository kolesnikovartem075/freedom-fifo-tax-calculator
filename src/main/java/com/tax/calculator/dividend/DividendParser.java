package com.tax.calculator.dividend;

import com.tax.calculator.utils.FileReportLoader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DividendParser {

    private static final String SHEET_PREFIX = "Corpactions";
    private static final String DIVIDEND_TYPE = "Дивіденди";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<DividendRow> parse(String path) throws IOException {
        var file = FileReportLoader.load(path);
        return readRows(file);
    }

    private static List<DividendRow> readRows(File file) throws IOException {
        try (var workbook = new XSSFWorkbook(new FileInputStream(file))) {
            var sheet = findSheet(workbook);
            return parseSheet(sheet);
        }
    }

    private static List<DividendRow> parseSheet(Sheet sheet) {
        List<DividendRow> rows = new ArrayList<>();
        var iterator = sheet.iterator();

        // skip header
        if (iterator.hasNext()) {
            iterator.next();
        }

        while (iterator.hasNext()) {
            Row row = iterator.next();
            Cell typeCell = row.getCell(0);
            if (typeCell == null || !DIVIDEND_TYPE.equals(typeCell.getStringCellValue().trim())) {
                continue;
            }
            rows.add(mapRow(row));
        }

        return rows;
    }

    private static DividendRow mapRow(Row row) {
        var paymentDate = LocalDate.parse(getString(row, 1).trim(), DATE_FORMAT);
        var netAmount = getBigDecimal(row, 3);
        var perShare = getBigDecimal(row, 4);
        var currency = getString(row, 5).trim();
        var ticker = getString(row, 6).trim();
        var shares = (int) row.getCell(9).getNumericCellValue();

        var sourceTax = TaxAmountParser.parse(getString(row, 10));
        var brokerTax = TaxAmountParser.parse(getString(row, 11));

        return new DividendRow(
                paymentDate, ticker, netAmount, perShare, currency, shares,
                sourceTax.amount(), sourceTax.currency(),
                brokerTax.amount(), brokerTax.currency()
        );
    }

    private static String getString(Row row, int col) {
        Cell cell = row.getCell(col);
        return cell.getStringCellValue();
    }

    private static BigDecimal getBigDecimal(Row row, int col) {
        Cell cell = row.getCell(col);
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    private static Sheet findSheet(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (workbook.getSheetName(i).startsWith(SHEET_PREFIX)) {
                return workbook.getSheetAt(i);
            }
        }
        throw new IllegalArgumentException("Sheet with prefix '" + SHEET_PREFIX + "' not found");
    }
}
