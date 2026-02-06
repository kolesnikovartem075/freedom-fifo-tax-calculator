package com.tax.calculator.trade;


import com.tax.calculator.utils.FileReportLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses Freedom Finance broker report (Excel) into a list of trade rows.
 * Reads from the sheet with "Trades" prefix, skipping headers and empty rows.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerReportParser {


    private static final String SHEET_PREFIX = "Trades";

    public static List<TradeRow> parse(String path) throws IOException {
        var file = FileReportLoader.load(path);
        return readRows(file);
    }

    private static List<TradeRow> readRows(File file) throws IOException {
        try (var workbook = new XSSFWorkbook(new FileInputStream(file))) {
            var sheet = findTradesSheet(workbook);

            var iterator = TradeRowIterator.of(sheet);
            List<TradeRow> rows = new ArrayList<>();
            while (iterator.hasNext()) {
                rows.add(iterator.next());
            }
            return rows;
        }
    }

    public static Sheet findTradesSheet(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String name = workbook.getSheetName(i);
            if (name.startsWith(SHEET_PREFIX)) {
                return workbook.getSheetAt(i);
            }
        }
        throw new IllegalArgumentException("Sheet with prefix '" + SHEET_PREFIX + "' not found");
    }
}