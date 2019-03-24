package org.xlbean.definition;

import org.xlbean.definition.parser.DefinitionPair;
import org.xlbean.definition.parser.DefinitionUnit;
import org.xlbean.excel.XlCellAddress;

public class TableDefinitionBuilder extends DefinitionBuilder {

    @Override
    public boolean isBuildable(Object parsedDefinition) {
        if (parsedDefinition == null) {
            return false;
        }
        return parsedDefinition instanceof DefinitionPair;
    }

    @Override
    public Definition build(Object parsedDefinition, XlCellAddress cell) {
        DefinitionPair definitionPair = (DefinitionPair) parsedDefinition;

        // Table
        DefinitionUnit table = definitionPair.getLeft();
        TableDefinition definition = new TableDefinition();
        definition.setName(table.getName());
        definition.getOptions().setOptions(toMap(table.getOptions()));

        // Column
        DefinitionUnit column = definitionPair.getRight();
        SingleDefinition columnDefinition = new SingleDefinition();
        columnDefinition.setName(column.getName());
        columnDefinition.getOptions().setOptions(toMap(column.getOptions()));
        columnDefinition.setCell(cell);

        // Set column to table
        definition.addAttribute(columnDefinition);

        return definition;
    }

}
