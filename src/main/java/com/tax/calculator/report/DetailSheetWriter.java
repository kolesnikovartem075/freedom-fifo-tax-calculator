package com.tax.calculator.report;

import com.tax.calculator.closed.position.entity.ClosedPosition;
import org.apache.poi.ss.usermodel.*;

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

    public void write(Workbook workbook, List<ClosedPosition> positions,
                      CellStyle headerStyle, CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet(SHEET_NAME);

        writeHeaders(sheet, headerStyle);
        writePositions(sheet, positions, dateStyle);
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

    private void writePositions(Sheet sheet, List<ClosedPosition> positions, CellStyle dateStyle) {
        for (int i = 0; i < positions.size(); i++) {
            writePosition(sheet.createRow(i + 1), positions.get(i), dateStyle);
        }
    }

    private void writePosition(Row row, ClosedPosition position, CellStyle dateStyle) {
        row.createCell(0).setCellValue(position.ticker());
        row.createCell(1).setCellValue(position.quantity());

        Cell buyDateCell = row.createCell(2);
        buyDateCell.setCellValue(position.buy().tradeDate());
        buyDateCell.setCellStyle(dateStyle);

        Cell sellDateCell = row.createCell(3);
        sellDateCell.setCellValue(position.sell().tradeDate());
        sellDateCell.setCellStyle(dateStyle);

        row.createCell(4).setCellValue(position.buy().pricePerUnit().doubleValue());
        row.createCell(5).setCellValue(position.buy().commissionPerUnit().doubleValue());
        row.createCell(6).setCellValue(position.sell().pricePerUnit().doubleValue());
        row.createCell(7).setCellValue(position.sell().commissionPerUnit().doubleValue());
        row.createCell(8).setCellValue(position.profitUsd().doubleValue());
        row.createCell(9).setCellValue(position.profitUah().doubleValue());
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
