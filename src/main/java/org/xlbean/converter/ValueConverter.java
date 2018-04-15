package org.xlbean.converter;

import org.xlbean.XlBean;

/**
 * Converter used for converting String values in {@link XlBean} to object or
 * vice-versa.
 *
 * <p>
 * This class defines all types which can be used as a leaf value and it can be
 * checked by a method {@link #canConvert(Class)}. Any class which is not
 * convertable is treated as custom object and dug down into the object to
 * convert all the elements inside the object.
 *
 * @author Kazuya Tanikawa
 */
public interface ValueConverter {

    /**
     * Returns true when the given {@code clazz} is convertable by this converter.
     *
     * @param clazz
     * @return
     */
    public boolean canConvert(Class<?> clazz);

    /**
     * Convert given {@code obj} to the {@link String} value to be hold in
     * {@link XlBean}.
     *
     * @param obj
     * @return
     */
    public String toString(Object obj);

    /**
     * Convert given {@code value} to an instance of {@code valueClass}.
     *
     * <p>
     * This method will not throw any exception even if the {@code value} is not
     * able to be converted to the specified class. If the {@code value} is null,
     * then always returns null.
     *
     * @param value
     * @param valueClass
     * @return
     */
    public Object toObject(String value, Class<?> valueClass);
}
