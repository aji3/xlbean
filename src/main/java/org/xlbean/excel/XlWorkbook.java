package org.xlbean.excel;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Wrapper class of {@link Workbook}.
 *
 * @author Kazuya Tanikawa
 */
public class XlWorkbook {

    private Workbook workbook;

    /**
     * Wraps Apache POI native {@code Workbook} with original wrapper class.
     *
     * @param workbook
     *            Apache POI native Workbook
     * @return
     */
    public static XlWorkbook wrap(Workbook workbook) {
        if (workbook == null) {
            return null;
        }
        return new XlWorkbook(workbook);
    }

    public XlWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public int getNumberOfSheets() {
        return workbook.getNumberOfSheets();
    }

    public XlSheet getSheetAt(int index) {
        return wrap(workbook.getSheetAt(index));
    }

    protected XlSheet wrap(Sheet sheet) {
        if (sheet == null) {
            return null;
        }
        return new XlSheet(sheet);
    }

    public XlSheet getSheet(String name) {
        return wrap(workbook.getSheet(name));
    }

    public void write(OutputStream out) throws IOException {
        workbook.write(out);
    }
}
