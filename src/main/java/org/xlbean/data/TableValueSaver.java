package org.xlbean.data;

import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;

public class TableValueSaver extends ValueSaver<TableDefinition>{

	private TableDefinitionCacheAccessor definitionCache;
	public TableValueSaver(TableDefinition definition) {
		super(definition);
		definitionCache = new TableDefinitionCacheAccessor(definition);
	}

	@Override
	public void save(XlBean bean) {
		TableDefinition definition = getDefinition();
		XlList table = getValueFromXlBean(definition.getName(), bean);

		List<SingleDefinition> columns = definitionCache.getColumns();
		int index = 0;
		for (XlBean row : table) {
			for (SingleDefinition attribute : columns) {
				String value = getValueFromXlBean(attribute.getName(), row);
				if (definition.isDirectionDown()) {
					setValue(attribute, definition.getStartCell().getRow() + index, attribute.getCell().getColumn(), value);
				} else {
					setValue(attribute, attribute.getCell().getRow(), definition.getStartCell().getColumn() + index, value);
				}
			}
			index++;
		}
	}

}
