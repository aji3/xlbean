package org.xlbean.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;

public class TableDefinitionCacheAccessor {

  private TableDefinition definition;

  public TableDefinitionCacheAccessor(TableDefinition definition) {
    this.definition = definition;
  }

  private Map<String, Integer> cache = new HashMap<>();

  protected int getOffset() {
    Integer offset = cache.get("offset");
    if (offset == null) {
      try {
        offset = Integer.parseInt(definition.getOptions().get("offset"));
      } catch (NumberFormatException e) {
        offset = 0;
      }
      cache.put("offset", offset);
    }
    return cache.get("offset");
  }

  public int getLimit() {
    Integer limit = cache.get("limit");
    if (limit == null) {
      try {
        limit = Integer.parseInt(definition.getOptions().get("limit"));
      } catch (NumberFormatException e) {
        limit = Integer.MAX_VALUE;
      }
      cache.put("limit", limit);
    }
    return limit;
  }

  private List<SingleDefinition> columnsCache = null;

  public List<SingleDefinition> getColumns() {
    if (columnsCache == null) {
      if (definition.isDirectionDown()) {
        columnsCache =
            definition
                .getAttributes()
                .values()
                .stream()
                .filter(elem -> elem.getCell().getColumn() != null)
                .collect(Collectors.toList());
      } else {
        columnsCache =
            definition
                .getAttributes()
                .values()
                .stream()
                .filter(elem -> elem.getCell().getRow() != null)
                .collect(Collectors.toList());
      }
    }
    return columnsCache;
  }

  private Map<String, List<String>> indexKeysMap = null;

  public Map<String, List<String>> getIndexKeysMap() {
    if (indexKeysMap == null) {
      indexKeysMap = new HashMap<>();
      for (SingleDefinition attr : definition.getAttributes().values()) {
        String indexName = attr.getOptions().get("index");
        if (indexName != null) {
          List<String> list = indexKeysMap.get(indexName);
          if (list == null) {
            list = new ArrayList<>();
            indexKeysMap.put(indexName, list);
          }
          list.add(attr.getName());
        }
      }
    }
    return indexKeysMap;
  }
}
