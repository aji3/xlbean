package org.xlbean.excel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.xlbean.excel.XlSheet;

public class XlSheetTest {

	@Test
	public void parseDateTimeValue() {
		XlSheet sheet = new XlSheet(null);
		double actual = sheet.parseDateTimeValue("2017/03/18 00:00:00.000");
		assertThat(actual, is(42812d));
	}
}
