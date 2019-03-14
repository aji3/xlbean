package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class IntValueConverter implements ValueConverter<Integer> {

    @Override
    public String getName() {
        return "int";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Integer toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.replaceAll("\\..*$", ""));
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
