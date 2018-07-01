package org.xlbean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.xlbean.util.XlBeanFactory;

/**
 * Inherits {@link ArrayList} with additional utility methods suitable for
 * common data structure of excel.
 *
 * <p>
 * All the tables defined in excel is represented as {@link XlList} of
 * {@link XlBean} instances.
 *
 * @author Kazuya Tanikawa
 */
@SuppressWarnings("serial")
public class XlList extends ArrayList<XlBean> {

    /**
     * HashMap used for searching elements of this list by hashed keys.
     * 
     * <p>
     * This field will be used when "index" option is defined.
     * </p>
     */
    private Map<String, Index> indexMap = new HashMap<>();

    /**
     * Adds index of {@code indexName} with {@code indexKeys} to this object.
     *
     * @param indexName
     * @param indexKeys
     */
    public void addIndex(String indexName, List<String> indexKeys) {
        indexMap.put(indexName, new Index(indexKeys));
    }

    /**
     * Adds {@code element} to the list after organizing index.
     *
     * @return
     */
    @Override
    public boolean add(XlBean element) {
        if (!indexMap.isEmpty()) {
            indexMap.values().forEach(idx -> idx.addData(element));
        }
        return super.add(element);
    }

    /**
     * Convert a denormalized list of one to many relation objects into parent
     * object with list of children objects.
     *
     * <p>
     * Suppose you have a table of companies and employees in a excel sheet. In each
     * row of the table, there are a name of an employee and a name of a company
     * which the employee belongs to. When this table is loaded by XlBean, you will
     * get an instance of {@link XlList} filled with multiple instances of
     * {@link XlBean} as elements, and each of the XlBean instance has name of an
     * employeeName and a companyName.
     *
     * <p>
     * Now you want to have a list of companies which has list of employees inside
     * each instance of company. This can be done by calling this method with
     * "employees" as childListName and ["companyName"] as parentKeys.
     *
     * @param childListName
     * @param parentKeys
     * @return
     */
    public XlList aggregate(String childListName, String... parentKeys) {
        XlList retList = createInstance();
        aggregateIndex(retList, parentKeys);
        Map<String, XlBean> cache = new HashMap<>();
        for (XlBean elem : this) {
            String key = generateAggregateKey(elem, parentKeys);
            XlBean aggregatedMap = cache.get(key);
            if (aggregatedMap == null) {
                aggregatedMap = XlBeanFactory.getInstance().createBean();
                cache.put(key, aggregatedMap);
                retList.add(aggregatedMap);
            }
            XlList aggregatedList = null;
            Object aggregatedListObject = aggregatedMap.get(childListName);
            if (aggregatedListObject == null || !(aggregatedListObject instanceof XlList)) {
                aggregatedList = createInstance();
                aggregatedMap.put(childListName, aggregatedList);
                final XlBean x = aggregatedMap;
                Arrays.stream(parentKeys).forEach(k -> x.put(k, elem.get(k)));
            } else {
                aggregatedList = (XlList) aggregatedListObject;
            }
            aggregatedList.add(elem);
        }

        return retList;
    }

    /**
     * Convert this list to {@link XlBean}.
     *
     * <p>
     * Key of {@link XlBean} is a string created by concatenating string retrieved
     * from beans inside this list by {@code keys}. Delimiter for the key is "#_#".
     *
     * @param keys
     * @return
     */
    public XlBean toMap(String... keys) {
        XlBean retBean = XlBeanFactory.getInstance().createBean();
        stream().forEach(bean -> retBean.put(generateAggregateKey(bean, keys), bean));
        return retBean;
    }

    /**
     * Remove {@code keys} from {@link Index} and add to the {@code newList}.
     *
     * @param newList
     * @param keys
     */
    private void aggregateIndex(XlList newList, String... keys) {
        indexMap.forEach(
            (indexName, index) -> newList.addIndex(
                indexName,
                index
                    .getKeys()
                    .stream()
                    .filter(key -> Arrays.binarySearch(keys, key) >= 0)
                    .collect(Collectors.toList())));
    }

