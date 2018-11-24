package org.xlbean.definition;

import org.xlbean.definition.parser.InTableOptionUnit;
import org.xlbean.excel.XlCellAddress;

public class InTableOptionDefinitionBuilder extends DefinitionBuilder {

    @Override
    public boolean isBuildable(Object parsedObject) {
        if (parsedObject == null) {
            return false;
        }
        return parsedObject instanceof InTableOptionUnit;
    }

    @Override
    public Definition build(Object parsedObject, XlCellAddress cell) {
        InTableOptionUnit unit = (InTableOptionUnit) parsedObject;
        InTableOptionDefinition definition = new InTableOptionDefinition();
        definition.setName(unit.getName());
        definition.addOption(unit.getOptionKey(), cell);

        return definition;
    }

}
