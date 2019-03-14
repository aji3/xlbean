package org.xlbean.converter.impl;

import java.math.BigDecimal;

import org.xlbean.converter.ValueConverter;

public class BigDecimalValueConverter implements ValueConverter<BigDecimal> {

    @Override
    public String getName() {
        return "bigdecimal";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(BigDecimal.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return ((BigDecimal) obj).toPlainString();
    }

    @Override
    public BigDecimal toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
