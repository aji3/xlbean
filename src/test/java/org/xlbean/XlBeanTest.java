package org.xlbean;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.Test;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderTest;
import org.xlbean.testbean.TestConverterBean;

public class XlBeanTest {

	private static final DateFormat DT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Test
	public void listOf() throws Exception {
		InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_ValueConverter.xlsx");
		
		XlBeanReader reader = new XlBeanReader();
		XlBean bean = reader.read(in);
		
		System.out.println(bean);
		
		List<TestConverterBean> list = bean.listOf("testConverter", TestConverterBean.class);
		
		System.out.println(list);

		assertThat(list.get(0).get_BigDecimal(), is(new BigDecimal("1.0")));
		assertThat(list.get(1).get_BigDecimal(), is(new BigDecimal("12.34567")));
		assertThat(list.get(2).get_BigDecimal(), is(new BigDecimal("-54321.12345")));

		assertThat(list.get(0).get_BigInteger(), is(new BigInteger("12345")));
		assertThat(list.get(1).get_BigInteger(), is(new BigInteger("12345")));

		assertThat(list.get(0).get_Boolean(), is(true));
		assertThat(list.get(1).get_Boolean(), is(false));
		assertThat(list.get(2).get_Boolean(), is(true));
		assertThat(list.get(3).get_Boolean(), is(false));

		assertThat(list.get(0).get_Character(), is('A'));
		assertThat(list.get(1).get_Character(), is('9'));

		assertThat(list.get(0).get_Date(), is(DT.parse("2017-08-21T00:00:00.000")));
		assertThat(list.get(1).get_Date(), is(DT.parse("2017-08-21T11:35:00.000")));

		assertThat(list.get(0).get_Double(), is(Double.valueOf("1")));
		assertThat(list.get(1).get_Double(), is(Double.valueOf("12.34567")));
		assertThat(list.get(2).get_Double(), is(Double.valueOf("-54321.12345")));

		assertThat(list.get(0).get_Integer(), is(Integer.valueOf("12345")));
		assertThat(list.get(1).get_Integer(), is(Integer.valueOf("12345")));

		assertThat(list.get(0).get_LocalDate(), is(LocalDate.of(2017, 8, 21)));
		assertThat(list.get(1).get_LocalDate(), is(LocalDate.of(2017, 8, 21)));

		assertThat(list.get(0).get_LocalDateTime(), is(LocalDateTime.of(2017, 8, 21, 0, 0, 0, 0)));
		assertThat(list.get(1).get_LocalDateTime(), is(LocalDateTime.of(2017, 8, 21, 11, 35, 0, 0)));

		assertThat(list.get(0).get_LocalTime(), is(LocalTime.of(6, 12, 34, 123000000)));

		assertThat(list.get(0).get_Long(), is(Long.valueOf("12345")));
		assertThat(list.get(1).get_Long(), is(Long.valueOf("12345")));

		assertThat(list.get(0).get_Short(), is(Short.valueOf("123")));

		assertThat(list.get(0).get_String(), is("Hello String!"));

		assertThat(list.get(0).is_boolean(), is(true));
		assertThat(list.get(1).is_boolean(), is(false));
		assertThat(list.get(2).is_boolean(), is(true));
		assertThat(list.get(3).is_boolean(), is(false));

		assertThat(list.get(0).get_char(), is('A'));
		assertThat(list.get(1).get_char(), is('9'));

		assertThat(list.get(0).get_double(), is(Double.valueOf("1")));
		assertThat(list.get(1).get_double(), is(Double.valueOf("12.34567")));
		assertThat(list.get(2).get_double(), is(Double.valueOf("-54321.12345")));

		assertThat(list.get(0).get_int(), is(Integer.valueOf("12345")));
		assertThat(list.get(1).get_int(), is(Integer.valueOf("12345")));

		assertThat(list.get(0).get_long(), is(Long.valueOf("12345")));
		assertThat(list.get(1).get_long(), is(Long.valueOf("12345")));

		assertThat(list.get(0).get_short(), is(Short.valueOf("123")));
	}
}
