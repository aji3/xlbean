package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class DoubleValueConverter implements ValueConverter<Double> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Double toObject(String value, Class<?> valueClass) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }
}
