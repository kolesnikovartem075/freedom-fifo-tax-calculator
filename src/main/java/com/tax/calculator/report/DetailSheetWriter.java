package com.tax.calculator.report;

import com.tax.calculator.position.entity.ClosedPosition;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DetailSheetWriter {


    static final String SHEET_NAME = "Tax Report";

    private static final String[] HEADERS = {
            "Тікер",
            "Кількість",
            "Дата покупки",
            "Дата продажу",
            "Ціна покупки (USD)",
            "Комісія покупки (USD)",
            "Ціна продажу (USD)",
            "Комісія продажу (USD)",
            "Прибуток (USD)",
            "Прибуток (UAH)"
    };

    public void write(Workbook workbook, List<ClosedPosition> positions, CellFormat format) {
        Sheet sheet = workbook.createSheet(SHEET_NAME);

        writeHeaders(sheet, format.header());
        writePositions(sheet, positions, format);
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

    private void writePositions(Sheet sheet, List<ClosedPosition> positions, CellFormat format) {
        for (int i = 0; i < positions.size(); i++) {
            writePosition(sheet.createRow(i + 1), positions.get(i), format);
        }
    }

    private void writePosition(Row row, ClosedPosition position, CellFormat format) {
        row.createCell(0).setCellValue(position.ticker());
        row.createCell(1).setCellValue(position.quantity());

        setDateCell(row, 2, position.buy().tradeDate(), format.date());
        setDateCell(row, 3, position.sell().tradeDate(), format.date());

        setNumberCell(row, 4, position.buy().pricePerUnit(), format.number());
        setNumberCell(row, 5, position.buy().commissionPerUnit(), format.number());
        setNumberCell(row, 6, position.sell().pricePerUnit(), format.number());
        setNumberCell(row, 7, position.sell().commissionPerUnit(), format.number());
        setNumberCell(row, 8, position.profitUsd(), format.number());
        setNumberCell(row, 9, position.profitUah(), format.number());
    }

    private void setDateCell(Row row, int col, LocalDateTime value, CellStyle style) {
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
