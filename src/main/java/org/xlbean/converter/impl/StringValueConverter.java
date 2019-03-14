package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class StringValueConverter implements ValueConverter<String> {

    @Override
    public String getName() {
        return "string";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        return clazz.isAssignableFrom(String.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        } else {
            return obj.toString();
        }
    }

    @Override
    public String toObject(String value, Class<?> valueClass) {
        return value;
    }

}
