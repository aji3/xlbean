package org.xlbean.definition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.xlbean.excel.XlCellAddress;

public abstract class DefinitionResolver {

    public abstract boolean isResolvable(String value);

    /**
     * Parse key string and create a Definition object with cell information. Then
     * register the Definition object to context and return.
     *
     * @param context
     * @param key
     * @param cell
     * @return
     */
    public abstract Definition resolve(String key, XlCellAddress cell);

    protected String parseName(String tableNameOrColumnName) {
        if (tableNameOrColumnName.indexOf('?') > 0) {
            return tableNameOrColumnName.substring(0, tableNameOrColumnName.indexOf('?'));
        } else {
            return tableNameOrColumnName;
        }
    }

    protected Map<String, String> parseOptions(String tableNameOrColumnName) {
        Map<String, String> retMap = new HashMap<>();

        if (tableNameOrColumnName.indexOf('?') > 0) {
            String optionString = tableNameOrColumnName.substring(
                tableNameOrColumnName.indexOf('?') + 1,
                tableNameOrColumnName.length());
            Arrays
                .stream(optionString.split("&"))
                .map(str -> str.trim().split("="))
                .forEach(keyValueArray -> retMap.put(keyValueArray[0], keyValueArray[1]));
        }
        return retMap;
    }
}
