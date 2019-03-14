package org.xlbean.converter;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.junit.Test;

public class ValueConvertersTest {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Test
    public void toStringTest() throws ParseException {
        assertThat(ValueConverters.toString(null), is(nullValue()));

        assertThat(
            ValueConverters.toString(DATE_FORMAT.parse("2017-09-09T01:23:45.678")),
            is("2017-09-09T01:23:45.678"));
        assertThat(ValueConverters.toString(LocalDate.of(2017, 9, 10)), is("2017-09-10T00:00:00.000"));
        assertThat(
            ValueConverters.toString(LocalDateTime.of(2017, 9, 10, 1, 23, 45, 678000000)),
            is("2017-09-10T01:23:45.678"));
        assertThat(ValueConverters.toString(LocalTime.of(1, 23, 45, 678000000)), is("01:23:45.678"));
    }

    @Test
    public void toObject() throws ParseException {
        assertThat(ValueConverters.toObject(null, null), is(nullValue()));
        assertThat(ValueConverters.toObject("test", null), is(nullValue()));

        assertThat(ValueConverters.toObject("123.45", Integer.class), is(123));
        assertThat(ValueConverters.toObject("-123.45", Integer.class), is(-123));
        assertThat(ValueConverters.toObject("test", Integer.class), is(nullValue()));
        assertThat(ValueConverters.toObject("123.45", Long.class), is(123l));
        assertThat(ValueConverters.toObject("-123.45", Long.class), is(-123l));
        assertThat(ValueConverters.toObject("test", Long.class), is(nullValue()));
        assertThat(ValueConverters.toObject("1", Character.class), is('1'));
        assertThat(ValueConverters.toObject("", Character.class), is(nullValue()));
        assertThat(ValueConverters.toObject("true", Boolean.class), is(true));
        assertThat(ValueConverters.toObject("test", Boolean.class), is(false));
        assertThat(ValueConverters.toObject("123.45", Byte.class), is((byte) 123));
        assertThat(ValueConverters.toObject("-123.45", Byte.class), is((byte) -123));
        assertThat(ValueConverters.toObject("123.45", Short.class), is((short) 123));
        assertThat(ValueConverters.toObject("-123.45", Short.class), is((short) -123));
        assertThat(ValueConverters.toObject("test", Short.class), is(nullValue()));
        assertThat(ValueConverters.toObject("123.45", Float.class), is(123.45f));
        assertThat(ValueConverters.toObject("-123.45", Float.class), is(-123.45f));
        assertThat(ValueConverters.toObject("test", Float.class), is(nullValue()));
        assertThat(ValueConverters.toObject("123.45", Double.class), is(123.45));
        assertThat(ValueConverters.toObject("-123.45", Double.class), is(-123.45));
        assertThat(ValueConverters.toObject("test", Double.class), is(nullValue()));
        assertThat(ValueConverters.toObject("123.45", BigDecimal.class), is(BigDecimal.valueOf(123.45)));
        assertThat(ValueConverters.toObject("-123.45", BigDecimal.class), is(BigDecimal.valueOf(-123.45)));
        assertThat(ValueConverters.toObject("test", BigDecimal.class), is(nullValue()));
        assertThat(ValueConverters.toObject("123.45", BigInteger.class), is(BigInteger.valueOf(123)));
        assertThat(ValueConverters.toObject("-123.45", BigInteger.class), is(BigInteger.valueOf(-123)));
        assertThat(ValueConverters.toObject("test", BigInteger.class), is(nullValue()));
        assertThat(
            ValueConverters.toObject("2017-09-10T01:23:45.678", Date.class),
            is(DATE_FORMAT.parse("2017-09-10T01:23:45.678")));
        assertThat(ValueConverters.toObject("test", LocalDate.class), is(nullValue()));
        assertThat(
            ValueConverters.toObject("2017-09-10T01:23:45.678", LocalDate.class),
            is(LocalDate.of(2017, 9, 10)));
        assertThat(ValueConverters.toObject("test", LocalDateTime.class), is(nullValue()));
        assertThat(
            ValueConverters.toObject("2017-09-10T01:23:45.678", LocalDateTime.class),
            is(LocalDateTime.of(2017, 9, 10, 1, 23, 45, 678000000)));
        assertThat(ValueConverters.toObject("test", LocalDateTime.class), is(nullValue()));
        assertThat(
            ValueConverters.toObject("01:23:45.678", LocalTime.class),
            is(LocalTime.of(1, 23, 45, 678000000)));
        assertThat(ValueConverters.toObject("test", LocalTime.class), is(nullValue()));
    }
}
