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
import org.xlbean.converter.impl.DelegateValueConverter;

public class DelegateValueConverterTest {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Test
    public void toStringTest() throws ParseException {
        DelegateValueConverter converter = new DelegateValueConverter();
        assertThat(converter.toString(null), is(nullValue()));

        assertThat(
            converter.toString(DATE_FORMAT.parse("2017-09-09T01:23:45.678")),
            is("2017-09-09T01:23:45.678"));
        assertThat(converter.toString(LocalDate.of(2017, 9, 10)), is("2017-09-10T00:00:00.000"));
        assertThat(
            converter.toString(LocalDateTime.of(2017, 9, 10, 1, 23, 45, 678000000)),
            is("2017-09-10T01:23:45.678"));
        assertThat(converter.toString(LocalTime.of(1, 23, 45, 678000000)), is("01:23:45.678"));
    }

    @Test
    public void toObject() throws ParseException {
        DelegateValueConverter converter = new DelegateValueConverter();

        assertThat(converter.toObject(null, null), is(nullValue()));
        assertThat(converter.toObject("test", null), is(nullValue()));

        assertThat(converter.toObject("123.45", Integer.class), is(123));
        assertThat(converter.toObject("-123.45", Integer.class), is(-123));
        assertThat(converter.toObject("test", Integer.class), is(nullValue()));
        assertThat(converter.toObject("123.45", Long.class), is(123l));
        assertThat(converter.toObject("-123.45", Long.class), is(-123l));
        assertThat(converter.toObject("test", Long.class), is(nullValue()));
        assertThat(converter.toObject("1", Character.class), is('1'));
        assertThat(converter.toObject("", Character.class), is(nullValue()));
        assertThat(converter.toObject("true", Boolean.class), is(true));
        assertThat(converter.toObject("test", Boolean.class), is(false));
        assertThat(converter.toObject("123.45", Byte.class), is((byte) 123));
        assertThat(converter.toObject("-123.45", Byte.class), is((byte) -123));
        assertThat(converter.toObject("123.45", Short.class), is((short) 123));
        assertThat(converter.toObject("-123.45", Short.class), is((short) -123));
        assertThat(converter.toObject("test", Short.class), is(nullValue()));
        assertThat(converter.toObject("123.45", Float.class), is(123.45f));
        assertThat(converter.toObject("-123.45", Float.class), is(-123.45f));
        assertThat(converter.toObject("test", Float.class), is(nullValue()));
        assertThat(converter.toObject("123.45", Double.class), is(123.45));
        assertThat(converter.toObject("-123.45", Double.class), is(-123.45));
        assertThat(converter.toObject("test", Double.class), is(nullValue()));
        assertThat(converter.toObject("123.45", BigDecimal.class), is(BigDecimal.valueOf(123.45)));
        assertThat(converter.toObject("-123.45", BigDecimal.class), is(BigDecimal.valueOf(-123.45)));
        assertThat(converter.toObject("test", BigDecimal.class), is(nullValue()));
        assertThat(converter.toObject("123.45", BigInteger.class), is(BigInteger.valueOf(123)));
        assertThat(converter.toObject("-123.45", BigInteger.class), is(BigInteger.valueOf(-123)));
        assertThat(converter.toObject("test", BigInteger.class), is(nullValue()));
        assertThat(
            converter.toObject("2017-09-10T01:23:45.678", Date.class),
            is(DATE_FORMAT.parse("2017-09-10T01:23:45.678")));
        assertThat(converter.toObject("test", LocalDate.class), is(nullValue()));
        assertThat(
            converter.toObject("2017-09-10T01:23:45.678", LocalDate.class),
            is(LocalDate.of(2017, 9, 10)));
        assertThat(converter.toObject("test", LocalDateTime.class), is(nullValue()));
        assertThat(
            converter.toObject("2017-09-10T01:23:45.678", LocalDateTime.class),
            is(LocalDateTime.of(2017, 9, 10, 1, 23, 45, 678000000)));
        assertThat(converter.toObject("test", LocalDateTime.class), is(nullValue()));
        assertThat(
            converter.toObject("01:23:45.678", LocalTime.class),
            is(LocalTime.of(1, 23, 45, 678000000)));
        assertThat(converter.toObject("test", LocalTime.class), is(nullValue()));
    }
}
