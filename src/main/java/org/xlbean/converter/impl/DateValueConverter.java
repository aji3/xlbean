package org.xlbean.converter.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xlbean.converter.ValueConverter;

public class DateValueConverter implements ValueConverter<Date> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(Date.class);
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return DATE_FORMAT.format((Date) obj);
    }

    @Override
    public Date toObject(String value, Class<?> valueClass) {
        if (value == null) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            // ignore
        }
        return null;
    }

}
