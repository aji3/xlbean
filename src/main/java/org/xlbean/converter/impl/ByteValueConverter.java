package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

public class ByteValueConverter implements ValueConverter<Byte> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(byte.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public Byte toObject(String value, Class<?> valueClass) {
        try {
            return Byte.parseByte(value.replaceAll("\\..*$", ""));
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
