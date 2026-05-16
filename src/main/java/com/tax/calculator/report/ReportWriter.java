package com.tax.calculator.report;


import com.tax.calculator.dividend.DividendResult;
import com.tax.calculator.position.entity.ClosedPosition;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportWriter {

    private static final DetailSheetWriter DETAIL_WRITER = new DetailSheetWriter();
    private static final SummarySheetWriter SUMMARY_WRITER = new SummarySheetWriter();
    private static final DividendSheetWriter DIVIDEND_WRITER = new DividendSheetWriter();

    public static void write(List<ClosedPosition> positions, List<DividendResult> dividends,
                             File outputFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(outputFile)) {
            var cellFormat = CellFormatFactory.createCellFormat(workbook);

            DETAIL_WRITER.write(workbook, positions, cellFormat);
            SUMMARY_WRITER.write(workbook, positions, cellFormat);
            DIVIDEND_WRITER.write(workbook, dividends, cellFormat);

            workbook.write(out);
        }
    }

}
