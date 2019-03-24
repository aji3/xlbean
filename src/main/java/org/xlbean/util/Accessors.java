package org.xlbean.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xlbean.util.Accessors.AccessorConfig.AccessorConfigBuilder;

/**
 * Utility class to get/set values from Map object by using nested field name.
 * (e.g. person.father.name).
 * 
 * <p>
 * Behavior for null, blank map and blank list can be changed by using
 * {@link AccessorConfig}. Refer to {@link AccessorConfigBuilder} for detail
 * description of each configuration.
 * </p>
 * 
 * @author tanikawa
 *
 */
public class Accessors {

    private static final Pattern INDEX = Pattern.compile("\\[([0-9]+)\\]");

    private static Accessors INSTANCE = new Accessors();

    /**
     * If true, remove key whose value is null, empty list or empty map.
     */
    private AccessorConfig config = new AccessorConfig();

    public Accessors() {}

    public Accessors(AccessorConfig config) {
        this.config = config;
    }

    /**
     * Parse {@code dottedFieldName}, create new XlBean object if necessary, set
     * {@code data} to {@code bean}.
     * 
     * @param dottedFieldName
     * @param data
     * @param bean
     */
    public static void setValue(String dottedFieldName, Object data, Map<String, Object> bean) {
        INSTANCE.set(dottedFieldName, data, bean);
    }

    /**
     * Parse {@code dottedFieldName}, create new XlBean object if necessary, set
     * {@code data} to {@code bean} by new instance of {@link Accessors} configured
     * by given {@code ignoreNull}, {@code ignoreBlankMap} and
     * {@code ignoreBlankList}
     * 
     * @param dottedFieldName
     * @param data
     * @param bean
     * @param ignoreNull
     * @param ignoreBlankMap
     * @param ignoreBlankList
     */
    public static void setValue(String dottedFieldName, Object data, Map<String, Object> bean,
            boolean ignoreNull, boolean ignoreBlankMap, boolean ignoreBlankList) {
        new Accessors(
            new AccessorConfigBuilder()
                .ignoreNull(ignoreNull)
                .ignoreBlankMap(ignoreBlankMap)
                .ignoreBlankList(ignoreBlankList)
                .build()).set(dottedFieldName, data, bean);
    }

