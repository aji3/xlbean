package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class BooleanValueConverter implements ValueConverter<Boolean> {

    @Override
    public String getName() {
        return "boolean";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Boolean toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

}
