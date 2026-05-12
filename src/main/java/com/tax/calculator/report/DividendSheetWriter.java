package com.tax.calculator.report;

import com.tax.calculator.dividend.DividendResult;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class DividendSheetWriter {

    static final String SHEET_NAME = "Dividends";

    private static final String[] HEADERS = {
            "Тікер",
            "Дата виплати",
            "Валюта",
            "Дивіденд (брутто)",
            "Податок за кордоном",
            "Дивіденд (нетто)",
            "Дохід (UAH)",
            "Податок за кордоном (UAH)",
            "Нетто (UAH)"
    };

    private static final int[] SUM_COLS = {3, 4, 5, 6, 7, 8};

    public void write(Workbook workbook, List<DividendResult> dividends, CellFormat format) {
        Sheet sheet = workbook.createSheet(SHEET_NAME);

        writeHeaders(sheet, format.header());
        int nextRow = writeGroupedByYear(sheet, dividends, format);
        autoSizeColumns(sheet);
    }

    private int writeGroupedByYear(Sheet sheet, List<DividendResult> dividends, CellFormat format) {
        Map<Integer, List<DividendResult>> byYear = groupByYear(dividends);
        int rowNum = 1;

        for (var entry : byYear.entrySet()) {
            int year = entry.getKey();
            List<DividendResult> yearDividends = entry.getValue();

            int firstDataRow = rowNum;
            for (DividendResult div : yearDividends) {
                writeDividend(sheet.createRow(rowNum++), div, format);
            }
            int lastDataRow = rowNum;

            writeYearSubtotal(sheet.createRow(rowNum++), year, firstDataRow, lastDataRow, format);
        }

        return rowNum;
    }

    private void writeYearSubtotal(Row row, int year, int firstDataRow, int lastDataRow, CellFormat format) {
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue("Разом за " + year);
        labelCell.setCellStyle(format.header());

        for (int col : SUM_COLS) {
            String colLetter = String.valueOf((char) ('A' + col));
            Cell cell = row.createCell(col);
            cell.setCellFormula("SUM(%s%d:%s%d)".formatted(
                    colLetter, firstDataRow + 1, colLetter, lastDataRow));
            cell.setCellStyle(format.number());
        }
    }

    private Map<Integer, List<DividendResult>> groupByYear(List<DividendResult> dividends) {
        Map<Integer, List<DividendResult>> result = new TreeMap<>();
        for (DividendResult div : dividends) {
            result.computeIfAbsent(div.paymentDate().getYear(), k -> new ArrayList<>()).add(div);
        }
        return result;
    }

    private void writeHeaders(Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeDividend(Row row, DividendResult div, CellFormat format) {
        row.createCell(0).setCellValue(div.ticker());
        setDateCell(row, 1, div.paymentDate(), format.date());
        row.createCell(2).setCellValue(div.currency());
        setNumberCell(row, 3, div.grossAmount(), format.number());
        setNumberCell(row, 4, div.foreignTax(), format.number());
        setNumberCell(row, 5, div.netAmount(), format.number());
        setNumberCell(row, 6, div.grossAmountUah(), format.number());
        setNumberCell(row, 7, div.foreignTaxUah(), format.number());
        setNumberCell(row, 8, div.netAmountUah(), format.number());
    }

    private void setDateCell(Row row, int col, LocalDate value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setNumberCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
