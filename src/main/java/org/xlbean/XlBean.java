package org.xlbean;

import java.util.List;
import java.util.Map;

import org.xlbean.converter.BeanConverter;

/**
 * Interface for data store.
 *
 * <p>
 * Basic concept of this library is to read or write any value on excel sheet as
 * {@link String}, and make it easy to access. This is an interface for the data
 * holder class.
 *
 * <p>
 * This class extends {@link Map<String, Object>}. The implementation of this
 * interface is intended to hold instances of following classes as values of
 * this map.
 * 
 * <ul>
 * <li>{@link XlBean}
 * <li>{@link List}
 * <li>{@link String}
 * </ul>
 *
 * If you want to put any value other than the types above, use
 * {@link #set(Object)} method which converts any type of object, including Map,
 * List or any other custom classes, to XlBean suitable type.
 *
 * <p>
 * Other methods defined in this class are utility to convert {@link Map} to
 * other format easily.
 *
 * @author Kazuya Tanikawa
 */
public interface XlBean extends Map<String, Object> {

    /**
     * Returns an object registered to this XlBean with given {@code key} after
     * casting to XlBean. This method doesn't check type before casting so make sure
     * that the object is XlBean.
     * 
     * @param key
     * @return
     */
    public XlBean bean(String key);

    /**
     * Returns an object registered to this XlBean with given {@code key} after
     * casting to List<XlBean>. This method doesn't check type before casting so
     * make sure that the object is List<XlBean>.
     * 
     * @param key
     * @return
     */
    public List<XlBean> beans(String key);

    /**
     * Returns an object registered to this XlBean with given {@code key} after
     * #toString().
     * 
     * @param key
     * @return
     */
    public String string(String key);

    /**
     * Returns an object registered to this XlBean with given {@code key} after
     * casting to List<String>. Contents of the list will be treated as String, so
     * make sure that the elements of this list is String.
     * 
     * @param key
     * @return
     */
    public List<String> strings(String key);

    /**
     * Returns the object of {@code destinationClass} filled with the contents
     * mapped from this {@link XlBean} instance.
     *
     * @param destinationClass
     * @return
     */
    public <T> T of(Class<T> destinationClass);

    /**
     * Returns the object of {@code destinationClass} filled with the contents
     * mapped from a {@code
     * Map} retrieved by {@code sourceKey}.
     *
     * <p>
     * If an object retrieved by {@code sourceKey} is null or not {@code Map}
     * instance, then it returns blank instance of {@code destinationClass}.
     *
     * <p>
     * Mapping from {@code Map} of {@code sourceKey} to instance of
     * {@code destinationClass} is key to property mapping. Refer to
     * {@link BeanConverter} for further detail.
     *
     * @param sourceKey
     * @param destinationClazz
     * @return
     */
    public <T> T beanOf(String sourceKey, Class<T> destinationClass);

    /**
     * Returns the list of {@code beanClass} instances filled with the contents
     * mapped from beans in the list which are retrieved by {@code sourceKey} from
     * this {@code BaseMap} instance.
     *
     * @param sourceKey
     * @param beanClass
     * @return
     */
    public <T> List<T> listOf(String sourceKey, Class<T> beanClass);

    /**
     * Convert each property of given {@code obj} to key-value of this XlBean
     * object.
     *
     * <p>
     * Conversion is done by {@link BeanConverter#toMap(Object)}.
     *
     * @param obj
     */
    public void set(Object obj);

    /**
     * Convert given {@code obj} to XlBean and put to this XlBean object with given
     * {@code key}.
     *
     * <p>
     * Conversion is done by {@link BeanConverter#toMap(Object)}.
     *
     * @param key
     * @param obj
     */
    public void set(String key, Object obj);

}
