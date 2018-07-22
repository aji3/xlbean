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

    /**
     * Convert a value registered by {@code key} to Integer and return.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, IntValueConverter is used hence exception will be
     * ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public Integer integer(String key);

    /**
     * Convert all values in a list registered by {@code key} to Integer and return
     * a new instance of list with the Integer values.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, IntValueConverter is used hence exception will be
     * ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Integer> integers(String key);

    /**
     * Convert a value registered by {@code key} to Long and return.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, LongValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public Long lng(String key);

    /**
     * Convert all values in a list registered by {@code key} to Long and return a
     * new instance of list with the Long values.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, LongValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Long> longs(String key);

    /**
     * Convert a value registered by {@code key} to Short and return.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, ShortValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public Short shrt(String key);

    /**
     * Convert all values in a list registered by {@code key} to Short and return a
     * new instance of list with the Short values.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, ShortValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Short> shorts(String key);

    /**
     * Convert a value registered by {@code key} to Float and return.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, FloatValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public Float flt(String key);

    /**
     * Convert all values in a list registered by {@code key} to Float and return a
     * new instance of list with the Float values.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, FloatValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Float> floats(String key);

    /**
     * Convert a value registered by {@code key} to Double and return.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, DoubleValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public Double dbl(String key);

    /**
     * Convert all values in a list registered by {@code key} to Double and return a
     * new instance of list with the Double values.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, DoubleValueConverter is used hence exception will
     * be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Double> doubles(String key);

    /**
     * Convert a value registered by {@code key} to Boolean and return.
     * 
     * <p>
     * By default, BooleanValueConverter.
     * </p>
     * 
     * @param key
     * @return
     */
    public Boolean bool(String key);

    /**
     * Convert all values in a list registered by {@code key} to Boolean and return
     * a new instance of list with the Boolean values.
     * 
     * <p>
     * By default, BooleanValueConverter is used hence exception will be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Boolean> bools(String key);

    /**
     * Convert a value registered by {@code key} to Character and return.
     * 
     * <p>
     * Exception handling in case of NumberFormatException depends on the
     * implementation. By default, CharacterValueConverter is used hence exception
     * will be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public Character character(String key);

    /**
     * Convert all values in a list registered by {@code key} to Character and
     * return a new instance of list with the Character values.
     * 
     * <p>
     * By default, CharValueConverter is used hence exception will be ignored.
     * </p>
     * 
     * @param key
     * @return
     */
    public List<Character> characters(String key);

}
