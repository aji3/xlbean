package org.xlbean.definition;

import org.xlbean.definition.parser.DefinitionUnit;
import org.xlbean.excel.XlCellAddress;

public class SingleDefinitionBuilder extends DefinitionBuilder {

    @Override
    public boolean isBuildable(Object parsedObject) {
        if (parsedObject == null) {
            return false;
        }
        return parsedObject instanceof DefinitionUnit;
    }

    @Override
    public Definition build(Object parsedObject, XlCellAddress cell) {
        DefinitionUnit unit = (DefinitionUnit) parsedObject;
        SingleDefinition definition = new SingleDefinition();
        definition.setName(unit.getName());
        definition.getOptions().addOptions(convertParsedOptionsToMap(unit.getOptions()));
        definition.setCell(cell);

        return definition;
    }
}
