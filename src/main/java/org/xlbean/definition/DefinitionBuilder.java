package org.xlbean.definition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.xlbean.definition.parser.DefinitionOption;
import org.xlbean.excel.XlCellAddress;

public abstract class DefinitionBuilder {

    public abstract boolean isBuildable(Object parsedObject);

    /**
     * Parse key string and create a Definition object with cell information. Then
     * register the Definition object to context and return.
     *
     * @param context
     * @param key
     * @param cell
     * @return
     */
    public abstract Definition build(Object parsedDefinition, XlCellAddress cell);

    protected Map<String, String> convertParsedOptionsToMap(List<DefinitionOption> definitionUnitList) {
        return definitionUnitList
            .stream()
            .collect(Collectors.toMap(DefinitionOption::getName, DefinitionOption::getValue));
    }
}