    public static void setValue(String dottedFieldName, Object data, Map<String, Object> bean, AccessorConfig config) {
        new Accessors(config).set(dottedFieldName, data, bean);
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
    public static <T> T getValue(String dottedFieldName, Map<String, Object> bean) {
        return INSTANCE.get(dottedFieldName, bean);
    }

    public static void setInstance(Accessors helper) {
        INSTANCE = helper;
    }

    public static AccessorConfig getConfig() {
        return INSTANCE.config;
    }

    public void set(String dottedFieldName, Object data, Map<String, Object> bean) {
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
    private FieldWrapper parseDottedFieldName(String dottedFieldName) {
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
     * Parse {@code dottedFieldName} and get corresponding data from {@code bean}.
     * 
     * <p>
     * {@code dottedFieldName} can be a value like "field",
     * "parentBean.childElement", "list[1]", "parent.list[0]", etc
     * </p>
     * 
     * <p>
     * This method will not do any validation for {@code dottedFieldName}.
     * </p>
     * 
     * @param dottedFieldName
     * @param bean
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String dottedFieldName, Map<String, Object> bean) {
        if (bean == null || dottedFieldName == null) {
            return null;
        }
        FieldWrapper field = parseDottedFieldName(dottedFieldName);
        field.linkTarget(bean);
        return (T) field.getLeaf().getValue();
    }

    /**
     * Class to wrap field defined in "aaa[0][1]" format.
     * 
     * @author tanikawa
     *
     */
    private class FieldWrapper {
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
            for (int i = 0; i < lists.size(); i++) {
                if (childValue instanceof List) {
                    ListWrapper list = lists.get(i);
                    list.linkTarget((List<Object>) childValue);
                    childValue = list.getTargetForIndex();
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
                target = createMap();
            }
            for (int i = lists.size() - 1; i >= 0; i--) {
                ListWrapper list = lists.get(i);
                newValue = list.buildObject(newValue);
            }
            if (config.isIgnoreNull() && newValue == null) {
                target.remove(name);
            } else if (config.isIgnoreBlankList() && newValue instanceof Collection && ((Collection<?>) newValue)
                .isEmpty()) {
                target.remove(name);
            } else if (config.isIgnoreBlankMap() && newValue instanceof Map && ((Map<?, ?>) newValue).isEmpty()) {
                target.remove(name);
            } else {
                target.put(name, newValue);
            }
            if (config.isIgnoreBlankMap() && target.isEmpty()) {
                return null;
            } else {
                return target;
            }
        }
    }

    private class ListWrapper {
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
                target = createList();
            }
            if (!(config.isIgnoreNull() && value == null)) {
                BeanHelper.setFillNull(target, index, value);
            }
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

    protected Map<String, Object> createMap() {
        return XlBeanFactory.getInstance().createBean();
    }

    protected List<Object> createList() {
        return new ArrayList<>();
    }

    /**
     * Configuration holder for Accessors.
     * 
     * 
     * @author tanikawa
     *
     */
    public static class AccessorConfig {
        private boolean ignoreNull = true;
        private boolean ignoreBlankMap = true;
        private boolean ignoreBlankList = true;

        public boolean isIgnoreNull() {
            return ignoreNull;
        }

        public void setIgnoreNull(boolean ignoreNull) {
            this.ignoreNull = ignoreNull;
        }

        public boolean isIgnoreBlankMap() {
            return ignoreBlankMap;
        }

        public void setIgnoreBlankMap(boolean ignoreBlankMap) {
            this.ignoreBlankMap = ignoreBlankMap;
        }

        public boolean isIgnoreBlankList() {
            return ignoreBlankList;
        }

        public void setIgnoreBlankList(boolean ignoreBlankList) {
            this.ignoreBlankList = ignoreBlankList;
        }

        /**
         * Builder for AccessorConfig.
         * <p>
         * "ignoreNull": When true, key for null value will not be set to XlBean,
         * otherwise, the key will be set to XlBean with null value. For instance, if a
         * name of a field is "aaa" and value for this field is null, then the result
         * will be as follows:
         * <ul>
         * <li>ignoreNull == true: {}</li>
         * <li>ignoreNull == false: {"aaa": null}</li>
         * </ul>
         * </p>
         * 
         * <p>
         * "ignoreBlankMap": When true, a XlBean (or Map) instance without any key will
         * be treated as null. For instance, if a name of a field is "test.test" and
         * value for this field is null, then the result will be as follows:
         * <ul>
         * <li>ignoreBlankMap == true && ignoreNull == true: {}</li>
         * <li>ignoreBlankMap == false && ignoreNull == true: {"test":{}}</li>
         * </ul>
         * Using ignoreNull == true case because if ignoreNull == false, then the map
         * will become {"test":{"test": null}} which is not empty anyway.
         * </p>
         * 
         * <p>
         * "ignoreBlankList": When true, a List instance whose size is 0 will be treated
         * as null. For instance, if a name of field is "testList" and value for this
         * field is null, then the result will be as follows:
         * <ul>
         * <li>ignoreBlankList == true && ignoreNull == true: {}</li>
         * <li>ignoreBlankList == false && ignoreNull == true: {"testList":[]}</li>
         * </ul>
         * Using ignoreNull == true case because if ignoreNull == false, then the field
         * will become {"testList":[null]} which is not empty anyway.
         * </p>
         * 
         * @author tanikawa
         *
         */
        public static class AccessorConfigBuilder {
            private AccessorConfig config = new AccessorConfig();

            /**
             * When true, key for null value will not be set to XlBean, otherwise, the key
             * will be set to XlBean with null value. For instance, if a name of a field is
             * "aaa" and value for this field is null, then the result will be as follows:
             * <ul>
             * <li>ignoreNull == true: {}</li>
             * <li>ignoreNull == false: {"aaa": null}</li>
             * </ul>
             * 
             * @param ignoreNull
             * @return
             */
            public AccessorConfigBuilder ignoreNull(boolean ignoreNull) {
                config.setIgnoreNull(ignoreNull);
                return this;
            }

            /**
             * When true, a XlBean (or Map) instance without any key will be treated as
             * null. For instance, if a name of a field is "test.test" and value for this
             * field is null, then the result will be as follows:
             * <ul>
             * <li>ignoreBlankMap == true && ignoreNull == true: {}</li>
             * <li>ignoreBlankMap == false && ignoreNull == true: {"test":{}}</li>
             * </ul>
             * Using ignoreNull == true case because if ignoreNull == false, then the map
             * will become {"test":{"test": null}} which is not empty anyway.
             * 
             * @param ignoreBlankMap
             * @return
             */
            public AccessorConfigBuilder ignoreBlankMap(boolean ignoreBlankMap) {
                config.setIgnoreBlankMap(ignoreBlankMap);
                return this;
            }

            /**
             * When true, a List instance whose size is 0 will be treated as null. For
             * instance, if a name of field is "testList" and value for this field is null,
             * then the result will be as follows:
             * <ul>
             * <li>ignoreBlankList == true && ignoreNull == true: {}</li>
             * <li>ignoreBlankList == false && ignoreNull == true: {"testList":[]}</li>
             * </ul>
             * Using ignoreNull == true case because if ignoreNull == false, then the field
             * will become {"testList":[null]} which is not empty anyway.
             * 
             * @param ignoreBlankList
             * @return
             */
            public AccessorConfigBuilder ignoreBlankList(boolean ignoreBlankList) {
                config.setIgnoreBlankList(ignoreBlankList);
                return this;
            }

            public AccessorConfig build() {
                return config;
            }
        }

        @Override
        public String toString() {
            return "AccessorConfig [ignoreNull=" + ignoreNull + ", ignoreBlankMap=" + ignoreBlankMap
                    + ", ignoreBlankList=" + ignoreBlankList + "]";
        }
    }

}
