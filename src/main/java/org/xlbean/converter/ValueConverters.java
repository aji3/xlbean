package org.xlbean.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueConverters {

    private static final Map<Class<?>, ValueConverter<?>> CONVERTER_CACHE = new HashMap<>();

    public static boolean canConvert(Class<?> clazz) {
        return getValueConverter(clazz) != null;
    }

    public static ValueConverter<?> getValueConverter(Class<?> clazz) {
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

    public static ValueConverter<?> getValueConverterByName(String name) {
        return getValueConverters().stream().filter(converter -> converter.getName().equals(name)).findFirst().orElse(
            null);
    }

    public static List<ValueConverter<?>> getValueConverters() {
        return ValueConverterFactory.getInstance().getValueConverters();
    }

    public static String toString(Object obj) {
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

    public static Object toObject(Object value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        if (valueClass.isAssignableFrom(value.getClass())) {
            return value;
        } else {
            String str = null;
            if (value instanceof String) {
                str = (String) value;
            } else {
                if (canConvert(value.getClass())) {
                    str = toString(value);
                } else {
                    str = value.toString();
                }
            }
            return toObject(str, valueClass);
        }
    }

    public static Object toObject(String value, Class<?> valueClass) {
        if (value == null || valueClass == null) {
            return null;
        }
        ValueConverter<?> converter = getValueConverter(valueClass);
        return converter.toObject(value, valueClass);
    }
}
