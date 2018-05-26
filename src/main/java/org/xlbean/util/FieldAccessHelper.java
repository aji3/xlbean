package org.xlbean.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xlbean.XlBean;

/**
 * Utility class to handle dotted fields. (e.g. person.father.name)
 * 
 * @author tanikawa
 *
 */
public class FieldAccessHelper {

    private static final Pattern INDEX = Pattern.compile("\\[([0-9]+)\\]");

    /**
     * Parse {@code dottedFieldName}, create new XlBean object if necessary, set
     * {@code data} to {@code bean}.
     * 
     * @param dottedFieldName
     * @param data
     * @param bean
     */
    public static void setValue(String dottedFieldName, Object data, Map<String, Object> bean) {
        if (data == null) {
            return;
        }
        FieldWrapper rootField = parseDottedFieldName(dottedFieldName);
        rootField.linkTarget(bean);
        rootField.buildObject(data);
    }

    /**
     * Parse "aaa.bbb.ccc[0][1].ddd..." and create FieldWrapper instance for each of
     * "aaa", "bbb", "ccc[0][1]", etc., then return the root FieldWrapper instance.
     * 
     * @param dottedFieldName
     * @return
     */
    private static FieldWrapper parseDottedFieldName(String dottedFieldName) {
        String[] names = dottedFieldName.split("\\.");

        FieldWrapper rootField = null;
        FieldWrapper parent = null;
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            FieldWrapper field = new FieldWrapper(name);
            if (rootField == null) {
                rootField = field;
            }
            if (parent != null) {
                parent.setChild(field);
            }
            parent = field;
        }
        return rootField;
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
        if (bean == null || dottedFieldName == null) {
            return null;
        }
        FieldWrapper field = parseDottedFieldName(dottedFieldName);
        field.linkTarget((Map<String, Object>) bean);
        return (T) field.getLeaf().getValue();
    }

    /**
     * Set {@code data} to {@code index} of {@code list}. If index is bigger than
     * current size of {@code list}, then fills the list with null.
     * 
     * @param list
     * @param index
     * @param data
     */
    public static <T> void setFillNull(List<T> list, int index, T data) {
        while (list.size() < index) {
            list.add(null);
        }
        if (list.size() == index) {
            list.add(data);
        } else {
            list.set(index, data);
        }
    }

    /**
     * Class to wrap field defined in "aaa[0][1]" format.
     * 
     * @author tanikawa
     *
     */
    private static class FieldWrapper {
        private String name;
        private List<ListWrapper> lists = new ArrayList<>();
        private FieldWrapper child;
        private Map<String, Object> target;

        public FieldWrapper(String originalStr) {
            String fieldName = originalStr;
            if (originalStr.contains("[")) {
                fieldName = originalStr.substring(0, originalStr.indexOf("["));
                Matcher matcher = INDEX.matcher(originalStr);
                while (matcher.find()) {
                    lists.add(new ListWrapper(Integer.parseInt(matcher.group(1))));
                }
            }
            this.name = fieldName;
        }

        public void setChild(FieldWrapper child) {
            this.child = child;
        }

        private boolean isLeaf() {
            return child == null;
        }

        public FieldWrapper getLeaf() {
            if (isLeaf()) {
                return this;
            } else {
                return child.getLeaf();
            }
        }

        /**
         * Return value for "aaa[0]" representation. In this case, value for "aaa" is
         * list but this method returns value for "aaa[0]" so the value is an element of
         * the list.
         * 
         * @return
         */
        public Object getValue() {
            if (lists.isEmpty()) {
                return target == null ? null : target.get(name);
            } else {
                return lists.get(lists.size() - 1).getValue();
            }
        }

        @SuppressWarnings("unchecked")
        public void linkTarget(Map<String, Object> target) {
            if (target == null) {
                return;
            }
            this.target = target;
            Object childValue = target.get(name);
            if (!lists.isEmpty()) {
                for (int i = 0; i < lists.size(); i++) {
                    if (childValue instanceof List) {
                        ListWrapper list = lists.get(i);
                        list.linkTarget((List<Object>) childValue);
                        childValue = list.getTargetForIndex();
                    }
                }
            }
            if (!isLeaf()) {
                if (childValue instanceof Map) {
                    child.linkTarget((Map<String, Object>) childValue);
                }
            }
        }

        public Object buildObject(Object value) {
            Object newValue = value;
            if (!isLeaf()) {
                newValue = child.buildObject(newValue);
            }
            if (target == null) {
                target = XlBeanFactory.getInstance().createBean();
            }
            for (int i = lists.size() - 1; i >= 0; i--) {
                ListWrapper list = lists.get(i);
                newValue = list.buildObject(newValue);
            }
            target.put(name, newValue);
            return target;
        }
    }

    private static class ListWrapper {
        private int index;
        private List<Object> target;

        public ListWrapper(int index) {
            this.index = index;
        }

        public void linkTarget(List<Object> target) {
            this.target = target;
        }

        public Object getTargetForIndex() {
            if (target.size() > index) {
                return target.get(index);
            } else {
                return null;
            }
        }

        public Object buildObject(Object value) {
            if (target == null) {
                if (value instanceof XlBean) {
                    target = XlBeanFactory.getInstance().createList();
                } else {
                    target = new ArrayList<Object>();
                }
            }
            setFillNull(target, index, value);
            return target;
        }

        public Object getValue() {
            if (target.size() > index) {
                return target.get(index);
            } else {
                return null;
            }
        }

    }

}
