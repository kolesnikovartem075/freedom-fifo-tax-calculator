package com.tax.calculator.report;

import com.tax.calculator.position.entity.ClosedPosition;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

public class SummarySheetWriter {

    private static final String[] HEADERS = {
            "Тікер",
            "Кількість",
            "Прибуток (USD)",
            "Дохід (UAH)",
            "Витрати (UAH)",
            "Прибуток (UAH)"
    };

    // Detail sheet columns: B=Кількість, I=Прибуток USD, J=Дохід UAH, K=Витрати UAH, L=Прибуток UAH
    private static final String[] DETAIL_COLUMNS = {"B", "I", "J", "K", "L"};

    public void write(Workbook workbook, List<ClosedPosition> positions, CellFormat cellFormat) {
        Map<Integer, List<String>> yearTickers = collectYearTickers(positions);
        int dataLastRow = positions.size() + 1;

        for (Map.Entry<Integer, List<String>> entry : yearTickers.entrySet()) {
            writeYearSheet(workbook, entry.getKey(), entry.getValue(), dataLastRow, cellFormat);
        }
    }

    private void writeYearSheet(Workbook workbook, int year, List<String> tickers,
                                int dataLastRow, CellFormat format) {
        Sheet sheet = workbook.createSheet(String.valueOf(year));

        writeHeaders(sheet, format.header());

        for (int i = 0; i < tickers.size(); i++) {
            writeTickerRow(sheet.createRow(i + 1), tickers.get(i), year, dataLastRow, format.number());
        }

        writeTotalRow(sheet, tickers.size() + 1, tickers.size(), format);
        autoSizeColumns(sheet);
    }

    private void writeTickerRow(Row row, String ticker, int year, int dataLastRow, CellStyle numberStyle) {
        row.createCell(0).setCellValue(ticker);

        for (int i = 0; i < DETAIL_COLUMNS.length; i++) {
            Cell cell = row.createCell(i + 1);
            cell.setCellFormula(buildSumProductFormula(ticker, year, dataLastRow, DETAIL_COLUMNS[i]));

            if (!DETAIL_COLUMNS[i].equals("B")) {
                cell.setCellStyle(numberStyle);
            }
        }
    }

    private void writeTotalRow(Sheet sheet, int rowNum, int tickerCount, CellFormat format) {
        Row row = sheet.createRow(rowNum);

        Cell labelCell = row.createCell(0);
        labelCell.setCellValue("Total");
        labelCell.setCellStyle(format.header());

        for (int col = 1; col < HEADERS.length; col++) {
            String colLetter = String.valueOf((char) ('A' + col));
            Cell cell = row.createCell(col);
            cell.setCellFormula(String.format("SUM(%s2:%s%d)", colLetter, colLetter, tickerCount + 1));

            if (col > 1) {
                cell.setCellStyle(format.number());
            }
        }
    }

    private String buildSumProductFormula(String ticker, int year, int dataLastRow, String column) {
        return String.format(
                "SUMPRODUCT(('Tax Report'!A2:A%d=\"%s\")*(YEAR('Tax Report'!D2:D%d)=%d)*'Tax Report'!%s2:%s%d)",
                dataLastRow, ticker, dataLastRow, year, column, column, dataLastRow
        );
    }

    private Map<Integer, List<String>> collectYearTickers(List<ClosedPosition> positions) {
        Map<Integer, LinkedHashSet<String>> yearTickerSets = new TreeMap<>();

        for (ClosedPosition p : positions) {
            int year = p.sell().tradeDate().getYear();
            yearTickerSets.computeIfAbsent(year, k -> new LinkedHashSet<>()).add(p.ticker());
        }

        Map<Integer, List<String>> result = new TreeMap<>();
        yearTickerSets.forEach((year, tickers) -> result.put(year, new ArrayList<>(tickers)));
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

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}