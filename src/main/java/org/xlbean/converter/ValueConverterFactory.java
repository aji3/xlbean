package org.xlbean.converter;

import org.xlbean.converter.impl.DelegateValueConverter;

public class ValueConverterFactory {

    private static ValueConverterFactory INSTANCE = new ValueConverterFactory();

    public static ValueConverterFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(ValueConverterFactory factory) {
        INSTANCE = factory;
    }

    public ValueConverter<?> createValueConverter() {
        return new DelegateValueConverter();
    }
}
