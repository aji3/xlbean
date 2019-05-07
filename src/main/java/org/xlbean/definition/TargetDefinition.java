package org.xlbean.definition;

import org.xlbean.definition.parser.DefinitionConstants;

public class TargetDefinition extends Definition {

    public TargetDefinition() {
        setName(DefinitionConstants.TARGET_SHEET_MARK);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void merge(Definition newDefinition) {
        getOptions().merge(newDefinition.getOptions());
    }

}
