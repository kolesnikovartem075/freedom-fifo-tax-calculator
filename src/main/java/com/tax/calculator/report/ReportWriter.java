package com.tax.calculator.report;


import com.tax.calculator.closed.position.entity.ClosedPosition;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportWriter {

    private static final DetailSheetWriter DETAIL_WRITER = new DetailSheetWriter();
    private static final SummarySheetWriter SHEET_WRITER = new SummarySheetWriter();

    public static void write(List<ClosedPosition> positions, File outputFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(outputFile)) {

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            DETAIL_WRITER.write(workbook, positions, headerStyle, dateStyle);
            SHEET_WRITER.write(workbook, positions, headerStyle);

            workbook.write(out);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper helper = workbook.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        return style;
    }
}