package org.xlbean;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
import org.xlbean.util.BeanHelper;

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
            "Value set to XlBean must be XlBean, List<XlBean> or String. To set value of any other class to this bean, use #set(String, Object). #set(String, Object) will scan all the properties in the object and set to this object. (Actual: class java.lang.Integer)");

        XlBean bean = new XlBeanImpl();
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

        XlBean bean = new XlBeanImpl();
        bean.set(country);

        assertThat(bean.get("name"), is("testName"));
        assertThat(bean.bean("stats").get("gdp"), is("123.45"));
        assertThat(bean.bean("stats").get("totalArea"), is("1234567890"));
        assertThat(bean.beans("presidents").get(0).get("name"), is("1st president"));
        assertThat(bean.beans("presidents").get(1).get("name"), is("2nd president"));
        assertThat(bean.beans("presidents").get(2).get("name"), is("3rd president"));
    }

    @Test
    public void isValuesEmpty() {
        XlBean bean = new XlBeanImpl();

        assertThat(BeanHelper.isValuesEmpty(bean), is(true));

        bean.put("testNull", null);

        assertThat(BeanHelper.isValuesEmpty(bean), is(true));

        XlList list = new XlList();
        XlBean b = new XlBeanImpl();
        b.put("testNullForList", null);
        list.add(b);
        bean.put("list", list);

        assertThat(BeanHelper.isValuesEmpty(bean), is(true));

        b = new XlBeanImpl();
        b.put("testNonNullForList", "TESTVALUE");
        list.add(b);

        assertThat(BeanHelper.isValuesEmpty(bean), is(false));
    }

    @Test
    public void testInteger() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.integer("int1"), is(1));
        assertThat(bean.integer("int2"), is(0));
        assertThat(bean.integer("int3"), is(-1));
        assertThat(bean.integer("int4"), is(100));
        assertThat(bean.integer("error1"), is(nullValue()));
        assertThat(bean.integer("error2"), is(nullValue()));
        assertThat(bean.integer("last"), is(9999));

    }

    @Test
    public void testIntegers() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Integer> ints = bean.integers("ints");
        assertThat(ints.get(0), is(1));
        assertThat(ints.get(1), is(0));
        assertThat(ints.get(2), is(-1));
        assertThat(ints.get(3), is(100));
        assertThat(ints.get(4), is(nullValue()));
        assertThat(ints.get(5), is(nullValue()));
        assertThat(ints.get(6), is(9999));

    }

    @Test
    public void testLng() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.lng("long1"), is(1l));
        assertThat(bean.lng("long2"), is(0l));
        assertThat(bean.lng("long3"), is(-1l));
        assertThat(bean.lng("long4"), is(100l));
        assertThat(bean.lng("long5"), is(9223372036854775807l));
        assertThat(bean.lng("error1"), is(nullValue()));
        assertThat(bean.lng("error2"), is(nullValue()));
        assertThat(bean.lng("last"), is(9999l));

    }

    @Test
    public void testLongs() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Long> longs = bean.longs("longs");
        assertThat(longs.size(), is(8));
        assertThat(longs.get(0), is(1l));
        assertThat(longs.get(1), is(0l));
        assertThat(longs.get(2), is(-1l));
        assertThat(longs.get(3), is(100l));
        assertThat(longs.get(4), is(9223372036854775807l));
        assertThat(longs.get(5), is(nullValue()));
        assertThat(longs.get(6), is(nullValue()));
        assertThat(longs.get(7), is(9999l));

    }

    @Test
    public void testShort() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.shrt("short1"), is(Short.valueOf("1")));
        assertThat(bean.shrt("short2"), is(Short.valueOf("0")));
        assertThat(bean.shrt("short3"), is(Short.valueOf("-1")));
        assertThat(bean.shrt("short4"), is(Short.valueOf("100")));
        assertThat(bean.shrt("short5"), is(nullValue()));
        assertThat(bean.shrt("error1"), is(nullValue()));
        assertThat(bean.shrt("error2"), is(nullValue()));
        assertThat(bean.shrt("last"), is(Short.valueOf("9999")));

    }

    @Test
    public void testShorts() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Short> shorts = bean.shorts("shorts");
        assertThat(shorts.size(), is(8));
        assertThat(shorts.get(0), is(Short.valueOf("1")));
        assertThat(shorts.get(1), is(Short.valueOf("0")));
        assertThat(shorts.get(2), is(Short.valueOf("-1")));
        assertThat(shorts.get(3), is(Short.valueOf("100")));
        assertThat(shorts.get(4), is(nullValue()));
        assertThat(shorts.get(5), is(nullValue()));
        assertThat(shorts.get(6), is(nullValue()));
        assertThat(shorts.get(7), is(Short.valueOf("9999")));

    }

    @Test
    public void testFloat() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.flt("float1"), is(1f));
        assertThat(bean.flt("float2"), is(0f));
        assertThat(bean.flt("float3"), is(-1f));
        assertThat(bean.flt("float4"), is(100.1f));
        assertThat(bean.flt("float5"), is(9223372000000000000f));
        assertThat(bean.flt("error1"), is(nullValue()));
        assertThat(bean.flt("error2"), is(nullValue()));
        assertThat(bean.flt("last"), is(9999f));

    }

    @Test
    public void testFloats() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Float> floats = bean.floats("floats");
        assertThat(floats.size(), is(8));
        assertThat(floats.get(0), is(1f));
        assertThat(floats.get(1), is(0f));
        assertThat(floats.get(2), is(-1f));
        assertThat(floats.get(3), is(100.1f));
        assertThat(floats.get(4), is(9223372000000000000f));
        assertThat(floats.get(5), is(nullValue()));
        assertThat(floats.get(6), is(nullValue()));
        assertThat(floats.get(7), is(9999f));

    }

    @Test
    public void testDouble() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.dbl("double1"), is(1d));
        assertThat(bean.dbl("double2"), is(0d));
        assertThat(bean.dbl("double3"), is(-1d));
        assertThat(bean.dbl("double4"), is(100.1d));
        assertThat(bean.dbl("double5"), is(9223372036854775807d));
        assertThat(bean.dbl("error1"), is(nullValue()));
        assertThat(bean.dbl("error2"), is(nullValue()));
        assertThat(bean.dbl("last"), is(9999d));

    }

    @Test
    public void testDoubles() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Double> doubles = bean.doubles("doubles");
        assertThat(doubles.size(), is(8));
        assertThat(doubles.get(0), is(1d));
        assertThat(doubles.get(1), is(0d));
        assertThat(doubles.get(2), is(-1d));
        assertThat(doubles.get(3), is(100.1d));
        assertThat(doubles.get(4), is(9223372036854775807d));
        assertThat(doubles.get(5), is(nullValue()));
        assertThat(doubles.get(6), is(nullValue()));
        assertThat(doubles.get(7), is(9999d));

    }

    @Test
    public void testBool() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.bool("bool1"), is(true));
        assertThat(bean.bool("bool2"), is(false));
        assertThat(bean.bool("bool3"), is(true));
        assertThat(bean.bool("bool4"), is(false));
        assertThat(bean.bool("bool_error1"), is(false));
        assertThat(bean.bool("bool_error2"), is(false));
        assertThat(bean.bool("bool5"), is(false));

    }

    @Test
    public void testBools() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Boolean> bools = bean.bools("bools");
        assertThat(bools.size(), is(7));
        assertThat(bools.get(0), is(true));
        assertThat(bools.get(1), is(false));
        assertThat(bools.get(2), is(true));
        assertThat(bools.get(3), is(false));
        assertThat(bools.get(4), is(false));
        assertThat(bools.get(5), is(false));
        assertThat(bools.get(6), is(false));

    }

    @Test
    public void testCharacter() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.character("character1"), is('a'));
        assertThat(bean.character("character2"), is('1'));
        assertThat(bean.character("character3"), is('b'));
        assertThat(bean.character("character4"), is('t'));
        assertThat(bean.character("character5"), is(nullValue()));
        assertThat(bean.character("character6"), is('-'));

    }

    @Test
    public void testCharacters() {
        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<Character> characters = bean.characters("characters");
        assertThat(characters.size(), is(6));
        assertThat(characters.get(0), is('a'));
        assertThat(characters.get(1), is('1'));
        assertThat(characters.get(2), is('b'));
        assertThat(characters.get(3), is('t'));
        assertThat(characters.get(4), is(nullValue()));
        assertThat(characters.get(5), is('-'));

    }

}
