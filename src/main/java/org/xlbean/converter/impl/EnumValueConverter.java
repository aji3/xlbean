package org.xlbean.converter.impl;

import org.xlbean.converter.ValueConverter;

@SuppressWarnings("rawtypes")
public class EnumValueConverter implements ValueConverter<Enum> {

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isEnum();
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @SuppressWarnings({ "unchecked", })
    @Override
    public Enum toObject(String value, Class<?> valueClass) {
        try {
            return Enum.valueOf((Class<? extends Enum>) valueClass, value);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

}
