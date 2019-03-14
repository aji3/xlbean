package org.xlbean.converter.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xlbean.XlBean;
import org.xlbean.converter.BeanConverter;
import org.xlbean.converter.ValueConverter;
import org.xlbean.converter.ValueConverterFactory;
import org.xlbean.exception.XlBeanException;
import org.xlbean.util.XlBeanFactory;

/**
 * Converter for {@link XlBean} to generic class or vice-versa.
 *
 * @author Kazuya Tanikawa
 */
public class BeanConverterImpl implements BeanConverter {

    private ValueConverter<?> converter = ValueConverterFactory.getInstance().createValueConverter();

    /**
     * Convert the object retrieved by the given sourceKey to an object of given
     * destinationClass if the instance if an object of Map.
     *
     * @param sourceKey
     * @param destinationClazz
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T toBean(Object srcObj, Class<T> destinationClazz) {
        T dstObj = instantiate(destinationClazz);
        if (srcObj instanceof Map) {
            // srcObj is mapped to the instance of destinationClazz only when it is Map
            Map<String, Object> map = (Map<String, Object>) srcObj;
            return toBean(map, dstObj);
        }
        return dstObj;
    }

    /**
     * Convert {@code srcList} to {@link ArrayList}. If {@code srcList} is not an
     * instance of {@link Iterable}, then it returns blank {@link ArrayList}.
     *
     * @param srcList
     * @param beanClass
     * @return
     */
    @Override
    public <T> List<T> toBeanList(Object srcList, Class<T> beanClass) {
        List<T> retList = new ArrayList<>();
        if (srcList instanceof Iterable) {
            for (Object obj : (Iterable<?>) srcList) {
                retList.add(toBean(obj, beanClass));
            }
        }
        return retList;
    }

    private Map<Class<?>, Map<String, PropertyDescriptor>> classPropertyDescriptorMap = new HashMap<>();

    protected PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        Map<String, PropertyDescriptor> propertyDescriptorMap = classPropertyDescriptorMap.get(clazz);
        if (propertyDescriptorMap == null) {
            propertyDescriptorMap = new HashMap<>();
            try {
                List<PropertyDescriptor> propertyList = Arrays.asList(
                    Introspector.getBeanInfo(clazz).getPropertyDescriptors());

                for (PropertyDescriptor pd : propertyList) {
                    propertyDescriptorMap.put(pd.getName(), pd);
                }
            } catch (IntrospectionException e) {
                throw new XlBeanException(e);
            }
        }
        return propertyDescriptorMap.get(propertyName);
    }

    @SuppressWarnings("unchecked")
    protected <T> T toBean(Map<String, Object> sourceMap, Object destinationObj) {
        for (Entry<String, Object> entry : sourceMap.entrySet()) {
            try {
                PropertyDescriptor pd = getPropertyDescriptor(destinationObj.getClass(), entry.getKey());
                if (pd == null) {
                    continue;
                }
                Method setter = pd.getWriteMethod();
                Class<?> type = pd.getPropertyType();

                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if (value instanceof Map) {
                    // If a value of sourceMap is Map and field to be mapped is
                    // not a leaf class,
                    // then this value could be mapped to some bean.
                    Object obj = toBean(value, type);
                    setter.invoke(destinationObj, obj);
                } else if (Iterable.class.isAssignableFrom(value.getClass())
                        && Iterable.class.isAssignableFrom(type)) {
                    List<Object> obj = new ArrayList<>();
                    ParameterizedType p = (ParameterizedType) pd.getWriteMethod().getGenericParameterTypes()[0];
                    Type childType = p.getActualTypeArguments()[0];
                    for (Object srcObj : (Iterable<?>) value) {
                        if (srcObj == null) {
                            obj.add(srcObj);
                            continue;
                        }
                        if (isLeaf(srcObj.getClass())) {
                            obj.add(srcObj);
                        } else {
                            Object bean = toBean(srcObj, Class.forName(childType.getTypeName()));
                            obj.add(bean);
                        }
                    }
                    setter.invoke(destinationObj, obj);
                } else {
                    // If a value is neither Map or List, then it should be
                    // String.
                    Object convertedValue = converter.toObject(
                        value instanceof String ? (String) value : value.toString(),
                        type);
                    setter.invoke(destinationObj, convertedValue);
                }
            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
                throw new XlBeanException(e);
            }
        }
        return (T) destinationObj;
    }

    private boolean isLeaf(Class<?> clazz) {
        return converter.canConvert(clazz);
    }

    private <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new XlBeanException(e);
        }
    }

    @Override
    public Object toMap(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            XlBean xlBean = XlBeanFactory.getInstance().createBean();
            Map<?, ?> map = (Map<?, ?>) value;
            for (Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                String mapKey = entry.getKey().toString();
                Object mapValue = toMap(entry.getValue());
                xlBean.put(mapKey, mapValue);
            }
            return xlBean;
        } else if (value instanceof Iterable) {
            List<XlBean> xlList = XlBeanFactory.getInstance().createList();
            List<?> list = (List<?>) value;
            for (Object elem : list) {
                Object val = toMap(elem);
                if (val instanceof XlBean) {
                    xlList.add((XlBean) val);
                } else {
                    XlBean elemBean = XlBeanFactory.getInstance().createBean();
                    elemBean.put("value", val);
                    xlList.add(elemBean);
                }
            }
            return xlList;
        } else {
            //
            if (converter.canConvert(value.getClass())) {
                return converter.toString(value);
            } else {
                //
                return toMapInternal(value);
            }
        }
    }

    private XlBean toMapInternal(Object obj) {
        XlBean retBean = XlBeanFactory.getInstance().createBean();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
                String name = pd.getName();
                if ("class".equals(name)) {
                    continue;
                }
                Object value = pd.getReadMethod().invoke(obj, (Object[]) null);
                retBean.put(name, toMap(value));
            }
        } catch (IntrospectionException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new XlBeanException(e);
        }
        return retBean;
    }
}
