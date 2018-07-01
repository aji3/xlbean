package org.xlbean;

import java.util.HashMap;
import java.util.List;

import org.xlbean.converter.BeanConverter;
import org.xlbean.converter.BeanConverterFactory;

/**
 * Implementation of XlBean which inherits {@link HashMap}.
 * 
 * <p>
 * Override {@link #put(String, Object)} to validate a object given to the
 * method before actually put it to this map, in order to prevent unexpected
 * behavior which will be caused by various types of values in this map which
 * are not supported by this library by default. However, as long as you are
 * sure about the types of the values, the validation can be omitted or changed
 * to other logic by overriding {@link #canPut(Object)}.
 * </p>
 * 
 * @author Kazuya Tanikawa
 */
@SuppressWarnings("serial")
public class XlBeanImpl extends HashMap<String, Object> implements XlBean {

    /**
     * Overridden method of {@link HashMap#put(Object, Object)} with type check.
     *
     * <p>
     * This method allows only the following types:
     *
     * <ul>
     * <li>{@link XlBean}
     * <li>{@link List}
     * <li>{@link String} If the value type is other than above, then
     * {@link IllegalArgumentException} will be thrown.
     * </ul>
     *
     * @throws IllegalArgumentException
     *             If value type doesn't match allowed type.
     */
    @Override
    public Object put(String key, Object value) {
        if (!canPut(value)) {
            throw new IllegalArgumentException(
                String.format(
                    "Value set to XlBean must be XlBean, List<XlBean> or String. To set value of any other class to this bean, use #set(String, Object). #set(String, Object) will scan all the properties in the object and set to this object. (Actual: %s)",
                    value.getClass()));
        }
        return super.put(key, value);
    }

    /**
     * Method used in put method to check if the {@code value} can be put to this
     * map.
     * 
     * @param value
     * @return
     */
    protected boolean canPut(Object value) {
        return !(value != null
                && !(value instanceof XlBean || value instanceof List || value instanceof String));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#bean(java.lang.String)
     */
    public XlBean bean(String key) {
        return (XlBean) get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#beans(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<XlBean> beans(String key) {
        return (List<XlBean>) get(key);
    }

    private BeanConverter converter = BeanConverterFactory.getInstance().createBeanConverter();

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#of(java.lang.Class)
     */
    public <T> T of(Class<T> destinationClass) {
        return converter.toBean(this, destinationClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#beanOf(java.lang.String, java.lang.Class)
     */
    public <T> T beanOf(String sourceKey, Class<T> destinationClass) {
        Object srcObj = get(sourceKey);
        return converter.toBean(srcObj, destinationClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#listOf(java.lang.String, java.lang.Class)
     */
    public <T> List<T> listOf(String sourceKey, Class<T> beanClass) {
        Object srcList = get(sourceKey);
        return converter.toBeanList(srcList, beanClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#set(java.lang.Object)
     */
    public void set(Object obj) {
        Object convertedObj = converter.toMap(obj);
        if (convertedObj instanceof XlBeanImpl) {
            putAll((XlBeanImpl) convertedObj);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#set(java.lang.String, java.lang.Object)
     */
    public void set(String key, Object obj) {
        Object convertedObj = converter.toMap(obj);
        put(key, convertedObj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#string(java.lang.String)
     */
    @Override
    public String string(String key) {
        return get(key) == null ? null : get(key).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xlbean.XlBean#strings(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> strings(String key) {
        return (List<String>) get(key);
    }

}
