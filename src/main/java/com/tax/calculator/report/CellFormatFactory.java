package com.tax.calculator.report;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public class CellFormatFactory {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String NUMERIC_FORMAT = "#,##0.00";

    public static CellFormat createCellFormat(Workbook workbook) {
        return new CellFormat(
                createHeaderStyle(workbook),
                createStyle(workbook, DATE_TIME_FORMAT),
                createStyle(workbook, NUMERIC_FORMAT)
        );
    }

    private static CellStyle createStyle(Workbook workbook, String format) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(format));
        return style;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
