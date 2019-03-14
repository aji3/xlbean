package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class ShortValueConverter implements ValueConverter<Short> {

    @Override
    public String getName() {
        return "short";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Short toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return Short.parseShort(value.replaceAll("\\..*$", ""));
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
