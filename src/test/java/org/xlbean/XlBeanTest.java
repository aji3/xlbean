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
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderTest;
import org.xlbean.testbean.Country;
import org.xlbean.testbean.President;
import org.xlbean.testbean.Stats;
import org.xlbean.testbean.TestConverterBean;

public class XlBeanTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final DateFormat DT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Test
    public void beanOf() throws Exception {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        Country c = bean.of(Country.class);
        System.out.println(c);

        assertThat(c.getName(), is("United States of America"));
        assertThat(c.getStats().getGdp(), is(BigDecimal.valueOf(18558000000000000l)));
        assertThat(c.getStats().getTotalArea(), is(9833520l));

        Stats stats = bean.beanOf("stats", Stats.class);
        assertThat(stats.getGdp(), is(BigDecimal.valueOf(18558000000000000l)));
        assertThat(stats.getTotalArea(), is(9833520l));
    }

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

    @Test
    public void put() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(
            "Value set to XlBean must be XlBean, XlList or String. To set value of any other class to this bean, please use #set(String, Object). #set(String, Object) will scan all the properties in the object and set to this object. (Actual: class java.lang.Integer)");

        XlBean bean = new XlBean();
        bean.put("illegalValue", Integer.valueOf(1));
    }

    @Test
    public void set() {
        Stats stats = new Stats();
        stats.setGdp(BigDecimal.valueOf(123.45));
        stats.setTotalArea(Long.valueOf(1234567890));
        List<President> presidents = new ArrayList<>();
        President p = new President();
        p.setName("1st president");
        presidents.add(p);
        p = new President();
        p.setName("2nd president");
        presidents.add(p);
        p = new President();
        p.setName("3rd president");
        presidents.add(p);
        Country country = new Country();
        country.setName("testName");
        country.setStats(stats);
        country.setPresidents(presidents);

        XlBean bean = new XlBean();
        bean.set(country);

        assertThat(bean.get("name"), is("testName"));
        assertThat(bean.bean("stats").get("gdp"), is("123.45"));
        assertThat(bean.bean("stats").get("totalArea"), is("1234567890"));
        assertThat(bean.list("presidents").get(0).get("name"), is("1st president"));
        assertThat(bean.list("presidents").get(1).get("name"), is("2nd president"));
        assertThat(bean.list("presidents").get(2).get("name"), is("3rd president"));
    }

    @Test
    public void isValuesEmpty() {
        XlBean bean = new XlBean();

        assertThat(bean.isValuesEmpty(), is(true));

        bean.put("testNull", null);

        assertThat(bean.isValuesEmpty(), is(true));

        XlList list = new XlList();
        XlBean b = new XlBean();
        b.put("testNullForList", null);
        list.add(b);
        bean.put("list", list);

        assertThat(bean.isValuesEmpty(), is(true));

        b = new XlBean();
        b.put("testNonNullForList", "TESTVALUE");
        list.add(b);

        assertThat(bean.isValuesEmpty(), is(false));
    }
}
