package org.xlbean.converter.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xlbean.converter.ValueConverter;

/**
 * Default implementation of {@link ValueConverter}.
 *
 * <p>
 * List of convertable objects:
 *
 * <ul>
 * <li>BigDecimal.class
 * <li>BigInteger.class
 * <li>Boolean.class
 * <li>Character.class
 * <li>Date.class
 * <li>Double.class
 * <li>Float.class
 * <li>Integer.class
 * <li>LocalDate.class
 * <li>Long.class
 * <li>Short.class
 * <li>String.class
 * <li>boolean.class
 * <li>char.class
 * <li>double.class
 * <li>float.class
 * <li>int.class
 * <li>long.class
 * <li>short.class
 * <li>LocalDateTime.class
 * <li>LocalTime.class
 * <li>Enum.class
 * </ul>
 *
 * @author Kazuya Tanikawa
 */
public class DelegateValueConverter implements ValueConverter<Object> {

    private static final List<ValueConverter<?>> DEFAULT_VALUE_CONVERTERS = Arrays.asList(
        new StringValueConverter(),
        new IntValueConverter(),
        new LongValueConverter(),
        new CharValueConverter(),
        new BooleanValueConverter(),
        new ByteValueConverter(),
        new ShortValueConverter(),
        new FloatValueConverter(),
        new DoubleValueConverter(),
        new BigDecimalValueConverter(),
        new BigIntegerValueConverter(),
        new DateValueConverter(),
        new LocalDateTimeValueConverter(),
        new LocalDateValueConverter(),
        new LocalTimeValueCoverter(),
        new EnumValueConverter());

    private static final Map<Class<?>, ValueConverter<?>> CONVERTER_CACHE = new HashMap<>();

    /*
     * (non-Javadoc)
     *
     * @see org.xlbean.converter.ValueConverter#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(Class<?> clazz) {
        return getValueConverter(clazz) != null;
    }

    protected ValueConverter<?> getValueConverter(Class<?> clazz) {
        ValueConverter<?> ret = CONVERTER_CACHE.get(clazz);
        if (ret == null) {
            ret = getValueConverters()
                .stream()
                .filter(converter -> converter.canConvert(clazz))
                .findFirst()
                .orElse(null);
            CONVERTER_CACHE.put(clazz, ret);
        }
        return ret;
    }

    public List<ValueConverter<?>> getValueConverters() {
        return DEFAULT_VALUE_CONVERTERS;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xlbean.converter.ValueConverter#toString(java.lang.Object)
     */
    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        ValueConverter<?> converter = getValueConverter(obj.getClass());
        if (converter == null) {
            return obj.toString();
        } else {
            return converter.toString(obj);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xlbean.converter.ValueConverter#toObject(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public Object toObject(String value, Class<?> valueClass) {
        if (value == null || valueClass == null) {
            return null;
        }
        ValueConverter<?> converter = getValueConverter(valueClass);
        return converter.toObject(value, valueClass);
    }
}
