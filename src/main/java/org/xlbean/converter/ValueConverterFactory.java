package org.xlbean.converter;

public class ValueConverterFactory {

    private static ValueConverterFactory INSTANCE = new ValueConverterFactory();

    public static ValueConverterFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(ValueConverterFactory factory) {
        INSTANCE = factory;
    }

    public ValueConverter createValueConverter() {
        return new ValueConverterImpl();
    }
}
