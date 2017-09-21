package org.xlbean.excel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.xlbean.reader.XlBeanReaderTest;
import org.xlbean.util.FileUtil;

public class XlSheetTest {

	@Test
	public void parseDateTimeValue() {
		XlSheet sheet = new XlSheet(null);
		double actual = sheet.parseDateTimeValue("2017-03-18T00:00:00.000");
		assertThat(actual, is(42812d));
	}
	
	@Test
	public void getCellValue() throws Exception{
	    InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");
	    try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in))) {
	        XlWorkbook book = XlWorkbook.wrap(wb);
	        XlSheet sheet = book.getSheet("presidents");
	        
	        XlCellAddress address = new XlCellAddress.Builder().row(2).column(3).build();
	        assertThat(sheet.getCellValue(address), is("United States of America"));
	    }
	}
}
