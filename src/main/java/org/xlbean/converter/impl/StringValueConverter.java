package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class StringValueConverter implements ValueConverter<String> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        return clazz.isAssignableFrom(String.class);
    }

    @Override
    public String toString(Object obj) {
        return (String) obj;
    }

    @Override
    public String toObject(String value, Class<?> valueClass) {
        return value;
    }

}
