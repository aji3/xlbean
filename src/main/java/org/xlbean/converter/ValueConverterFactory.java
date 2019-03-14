package org.xlbean.converter;

import java.util.Arrays;
import java.util.List;

import org.xlbean.converter.impl.BigDecimalValueConverter;
import org.xlbean.converter.impl.BigIntegerValueConverter;
import org.xlbean.converter.impl.BooleanValueConverter;
import org.xlbean.converter.impl.ByteValueConverter;
import org.xlbean.converter.impl.CharValueConverter;
import org.xlbean.converter.impl.DateValueConverter;
import org.xlbean.converter.impl.DoubleValueConverter;
import org.xlbean.converter.impl.EnumValueConverter;
import org.xlbean.converter.impl.FloatValueConverter;
import org.xlbean.converter.impl.IntValueConverter;
import org.xlbean.converter.impl.LocalDateTimeValueConverter;
import org.xlbean.converter.impl.LocalDateValueConverter;
import org.xlbean.converter.impl.LocalTimeValueCoverter;
import org.xlbean.converter.impl.LongValueConverter;
import org.xlbean.converter.impl.ShortValueConverter;
import org.xlbean.converter.impl.StringValueConverter;

public class ValueConverterFactory {

    private static final List<ValueConverter<?>> DEFAULT_VALUE_CONVERTERS = Arrays.asList(
        new StringValueConverter(),
        new IntValueConverter(),
        new LongValueConverter(),
        new CharValueConverter(),
        new BooleanValueConverter(),
        new ByteValueConverter(),
        new ShortValueConverter(),
        new FloatValueConverter(),
        new DoubleValueConverter(),
        new BigDecimalValueConverter(),
        new BigIntegerValueConverter(),
        new DateValueConverter(),
        new LocalDateTimeValueConverter(),
        new LocalDateValueConverter(),
        new LocalTimeValueCoverter(),
        new EnumValueConverter());

    private static ValueConverterFactory INSTANCE = new ValueConverterFactory();

    public static ValueConverterFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(ValueConverterFactory factory) {
        INSTANCE = factory;
    }

    public List<ValueConverter<?>> getValueConverters() {
        return DEFAULT_VALUE_CONVERTERS;
    }
}
