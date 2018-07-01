package org.xlbean.converter;

import java.util.ArrayList;
import java.util.List;

import org.xlbean.XlBean;

/**
 * Converter for {@link XlBean} to generic class or vice-versa.
 *
 * <p>
 * This class will regard the classes defined in
 * {@link ValueConverter#canConvert(Class)} as leaf classes and set them as
 * value. Other than them, converter tries to dig into the object and convert.
 * </p>
 * 
 * @author Kazuya Tanikawa
 */
public interface BeanConverter {

    /**
     * Converts {@code srcObj} to the instance of {@code destinationClazz}
     * 
     * @param srcObj
     * @param destinationClazz
     * @return
     */
    public <T> T toBean(Object srcObj, Class<T> destinationClazz);

    /**
     * Convert {@code srcList} to {@link ArrayList}. If {@code srcList} is not an
     * instance of {@link Iterable}, then it returns blank {@link ArrayList}.
     *
     * @param srcList
     * @param beanClass
     * @return
     */
    public <T> List<T> toBeanList(Object srcList, Class<T> beanClass);

    /**
     * Converts {@code value} to the instance of Map.
     * 
     * <p>
     * Basically key-vakues in the {@code value} becomes properties of the map. If
     * the {@code value} is list or other bean, it will be converted to map
     * recursively.
     * </p>
     * 
     * @param value
     * @return
     */
    public Object toMap(Object value);
}
