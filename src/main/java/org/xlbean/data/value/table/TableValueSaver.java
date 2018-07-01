package org.xlbean.data.value.table;

import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.data.value.ValueSaver;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.util.FieldAccessHelper;

public class TableValueSaver extends ValueSaver<TableDefinition> {

    private TableDefinitionCacheAccessor definitionCache;

    public TableValueSaver(TableDefinition definition) {
        super(definition);
        definitionCache = new TableDefinitionCacheAccessor(definition);
    }

    @Override
    public void save(XlBean bean) {
        TableDefinition definition = getDefinition();
        List<XlBean> table = FieldAccessHelper.getValue(definition.getName(), bean);

        List<SingleDefinition> columns = definitionCache.getColumns();
        int index = 0;
        for (XlBean row : table) {
            for (SingleDefinition attribute : columns) {
                String value = FieldAccessHelper.getValue(attribute.getName(), row);
                if (definition.isDirectionDown()) {
                    setValue(
                        attribute,
                        definition.getStartCell().getRow() + index,
                        attribute.getCell().getColumn(),
                        value);
                } else {
                    setValue(
                        attribute,
                        attribute.getCell().getRow(),
                        definition.getStartCell().getColumn() + index,
                        value);
                }
            }
            index++;
        }
    }
}
