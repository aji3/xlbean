package org.xlbean.data.value.table;

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
        Integer offset = cache.get(TableValueLoader.OPTION_OFFSET);
        if (offset == null) {
            try {
                offset = Integer.parseInt(definition.getOptions().get(TableValueLoader.OPTION_OFFSET));
            } catch (NumberFormatException e) {
                offset = 0;
            }
            cache.put(TableValueLoader.OPTION_OFFSET, offset);
        }
        return cache.get(TableValueLoader.OPTION_OFFSET);
    }

    public int getLimit() {
        Integer limit = cache.get(TableValueLoader.OPTION_LIMIT);
        if (limit == null) {
            try {
                limit = Integer.parseInt(definition.getOptions().get(TableValueLoader.OPTION_LIMIT));
            } catch (NumberFormatException e) {
                limit = Integer.MAX_VALUE;
            }
            cache.put(TableValueLoader.OPTION_LIMIT, limit);
        }
        return limit;
    }

    private List<SingleDefinition> columnsCache = null;

    public List<SingleDefinition> getColumns() {
        if (columnsCache == null) {
            if (definition.isDirectionDown()) {
                columnsCache = definition
                    .getAttributes()
                    .values()
                    .stream()
                    .filter(elem -> elem.getCell().getColumn() != null)
                    .collect(Collectors.toList());
            } else {
                columnsCache = definition
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

    private boolean isKeyValueOptionInitialized = false;
    private SingleDefinition toBeanKeyOptionDefinition;
    private SingleDefinition toBeanValueOptionDefinition;

    public boolean hasToBeanOption() {
        if (!isKeyValueOptionInitialized) {
            for (SingleDefinition attr : definition.getAttributes().values()) {
                if (TableValueLoader.OPTION_TOBEAN_KEY.equals(
                    attr.getOptions().get(TableValueLoader.OPTION_TOBEAN))) {
                    toBeanKeyOptionDefinition = attr;
                } else if (TableValueLoader.OPTION_TOBEAN_VALUE.equals(
                    attr.getOptions().get(TableValueLoader.OPTION_TOBEAN))) {
                    toBeanValueOptionDefinition = attr;
                }
            }
            isKeyValueOptionInitialized = true;
        }
        return toBeanKeyOptionDefinition != null && toBeanValueOptionDefinition != null;
    }

    public SingleDefinition getToBeanKeyOptionDefinition() {
        return toBeanKeyOptionDefinition;
    }

    public SingleDefinition getToBeanValueOptionDefinition() {
        return toBeanValueOptionDefinition;
    }
}
