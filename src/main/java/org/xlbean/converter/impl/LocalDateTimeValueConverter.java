package org.xlbean.converter.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import org.xlbean.converter.ValueConverter;

public class LocalDateTimeValueConverter implements ValueConverter<LocalDateTime> {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(LocalDateTime.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return DATETIME_FORMAT.format((TemporalAccessor) obj);
    }

    @Override
    public LocalDateTime toObject(String value, Class<?> valueClass) {
        try {
            return LocalDateTime.parse(value, DATETIME_FORMAT);
        } catch (DateTimeParseException e) {
            // ignore
        }
        return null;
    }

}
