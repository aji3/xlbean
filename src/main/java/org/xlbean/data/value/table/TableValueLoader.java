package org.xlbean.data.value.table;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.data.value.ValueLoader;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.util.Accessors;
import org.xlbean.util.BeanHelper;
import org.xlbean.util.XlBeanFactory;

/**
 * Loader for table.
 *
 * @author Kazuya Tanikawa
 */
public class TableValueLoader extends ValueLoader<TableDefinition> {

    public static final String OPTION_OFFSET = "offset";
    public static final String OPTION_LIMIT = "limit";
    public static final String OPTION_TOBEAN = "toBean";
    public static final String OPTION_TOBEAN_KEY = "key";
    public static final String OPTION_TOBEAN_VALUE = "value";

    private static Logger log = LoggerFactory.getLogger(TableValueLoader.class);

    private TableDefinitionCacheAccessor definitionCache;

    public TableValueLoader(TableDefinition definition) {
        super(definition);
        definitionCache = new TableDefinitionCacheAccessor(definition);
    }

    @Override
    public void load(XlBean bean) {
        TableDefinition definition = getDefinition();
        List<XlBean> table = bean.beans(definition.getName());
        if (table == null) {
            table = XlBeanFactory.getInstance().createList();
            for (Entry<String, List<String>> entry : definitionCache.getIndexKeysMap().entrySet()) {
                ((XlList) table).addIndex(entry.getKey(), entry.getValue());
            }
        }
        ToBeanOptionProcessor toBeanOptionProcessor = new ToBeanOptionProcessor(definition, bean);

        int index = 0;
        // Loop over lines of table in excel sheet
        while (true) {
            // Create XlBean instance
            XlBean dataRow = XlBeanFactory.getInstance().createBean();

            // Iterate definitions to read out value of cells in the row
            for (SingleDefinition attribute : definitionCache.getColumns()) {
                String value = null;
                if (definition.isDirectionDown()) {
                    value = getValue(
                        attribute,
                        definition.getStartCell().getRow() + index,
                        attribute.getCell().getColumn());
                } else {
                    value = getValue(
                        attribute,
                        attribute.getCell().getRow(),
                        definition.getStartCell().getColumn() + index);
                }
                Accessors.setValue(attribute.getName(), value, dataRow);
            }
            if (log.isTraceEnabled()) {
                log.trace(dataRow.toString());
            }

            // Check terminate condition
            // if it does not fulfill the condition, add created bean to table instance.
            // if it fulfill the condition, throw away dataRow and break from the loop.
            if (checkTerminate(table, dataRow, index)) {
                Accessors.setValue(definition.getName(), table, bean);
                break;
            }

            if (definitionCache.getOffset() == 0) {
                table.add(dataRow);
            } else {
                BeanHelper.setFillNull(table, index + definitionCache.getOffset(), dataRow);
            }
            index++;
            toBeanOptionProcessor.process(dataRow);
        }

    }

    protected boolean checkTerminate(List<XlBean> data, XlBean dataRow, int num) {
        int limit = definitionCache.getLimit();
        if (limit <= num) {
            return true;
        }
        return BeanHelper.isValuesEmpty(dataRow);
    }

    /**
     * When a list has "toBean" option, then the list will be converted to key and
     * value of the bean where the list belongs to. For instance, if a list is
     * something like "someList", then columns specified as "toBean=key" and
     * "toBean=value" will become key-values of ROOT XlBean. If a list is
     * "obj.anotherList", then the key-values will be added to "obj".
     * 
     * @author tanikawa
     *
     */
    public static class ToBeanOptionProcessor {

        private Map<String, Object> rootBean;
        private String key;
        private String value;
        private String targetBeanName;
        private Map<String, Object> targetBean;

        public ToBeanOptionProcessor(TableDefinition definition, Map<String, Object> rootBean) {
            initialize(definition, rootBean);
        }

        private void initialize(TableDefinition definition, Map<String, Object> rootBean) {
            for (SingleDefinition attr : definition.getAttributes().values()) {
                if (TableValueLoader.OPTION_TOBEAN_KEY.equals(
                    attr.getOptions().get(TableValueLoader.OPTION_TOBEAN))) {
                    key = attr.getName();
                } else if (TableValueLoader.OPTION_TOBEAN_VALUE.equals(
                    attr.getOptions().get(TableValueLoader.OPTION_TOBEAN))) {
                    value = attr.getName();
                }
            }
            String tableName = definition.getName();
            if (tableName.contains(".")) {
                targetBeanName = tableName.substring(0, tableName.lastIndexOf('.'));
                targetBean = Accessors.getValue(targetBeanName, rootBean);
            } else {
                targetBean = rootBean;
            }
            this.rootBean = rootBean;
        }

        /**
         * If a list has "toBean" option, then the list will be converted to key and
         * value of the bean where the list belongs to. If a list is something like
         * "someList", then columns specified as "toBean=key" and "toBean=value" will
         * become key-values of root XlBean. If a list is "obj.anotherList", then the
         * key-values will be added to "obj".
         * 
         * @param rootBean
         * @param row
         */
        public void process(XlBean row) {
            if (key == null || value == null) {
                return;
            }
            String keyObj = Accessors.getValue(key, row);
            if (keyObj == null) {
                return;
            }
            String valueObj = Accessors.getValue(value, row);
            if (targetBean == null) {
                targetBean = XlBeanFactory.getInstance().createBean();
                Accessors.setValue(keyObj, valueObj, targetBean);
                // It must be this order because if Accessors#noNullValue = true, then it blank
                // map cannot be set.
                Accessors.setValue(targetBeanName, targetBean, rootBean);
            } else {
                Accessors.setValue(keyObj, valueObj, targetBean);
            }
        }
    }
}
