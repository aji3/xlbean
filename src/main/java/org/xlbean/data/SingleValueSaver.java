package org.xlbean.data;

import org.xlbean.XlBean;
import org.xlbean.definition.SingleDefinition;

public class SingleValueSaver extends ValueSaver<SingleDefinition> {

    public SingleValueSaver(SingleDefinition definition) {
        super(definition);
    }

    @Override
    public void save(XlBean bean) {
        String value = getValueFromXlBean(getDefinition().getName(), bean);
        setValue(
            getDefinition(),
            getDefinition().getCell().getRow(),
            getDefinition().getCell().getColumn(),
            value);
    }
}
