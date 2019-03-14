package org.xlbean.data.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.converter.ValueConverter;
import org.xlbean.converter.ValueConverters;
import org.xlbean.definition.Definition;

public class ConverterOptionProcessor {
    public static final String OPTION_CONVERTER = "converter";

    private static Logger log = LoggerFactory.getLogger(ConverterOptionProcessor.class);

    public boolean hasConverterOption(Definition definition) {
        return getConverterOption(definition) != null;
    }

    public String getConverterOption(Definition definition) {
        String option = definition.getOptions().get(OPTION_CONVERTER);
        if (option == null) {
            return null;
        }
        return option;
    }

    public String toString(Object obj, Definition definition) {
        return toString(obj, getConverterOption(definition));
    }

    public String toString(Object obj, String option) {
        if (obj == null) {
            return null;
        }
        ValueConverter<?> valueConverter = ValueConverters.getValueConverterByName(option);
        if (valueConverter != null) {
            return valueConverter.toString(obj);
        } else {
            if (option != null) {
                log.warn("Invalid converter option value: {}", option);
            }
        }
        return obj.toString();
    }

    public Object toObject(String value, Definition definition) {
        return toObject(value, getConverterOption(definition));
    }

    public Object toObject(String value, String option) {
        ValueConverter<?> valueConverter = ValueConverters.getValueConverterByName(option);
        if (valueConverter != null) {
            return valueConverter.toObject(value, null);
        } else {
            if (option != null) {
                log.warn("Invalid converter option value: {}", option);
            }
        }
        return value;
    }
}
