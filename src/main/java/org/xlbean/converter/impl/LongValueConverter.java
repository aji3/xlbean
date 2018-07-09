package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class LongValueConverter implements ValueConverter<Long> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Long toObject(String value, Class<?> valueClass) {
        try {
            return Long.parseLong(value.replaceAll("\\..*$", ""));
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
