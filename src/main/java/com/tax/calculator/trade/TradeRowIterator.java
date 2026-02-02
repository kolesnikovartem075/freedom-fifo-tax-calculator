package com.tax.calculator.trade;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TradeRowIterator implements Iterator<TradeRow> {


    private final Iterator<Row> rowIterator;
    private final TradeRowMapper tradeRowMapper;
    private Row peeked;

    private TradeRowIterator(Iterator<Row> rowIterator, TradeRowMapper tradeRowMapper) {
        this.rowIterator = rowIterator;
        this.tradeRowMapper = tradeRowMapper;
    }

    public static TradeRowIterator of(Sheet sheet) {
        Iterator<Row> iterator = sheet.iterator();

        // skip header row
        if (iterator.hasNext()) {
            iterator.next();
        }

        var tradeRowIterator = new TradeRowIterator(iterator, new TradeRowMapper());
        tradeRowIterator.peeked = tradeRowIterator.nextNonEmptyRow();
        return tradeRowIterator;
    }

    @Override
    public boolean hasNext() {
        return peeked != null;
    }

    @Override
    public TradeRow next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more trade rows");
        }

        TradeRow result = tradeRowMapper.map(peeked);
        peeked = nextNonEmptyRow();
        return result;
    }

    private Row nextNonEmptyRow() {
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isValidTrade(row)) {
                return row;
            }
        }
        return null;
    }

    private boolean isValidTrade(Row row) {
        Cell firstCell = row.getCell(0);
        return firstCell != null
                && !firstCell.getStringCellValue().isBlank()
                && !firstCell.getStringCellValue().contains("/");
    }
}
