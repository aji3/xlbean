package org.xlbean.converter.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.xlbean.XlBean;
import org.xlbean.converter.BeanConverter;
import org.xlbean.converter.ValueConverters;
import org.xlbean.exception.XlBeanException;
import org.xlbean.util.XlBeanFactory;

/**
 * Converter for {@link XlBean} to generic class or vice-versa.
 *
 * @author Kazuya Tanikawa
 */
public class BeanConverterImpl implements BeanConverter {

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
        if (ValueConverters.canConvert(destinationClazz)) {
            return (T) ValueConverters.toObject(srcObj, destinationClazz);
        }
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

    @SuppressWarnings({ "unchecked" })
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

                convertValueToTypeAndSetToDestObj(value, type, setter, destinationObj);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new XlBeanException(e);
            }
        }
        return (T) destinationObj;
    }

    /**
     * Convert {@code value} to {@code type} and set to {@code destinationObj} using
     * {@code setter}.
     * 
     * <p>
     * First, it checks if the target {@code type} is supported by
     * {@link ValueConverters#canConvert(Class)}. If yes, then convert and set,
     * otherwise go to next step.
     * </p>
     * 
     * <p>
     * Next, try to convert {@code value} based on its type. If {@code value} is an
     * instance of Map, then supported target {@code type} is Map or custom class.
     * If {@code value} is an instance of List, then supported target {@code type}
     * is List or array. If {@code value } is not any of them, then set to setter
     * only when the {@code value} is applicable for setter.
     * </p>
     * 
     * <p>
     * Skip whole process if {@code value} is null. It means that defalut value of a
     * corresponding field of a {@code destinationObj} will be applied.
     * </p>
     * 
     * @param value
     * @param type
     * @param setter
     * @param destinationObj
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     */
    private void convertValueToTypeAndSetToDestObj(Object value, Class<?> type, Method setter, Object destinationObj)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (value == null) {
            return;
        }
        if (ValueConverters.canConvert(type)) {
            // if a target type is supported by ValueConverter, then use ValueConverter
            setter.invoke(destinationObj, ValueConverters.toObject(value, type));
            return;
        }
        if (value instanceof Map) {
            // If a value of sourceMap is Map and field to be mapped is
            // not a leaf class,
            // then this value could be mapped to some bean.
            if (Map.class.isAssignableFrom(type)) {
                // if target type is Map, then copyMap
                Type keyType = getActualTypeArgument(setter, 0, 0);
                Type valueType = getActualTypeArgument(setter, 0, 1);
                Function<Entry<?, ?>, Object> keyConverter = generateMapKeyValueConverter(keyType, true);
                Function<Entry<?, ?>, Object> valueConverter = generateMapKeyValueConverter(valueType, false);
                Map<Object, Object> newMap = ((Map<?, ?>) value)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(keyConverter, valueConverter));
                setter.invoke(destinationObj, newMap);
            } else {
                // if target type is neither supported by ValueConverters or Map, then the type
                // should be a bean class.
                setter.invoke(destinationObj, toBean(value, type));
            }
        } else if (value instanceof Iterable) {
            // If type of both source and destination are List, then map it as List.
            if (Iterable.class.isAssignableFrom(type)) {
                Type childType = getActualTypeArgument(setter, 0, 0);
                List<Object> list = toList(value, childType);
                setter.invoke(destinationObj, list);
            } else if (type.isArray()) {
                List<Object> list = toList(value, type.getComponentType());
                Object ary = Array.newInstance(type.getComponentType(), list.size());
                for (int index = 0; index < list.size(); index++) {
                    Array.set(ary, index, list.get(index));
                }
                Object[] args = { ary };
                setter.invoke(destinationObj, args);
            } else {
                // if target type is neither supported by ValueConverters or Iterable, then skip
                // this value
            }
        } else {
            // Set without conversion only when the value is assignable for setter
            Class<?>[] targetType = setter.getParameterTypes();
            if (targetType != null && targetType.length == 1 && targetType[0].isAssignableFrom(value.getClass())) {
                setter.invoke(destinationObj, value);
            } else {
                // skip if the value is not convertable
            }
        }

    }

    /**
     * Generate a Lambda function to convert key or value of Map.Entry to
     * {@code targetType}.
     * 
     * @param targetType
     * @param isKey
     * @return
     */
    private Function<Entry<?, ?>, Object> generateMapKeyValueConverter(Type targetType, boolean isKey) {
        final Class<?> targetClass;
        if (targetType == null || targetType instanceof WildcardType) {
            targetClass = null;
        } else {
            targetClass = forName(targetType.getTypeName());
        }
        return entry -> {
            System.out.println(entry);
            System.out.println(targetClass);
            if (targetClass == null) {
                return isKey ? entry.getKey() : entry.getValue();
            } else {
                return toBean(isKey ? entry.getKey() : entry.getValue(), targetClass);
            }
        };
    }

    private static Map<String, Class<?>> PRIMITIVES = new HashMap<>();
    static {
        PRIMITIVES.put("byte", byte.class);
        PRIMITIVES.put("short", short.class);
        PRIMITIVES.put("int", int.class);
        PRIMITIVES.put("long", long.class);
        PRIMITIVES.put("float", float.class);
        PRIMITIVES.put("double", double.class);
        PRIMITIVES.put("char", char.class);
        PRIMITIVES.put("String", String.class);
        PRIMITIVES.put("boolean", boolean.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Object> toList(Object value, Type childType) {
        List<Object> list = new ArrayList<>();
        if (childType != null) {
            Class<?> childTypeClass = forName(childType.getTypeName());
            for (Object srcObj : (Iterable<?>) value) {
                if (srcObj == null) {
                    list.add(srcObj);
                    continue;
                }
                if (ValueConverters.canConvert(childTypeClass)) {
                    list.add(ValueConverters.toObject(srcObj, childTypeClass));
                } else {
                    Object bean = toBean(srcObj, childTypeClass);
                    list.add(bean);
                }
            }
        } else {
            list.addAll((Collection) value);
        }
        return list;
    }

    private Class<?> forName(String className) {
        Class<?> ret = PRIMITIVES.get(className);
        if (ret == null) {
            try {
                ret = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new XlBeanException(e);
            }
        }
        return ret;
    }

    private Type getActualTypeArgument(Method setter, int parameterizedTypeIndex,
            int actualTypeArgumentIndex) {
        Type[] genericParameterTypes = setter.getGenericParameterTypes();
        if (genericParameterTypes != null && genericParameterTypes.length > parameterizedTypeIndex) {
            Type type = genericParameterTypes[parameterizedTypeIndex];
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) genericParameterTypes[parameterizedTypeIndex];
                Type[] actualTypeArguments = p.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length > actualTypeArgumentIndex) {
                    return actualTypeArguments[actualTypeArgumentIndex];
                }
            }
        }
        return null;
    }

    private <T> T instantiate(Class<T> clazz) {
        if (clazz.isAssignableFrom(Map.class)) {
            return clazz.cast(XlBeanFactory.getInstance().createBean());
        } else if (clazz.isAssignableFrom(List.class)) {
            return clazz.cast(XlBeanFactory.getInstance().createList());
        }
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
            // if a value is not either Map or Iterable, then check if the value is
            // supported by ValueConverter.
            // if it is supported,then it will be converted to String, else the value is
            // converted to a Map instance with which the entries are the properties of the
            // bean.
            if (ValueConverters.canConvert(value.getClass())) {
                return ValueConverters.toString(value);
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
                if (pd.getReadMethod() == null) {
                    continue;
                }
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
