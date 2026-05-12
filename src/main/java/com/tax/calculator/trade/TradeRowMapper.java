package com.tax.calculator.trade;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradeRowMapper {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TradeRow map(Row row) {
        var ticker = getStringValue(row, 0);
        var operation = TradeOperationMapper.map(getStringValue(row, 3));
        var quantity = getNumericValue(row, 4);
        var price = getBigDecimalValue(row, 5);
        var currency = getStringValue(row, 6);
        var total = getBigDecimalValue(row, 7);
        var commission = getBigDecimalValue(row, 9);
        var commissionCurrency = getStringValue(row, 10);
        var tradeDate = parseDateTime(getStringValue(row, 11));
        var settlementDate = parseDate(getStringValue(row, 12));

        return TradeRow.builder()
                .ticker(ticker)
                .operation(operation)
                .quantity(quantity)
                .price(price)
                .currency(currency)
                .total(total)
                .commission(commission)
                .commissionCurrency(commissionCurrency)
                .tradeDate(tradeDate)
                .settlementDate(settlementDate)
                .build();
    }

    private LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value.trim(), DATE_TIME_FORMAT);
    }

    private LocalDate parseDate(String value) {
        return LocalDate.parse(value.trim(), DATE_FORMAT);
    }

    private String getStringValue(Row row, int col) {
        Cell cell = row.getCell(col);
        return cell.getStringCellValue().trim();
    }

    private int getNumericValue(Row row, int col) {
        Cell cell = row.getCell(col);
        return (int) cell.getNumericCellValue();
    }

    private BigDecimal getBigDecimalValue(Row row, int col) {
        Cell cell = row.getCell(col);
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }
}
