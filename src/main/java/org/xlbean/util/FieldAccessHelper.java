package org.xlbean.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xlbean.XlBean;
import org.xlbean.XlList;

/**
 * Utility class to handle dotted fields. (e.g. person.father.name)
 * 
 * @author tanikawa
 *
 */
public class FieldAccessHelper {

    /**
     * Parse {@code dottedFieldName}, create new XlBean object if necessary, set
     * {@code data} to {@code bean}.
     * 
     * @param dottedFieldName
     * @param data
     * @param bean
     */
    public static void setValue(String dottedFieldName, Object data, XlBean bean) {
        if (data == null) {
            return;
        }
        if (dottedFieldName.contains(".")) {
            XlBean newBean = toBean(dottedFieldName, data);
            mergeMap(bean, newBean);
        } else {
            bean.put(dottedFieldName, data);
        }
    }

    private static void mergeMap(XlBean base, XlBean additional) {
        if (additional == null) {
            return;
        }
        for (Entry<String, Object> entry : additional.entrySet()) {
            Object baseValue = base.get(entry.getKey());
            if (baseValue == null) {
                base.put(entry.getKey(), entry.getValue());
            } else {
                if (baseValue instanceof Map && entry.getValue() instanceof Map) {
                    mergeMap((XlBean) baseValue, (XlBean) entry.getValue());
                } else if (baseValue instanceof List && entry.getValue() instanceof List) {
                    mergeList((XlList) baseValue, (XlList) entry.getValue());
                } else {
                    base.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private static void mergeList(XlList baseList, XlList additional) {
        for (int i = 0; i < additional.size(); i++) {
            XlBean add = additional.get(i);
            if (add != null) {
                if (baseList.size() > i && baseList.get(i) != null) {
                    mergeMap(baseList.get(i), add);
                } else {
                    baseList.setFillNull(i, add);
                }
            }
        }
    }

    private static XlBean toBean(String name, Object data) {
        if (data == null) {
            return null;
        }
        XlBean ret = XlBeanFactory.getInstance().createBean();
        if (name.contains(".")) {
            String thisName = name.substring(0, name.indexOf('.'));
            String childName = name.substring(name.indexOf('.') + 1);
            XlBean childBean = toBean(childName, data);
            if (thisName.contains("[")) {
                String thisListName = name.substring(0, name.indexOf('['));
                int index = Integer.parseInt(thisName.substring(thisName.indexOf('[') + 1, thisName.length() - 1));
                XlList childList = ret.list(thisListName);
                if (childList == null) {
                    childList = XlBeanFactory.getInstance().createList();
                }
                childList.setFillNull(index, childBean);
                ret.put(thisListName, childList);
            } else {
                ret.put(thisName, childBean);
            }
        } else {
            ret.put(name, data);
        }
        return ret;
    }

    /**
     * Parse dotted fields, scan {@code bean} based on the parsed result and return
     * value in the descendant object.
     * 
     * @param dottedFieldName
     *            Dotted fields (e.g. person.father.name)
     * @param bean
     *            Map object with children of either Map, List or String
     * @return value in {@code bean}'s descendant object
     * 
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(String dottedFieldName, Map<?, ?> bean) {
        if (bean == null) {
            return null;
        }
        if (dottedFieldName.contains(".")) {
            String nameOfTopLayer = dottedFieldName.substring(0, dottedFieldName.indexOf('.'));
            if (nameOfTopLayer.contains("[")) {
                nameOfTopLayer = nameOfTopLayer.substring(0, nameOfTopLayer.indexOf('['));
            }
            Object value = bean.get(nameOfTopLayer);
            if (value == null) {
                return null;
            }
            String nameOfOtherLayers = dottedFieldName.substring(
                dottedFieldName.indexOf('.') + 1,
                dottedFieldName.length());
            if (Map.class.isAssignableFrom(value.getClass())) {
                return (T) getValue(
                    nameOfOtherLayers,
                    (Map<?, ?>) value);
            } else if (List.class.isAssignableFrom(value.getClass())) {
                try {
                    // Any exception occurs here will be ignored
                    // elementName is not in List format (e.g. list[0])
                    // elementIndex is not in number format (e.g. list[aaa], list[])
                    // object in the list is not a Map object
                    String elementIndex = dottedFieldName.substring(
                        dottedFieldName.indexOf('[') + 1,
                        dottedFieldName.indexOf(']'));
                    int index = Integer.parseInt(elementIndex);
                    return (T) getValue(
                        nameOfOtherLayers,
                        (Map<?, ?>) ((List<?>) value).get(index));
                } catch (RuntimeException e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return (T) bean.get(dottedFieldName);
        }
    }
}
