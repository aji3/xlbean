package org.xlbean.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.xlbean.XlBean;

/**
 * Converter used for converting String values in {@link XlBean} to object or
 * vice-versa.
 * 
 * <p>
 * This class defines all types which can be used as a leaf value and it can be
 * checked by a method {@link #canConvert(Class)}. Any class which is not
 * convertable is treated as custom object and dug down into the object to
 * convert all the elements inside the object.
 * </p>
 * 
 * <p>
 * List of convertable objects:
 * <ul>
 * <li>BigDecimal.class</li>
 * <li>BigInteger.class</li>
 * <li>Boolean.class</li>
 * <li>Character.class</li>
 * <li>Date.class</li>
 * <li>Double.class</li>
 * <li>Integer.class</li>
 * <li>LocalDate.class</li>
 * <li>Long.class</li>
 * <li>Short.class</li>
 * <li>String.class</li>
 * <li>boolean.class</li>
 * <li>char.class</li>
 * <li>double.class</li>
 * <li>int.class</li>
 * <li>long.class</li>
 * <li>short.class</li>
 * <li>LocalDateTime.class</li>
 * </ul>
 * </p>
 * 
 * @author Kazuya Tanikawa
 *
 */
public class ValueConverter {

    private static final List<Class<?>> SUPPORT_CLASSES = Arrays.asList(int.class, long.class, char.class,
            boolean.class, short.class, double.class, Integer.class, Long.class, Character.class, Boolean.class,
            Short.class, Double.class, String.class, BigDecimal.class, BigInteger.class, Date.class, LocalDate.class,
            LocalDateTime.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter LOCALDATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public boolean canConvert(Class<?> clazz) {
        return SUPPORT_CLASSES.contains(clazz);
    }

    /**
     * Convert given {@code obj} to the {@link String} value to be hold in
     * {@link XlBean}.
     * 
     * @param obj
     * @return
     */
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return DATE_FORMAT.format((Date) obj);
        } else if (obj instanceof LocalDate) {
            return LOCALDATE_FORMAT.format((LocalDate) obj) + " 00:00:00.000";
        } else if (obj instanceof LocalDateTime) {
            return DATETIME_FORMAT.format((TemporalAccessor) obj);
        }
        return obj.toString();
    }

    /**
     * Convert given {@code value} to an instance of {@code valueClass}.
     * 
     * <p>
     * This method will not throw any exception even if the {@code value} is not
     * able to be converted to the specified class. If the {@code value} is
     * null, then always returns null.
     * </p>
     * 
     * @param value
     * @param valueClass
     * @return
     */
    public Object toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        if (valueClass.isAssignableFrom(String.class)) {
            return value;
        } else if (valueClass.isAssignableFrom(Integer.class) || valueClass.isAssignableFrom(int.class)) {
            try {
                return Integer.parseInt(value.replaceAll("\\..*$", ""));
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Long.class) || valueClass.isAssignableFrom(long.class)) {
            try {
                return Long.parseLong(value.replaceAll("\\..*$", ""));
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Character.class) || valueClass.isAssignableFrom(char.class)) {
            try {
                return value.charAt(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Boolean.class) || valueClass.isAssignableFrom(boolean.class)) {
            try {
                return Boolean.parseBoolean(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Short.class) || valueClass.isAssignableFrom(short.class)) {
            try {
                return Short.parseShort(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Float.class) || valueClass.isAssignableFrom(float.class)) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Double.class) || valueClass.isAssignableFrom(double.class)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(BigDecimal.class)) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(BigInteger.class)) {
            try {
                return new BigInteger(value.replaceAll("\\..*$", ""));
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(Date.class)) {
            try {
                return DATE_FORMAT.parse(value);
            } catch (ParseException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(LocalDate.class)) {
            try {
                return LocalDate.parse(value, DATETIME_FORMAT);
            } catch (DateTimeParseException e) {
                // ignore
            }
        } else if (valueClass.isAssignableFrom(LocalDateTime.class)) {
            try {
                return LocalDateTime.parse(value, DATETIME_FORMAT);
            } catch (DateTimeParseException e) {
                // ignore
            }
        }
        return null;
    }

}
