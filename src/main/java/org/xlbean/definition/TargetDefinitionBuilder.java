package org.xlbean.definition;

import org.xlbean.definition.parser.TargetDefinitionUnit;
import org.xlbean.excel.XlCellAddress;

public class TargetDefinitionBuilder extends DefinitionBuilder {

    @Override
    public boolean isBuildable(Object parsedDefinition) {
        if (parsedDefinition == null) {
            return false;
        }
        return parsedDefinition instanceof TargetDefinitionUnit;
    }

    @Override
    public Definition build(Object parsedDefinition, XlCellAddress cell) {
        TargetDefinitionUnit definitionPair = (TargetDefinitionUnit) parsedDefinition;

        TargetDefinition definition = new TargetDefinition();
        definition.getOptions().addOptions(convertParsedOptionsToMap(definitionPair.getOptions()));

        return definition;
    }

}
