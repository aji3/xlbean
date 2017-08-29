package org.xlbean.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link ValueConverter}.
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
 * <li>LocalTime.class</li>
 * </ul>
 * </p>
 * 
 * @author Kazuya Tanikawa
 *
 */
public class ValueConverterImpl implements ValueConverter {

    private static final List<Class<?>> SUPPORT_CLASSES = Arrays.asList(int.class, long.class, char.class,
            boolean.class, short.class, double.class, Integer.class, Long.class, Character.class, Boolean.class,
            Short.class, Double.class, String.class, BigDecimal.class, BigInteger.class, Date.class, LocalDate.class,
            LocalDateTime.class, LocalTime.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter LOCALDATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOCALTIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /* (non-Javadoc)
     * @see org.xlbean.converter.ValueConverter#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(Class<?> clazz) {
        return SUPPORT_CLASSES.contains(clazz);
    }

    /* (non-Javadoc)
     * @see org.xlbean.converter.ValueConverter#toString(java.lang.Object)
     */
    @Override
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
        } else if (obj instanceof LocalTime) {
        	return LOCALTIME_FORMAT.format((LocalTime) obj);
        }
        return obj.toString();
    }

    /* (non-Javadoc)
     * @see org.xlbean.converter.ValueConverter#toObject(java.lang.String, java.lang.Class)
     */
    @Override
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
                return Short.parseShort(value.replaceAll("\\..*$", ""));
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
        } else if (valueClass.isAssignableFrom(LocalTime.class)) {
            try {
                return LocalTime.parse(value, LOCALTIME_FORMAT);
            } catch (DateTimeParseException e) {
                // ignore
            }
        }
        return null;
    }

}
