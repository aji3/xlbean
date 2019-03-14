package org.xlbean.converter.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.xlbean.converter.ValueConverter;

public class LocalTimeValueCoverter implements ValueConverter<LocalTime> {

    private static final DateTimeFormatter LOCALTIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public String getName() {
        return "localtime";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(LocalTime.class);
    }

    @Override
    public String toString(Object obj) {
        // TODO Auto-generated method stub
        return LOCALTIME_FORMAT.format((LocalTime) obj);
    }

    @Override
    public LocalTime toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return LocalTime.parse(value, LOCALTIME_FORMAT);
        } catch (DateTimeParseException e) {
            // ignore
        }
        return null;
    }

}
