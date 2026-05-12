package com.tax.calculator.report;

import com.tax.calculator.dividend.DividendResult;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    public void write(Workbook workbook, List<DividendResult> dividends, CellFormat format) {
        Sheet sheet = workbook.createSheet(SHEET_NAME);

        writeHeaders(sheet, format.header());
        writeDividends(sheet, dividends, format);
        writeTotalRow(sheet, dividends.size(), format);
        autoSizeColumns(sheet);
    }

    private void writeHeaders(Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeDividends(Sheet sheet, List<DividendResult> dividends, CellFormat format) {
        for (int i = 0; i < dividends.size(); i++) {
            writeDividend(sheet.createRow(i + 1), dividends.get(i), format);
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

    private void writeTotalRow(Sheet sheet, int dataRows, CellFormat format) {
        Row row = sheet.createRow(dataRows + 1);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue("Total");
        labelCell.setCellStyle(format.header());

        int[] sumCols = {3, 4, 5, 6, 7, 8};
        for (int col : sumCols) {
            String colLetter = String.valueOf((char) ('A' + col));
            Cell cell = row.createCell(col);
            cell.setCellFormula("SUM(%s2:%s%d)".formatted(colLetter, colLetter, dataRows + 1));
            cell.setCellStyle(format.number());
        }
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
