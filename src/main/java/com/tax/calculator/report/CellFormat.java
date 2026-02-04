package com.tax.calculator.report;

import org.apache.poi.ss.usermodel.CellStyle;

public record CellFormat(
        CellStyle header,
        CellStyle date,
        CellStyle number
) {
}