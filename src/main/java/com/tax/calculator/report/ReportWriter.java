package com.tax.calculator.report;


import com.tax.calculator.position.entity.ClosedPosition;
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
            var cellFormat = CellFormatFactory.createCellFormat(workbook);

            DETAIL_WRITER.write(workbook, positions, cellFormat);
            SHEET_WRITER.write(workbook, positions, cellFormat);

            workbook.write(out);
        }
    }

}