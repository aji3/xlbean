package org.xlbean.data.value.single;

import org.xlbean.XlBean;
import org.xlbean.data.value.ValueSaver;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.util.FieldAccessHelper;

public class SingleValueSaver extends ValueSaver<SingleDefinition> {

    public SingleValueSaver(SingleDefinition definition) {
        super(definition);
    }

    @Override
    public void save(XlBean bean) {
        String value = FieldAccessHelper.getValue(getDefinition().getName(), bean);
        setValue(
            getDefinition(),
            getDefinition().getCell().getRow(),
            getDefinition().getCell().getColumn(),
            value);
    }
}
