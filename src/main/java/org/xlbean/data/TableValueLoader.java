package org.xlbean.data;

import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.util.XlBeanFactory;

/**
 * Loader for table.
 *
 * @author Kazuya Tanikawa
 */
public class TableValueLoader extends ValueLoader<TableDefinition> {

  private static Logger log = LoggerFactory.getLogger(TableValueLoader.class);

  private TableDefinitionCacheAccessor definitionCache;

  public TableValueLoader(TableDefinition definition) {
    super(definition);
    definitionCache = new TableDefinitionCacheAccessor(definition);
  }

  @Override
  public void load(XlBean bean) {
    TableDefinition definition = getDefinition();
    XlList table = bean.list(definition.getName());
    if (table == null) {
      table = XlBeanFactory.getInstance().createList();
      for (Entry<String, List<String>> entry : definitionCache.getIndexKeysMap().entrySet()) {
        table.addIndex(entry.getKey(), entry.getValue());
      }
      convertDottedStringToBean(definition.getName(), table, bean);
    }
    List<SingleDefinition> columns = definitionCache.getColumns();
    int index = 0;
    while (true) {
      XlBean dataRow = XlBeanFactory.getInstance().createBean();
      for (SingleDefinition attribute : columns) {
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
        convertDottedStringToBean(attribute.getName(), value, dataRow);
      }
      if (log.isTraceEnabled()) {
        log.trace(dataRow.toString());
      }
      if (checkTerminate(table, dataRow, index)) {
        break;
      } else {
        if (definitionCache.getOffset() == 0) {
          table.add(dataRow);
        } else {
          table.setFillNull(index + definitionCache.getOffset(), dataRow);
        }
      }
      index++;
    }
  }

  protected boolean checkTerminate(XlList data, XlBean dataRow, int num) {
    int limit = definitionCache.getLimit();
    if (limit <= num) {
      return true;
    }
    return dataRow.isValuesEmpty();
  }
}