    /**
     * Generates a key used for aggregate.
     *
     * @param elem
     * @param keys
     * @return
     */
    protected String generateAggregateKey(XlBean elem, String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : Arrays.asList(keys).stream().sorted().collect(Collectors.toList())) {
            if (sb.length() > 0) {
                sb.append("#_#");
            }
            sb.append(elem.get(key));
        }
        return sb.toString();
    }

    /**
     * Filters this list with {@code conditionMap} and returns a new list.
     *
     * <p>
     * All the keys and values in the {@code conditionMap} is used to check if
     * elements in this list matches with it.
     *
     * @param conditionMap
     * @return
     */
    public XlList findAll(Map<String, String> conditionMap) {
        XlList list = createInstance();
        if (conditionMap == null) {
            return list;
        } else {
            stream().filter(elem -> matches(elem, conditionMap)).forEach(list::add);
        }
        return list;
    }

    /**
     * Returns element registered at {@code index}.
     *
     * <p>
     * If the index is out of bounds, then it returns null.
     *
     * @param index
     * @return
     */
    public XlBean find(int index) {
        if (size() > index) {
            return get(index);
        }
        return null;
    }

    /**
     * Returns the first element matches {@code conditionMap}.
     *
     * <p>
     * All the keys and values in the {@code conditionMap} is used to check if
     * elements in this list matches with it.
     *
     * <p>
     * The matching starts from index 0 to the end of this list one by one until
     * matched element. If there is no element to be matched, then it returns null.
     *
     * @param conditionMap
     * @return
     */
    public XlBean find(Map<String, String> conditionMap) {
        for (XlBean data : this) {
            if (matches(data, conditionMap)) {
                return data;
            }
        }
        return null;
    }

    /**
     * Returns an instance of {@link XlBean} which is registered to this list by an
     * index which value is specified by the {@code conditionMap}.
     *
     * <p>
     * This method is available only when one index is set to table. Otherwise it
     * always returns null.
     *
     * @param conditionMap
     * @return
     */
    public XlBean findByIndex(Map<String, String> conditionMap) {
        return findByIndex(conditionMap, null);
    }

    public XlBean findByIndex(Map<String, String> conditionMap, String indexName) {
        Index index = null;
        if (indexName == null) {
            if (indexMap.size() == 1) {
                index = indexMap.values().iterator().next();
            } else {
                return null;
            }
        } else {
            index = indexMap.get(indexName);
        }
        if (index == null) {
            return null;
        } else {
            return index.getData(conditionMap);
        }
    }

    private boolean matches(XlBean row, Map<String, String> conditionMap) {
        boolean matched = true;
        for (Entry<String, String> entry : conditionMap.entrySet()) {
            if (!entry.getValue().equals(row.get(entry.getKey()))) {
                matched = false;
                break;
            }
        }
        return matched;
    }

    /**
     * Private class for index of list.
     *
     * @author Kazuya Tanikawa
     */
    private static class Index {
        private static final String KEY_DELIMITER = "#_#";
        private List<String> keys;
        private Map<String, XlBean> indexData = new HashMap<>();

        public Index(List<String> keys) {
            this.keys = keys.stream().sorted().collect(Collectors.toList());
        }

        public void addData(XlBean data) {
            indexData.put(createKey(data), data);
        }

        public XlBean getData(Map<String, ?> conditionMap) {
            return indexData.get(createKey(conditionMap));
        }

        private String createKey(Map<String, ?> conditionMap) {
            if (keys.isEmpty()) {
                return String.valueOf(conditionMap.hashCode());
            }
            final StringBuilder sb = new StringBuilder();
            keys.forEach(key -> sb.append(conditionMap.get(key)).append(KEY_DELIMITER));
            return sb.toString();
        }

        public List<String> getKeys() {
            return keys;
        }
    }

    protected XlList createInstance() {
        return new XlList();
    }
}
