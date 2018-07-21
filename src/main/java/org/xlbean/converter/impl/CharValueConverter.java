package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class CharValueConverter implements ValueConverter<Character> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(char.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Character toObject(String value, Class<?> valueClass) {
        try {
            return value.charAt(0);
        } catch (StringIndexOutOfBoundsException e) {
            // ignore
        }
        return null;
    }

}
