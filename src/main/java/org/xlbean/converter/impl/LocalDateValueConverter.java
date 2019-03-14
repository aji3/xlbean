package org.xlbean.converter.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.xlbean.converter.ValueConverter;

public class LocalDateValueConverter implements ValueConverter<LocalDate> {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter LOCALDATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(LocalDate.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return LOCALDATE_FORMAT.format((LocalDate) obj) + "T00:00:00.000";
    }

    @Override
    public LocalDate toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value, DATETIME_FORMAT);
        } catch (DateTimeParseException e) {
            // ignore
        }
        return null;
    }

}
