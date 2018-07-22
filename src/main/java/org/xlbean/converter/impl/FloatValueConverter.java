package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class FloatValueConverter implements ValueConverter<Float> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Float toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
