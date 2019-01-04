package org.xlbean.data.value.table;

import java.util.List;
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
            } else {
                if (definitionCache.getOffset() == 0) {
                    table.add(dataRow);
                } else {
                    BeanHelper.setFillNull(table, index + definitionCache.getOffset(), dataRow);
                }
            }
            index++;
        }

        processToBeanOption(bean);
    }

    private void processToBeanOption(XlBean rootBean) {
        if (!definitionCache.hasToBeanOption()) {
            return;
        }
        String tableName = getDefinition().getName();
        List<XlBean> table = Accessors.getValue(tableName, rootBean);
        if (table == null) {
            return;
        }
        SingleDefinition key = definitionCache.getToBeanKeyOptionDefinition();
        SingleDefinition value = definitionCache.getToBeanValueOptionDefinition();
        XlBean targetBean = rootBean;
        if (tableName.contains(".")) {
            targetBean = Accessors.getValue(tableName.substring(0, tableName.lastIndexOf('.')), rootBean);
        }
        for (XlBean row : table) {
            String keyObj = Accessors.getValue(key.getName(), row);
            String valueObj = Accessors.getValue(value.getName(), row);
            Accessors.setValue(keyObj, valueObj, targetBean);
        }
    }

    protected boolean checkTerminate(List<XlBean> data, XlBean dataRow, int num) {
        int limit = definitionCache.getLimit();
        if (limit <= num) {
            return true;
        }
        return BeanHelper.isValuesEmpty(dataRow);
    }
}
