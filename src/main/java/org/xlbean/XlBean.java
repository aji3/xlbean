package org.xlbean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xlbean.converter.BeanConverter;
import org.xlbean.converter.BeanConverterFactory;
import org.xlbean.util.XlBeanFactory;

/**
 * Data store class.
 * 
 * <p>
 * Basic concept of this library is to read or write any value on excel sheet as
 * {@link String}. This class is to hold this string value with it's name as
 * map.
 * </p>
 * 
 * <p>
 * This class extends {@link HashMap<String, Object>} and overrides
 * {@link #put(String, Object)} to limit types of object it can hold. This class
 * can have following classes as value of map.
 * <ul>
 * <li>{@link XlBean}</li>
 * <li>{@link XlList}</li>
 * <li>{@link SingleValue}</li>
 * <li>{@link String}</li>
 * </ul>
 * If you want to put any value other than the types above, use
 * {@link #set(Object)} method which converts any type of object, including Map,
 * List or any other custom classes, to XlBean suitable type.
 * </p>
 * 
 * <p>
 * Other methods defined in this class are utility to convert {@link Map} to
 * other format easily.
 * </p>
 * 
 * @author Kazuya Tanikawa
 */
@SuppressWarnings("serial")
public class XlBean extends HashMap<String, Object> {

    /**
     * Convert this {@link XlBean} object to {@link XlList} object with elements
     * belongs to a List retrieved by {@code extractFieldName} and put all the
     * key-values in this object to each element inside the List.
     * 
     * <p>
     * This is an inverse method for {@link XlList#aggregate(String, String...)}
     * .
     * </p>
     * 
     * @param extractFieldName
     * @return
     */
    @SuppressWarnings("unchecked")
    public XlList extract(String extractFieldName) {
        XlList extractedTable = XlBeanFactory.getInstance()
                .createList();
        Object extractField = this.get(extractFieldName);
        if (Iterable.class.isAssignableFrom(extractField.getClass())) {
            for (XlBean extractRow : (Iterable<XlBean>) extractField) {
                putAllExcept(this, extractRow, extractFieldName);
                extractedTable.add(extractRow);
            }
        } else {
            extractedTable.add(this);
        }
        return extractedTable;
    }

    /**
     * Overridden method of {@link HashMap#put(Object, Object)} with type check.
     * 
     * <p>
     * This method allows only the following types:
     * <ul>
     * <li>{@link XlBean}</li>
     * <li>{@link XlList}</li>
     * <li>{@link SingleValue}</li>
     * <li>{@link String}</li> If the value type is other than above, then
     * {@link IllegalArgumentException} will be thrown.
     * </ul>
     * 
     * </p>
     * 
     * @throws IllegalArgumentException
     *             If value type doesn't match allowed type.
     */
    @Override
    public Object put(String key, Object value) {
        if (!canPut(value)) {
            throw new IllegalArgumentException(String.format(
                    "Value set to XlBean must be XlBean, XlList or String. To set value of any other class to this bean, please use #set(Object). set(Object) will scan all the properties in the object and set to this object.",
                    value.getClass()));
        }
        return super.put(key, value);
    }
    
    protected boolean canPut(Object value) {
        return !(value != null && !(value instanceof XlBean || value instanceof XlList || value instanceof String));
    }

    private void putAllExcept(XlBean src, XlBean dst, String excludeFieldName) {
        src.forEach((key, value) -> {
            if (key.equals(excludeFieldName)) {
                return;
            }
            dst.put(key, value);
        });
    }

    public XlBean bean(String key) {
        return (XlBean) get(key);
    }

    public XlList list(String key) {
        return (XlList) get(key);
    }

    /**
     * Returns the object retrieved by {@code key} after casting to generic
     * type.
     * 
     * <p>
     * This method will not check type before casting so make sure that the
     * object is able to be casted to specified type.
     * </p>
     * 
     * @param key
     * @return
     */
    public String value(String key) {
        Object ret = get(key);
        if (ret == null) {
            return null;
        } else {
            return ret.toString();
        }
    }

    private static BeanConverter converter = BeanConverterFactory.getInstance().createBeanConverter();

    public static void updateConverter(BeanConverter converter) {
        XlBean.converter = converter;
    }

    /**
     * Returns the object of {@code destinationClass} filled with the contents
     * mapped from this {@link XlBean} instance.
     * 
     * @param destinationClass
     * @return
     */
    public <T> T of(Class<?> destinationClass) {
        return converter.toBean(this, destinationClass);
    }

    /**
     * Returns the object of {@code destinationClass} filled with the contents
     * mapped from a {@code Map} retrieved by {@code sourceKey}.
     * 
     * <p>
     * If an object retrieved by {@code sourceKey} is null or not {@code Map}
     * instance, then it returns blank instance of {@code destinationClass}.
     * </p>
     * 
     * <p>
     * Mapping from {@code Map} of {@code sourceKey} to instance of
     * {@code destinationClass} is key to property mapping. Refer to
     * {@link BeanConverter} for further detail.
     * </p>
     * 
     * @param sourceKey
     * @param destinationClazz
     * @return
     */
    public <T> T beanOf(String sourceKey, Class<?> destinationClass) {
        Object srcObj = get(sourceKey);
        return converter.toBean(srcObj, destinationClass);
    }

    /**
     * Returns the list of {@code beanClass} instances filled with the contents
     * mapped from beans in the list which are retrieved by {@code sourceKey}
     * from this {@code BaseMap} instance.
     * 
     * @param sourceKey
     * @param beanClass
     * @return
     */
    public <T> List<T> listOf(String sourceKey, Class<?> beanClass) {
        Object srcList = get(sourceKey);
        return converter.toBeanList(srcList, beanClass);
    }

    public void set(Object obj) {
        Object convertedObj = converter.toMap(obj);
        if (convertedObj instanceof XlBean) {
            putAll((XlBean) convertedObj);
        }
    }

    public void set(Object obj, String key) {
        Object convertedObj = converter.toMap(obj);
        put(key, convertedObj);
    }

    public boolean isValuesEmpty() {
        for (Object item : this.values()) {
            if (item == null) {
                continue;
            }
            if (item instanceof String && !((String) item).isEmpty()) {
                return false;
            } else if (item instanceof XlList && ((XlList) item).size() != 0) {
                for (XlBean obj : (XlList) item) {
                    if (!obj.isValuesEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
