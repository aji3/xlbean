package org.xlbean.converter.impl;

import java.math.BigInteger;

import org.xlbean.converter.ValueConverter;

public class BigIntegerValueConverter implements ValueConverter<BigInteger> {

    @Override
    public String getName() {
        return "biginteger";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(BigInteger.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    @Override
    public BigInteger toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return new BigInteger(value.replaceAll("\\..*$", ""));
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }

}
