package org.xlbean.converter;

/**
 * Factory for {@link BeanConverterImpl} for the implementation of
 * {@link BeanConverter} interface.
 *
 * @author Kazuya Tanikawa
 */
public class BeanConverterFactory {

    private static BeanConverterFactory INSTANCE = new BeanConverterFactory();

    public static BeanConverterFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(BeanConverterFactory factory) {
        INSTANCE = factory;
    }

    public BeanConverter createBeanConverter() {
        return new BeanConverterImpl();
    }
}
