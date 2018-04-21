package org.xlbean.data.value.single;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.data.value.ValueLoader;
import org.xlbean.definition.Definition;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.util.FieldAccessHelper;

/**
 * Value loader to load a value of a cell.
 *
 * @author Kazuya Tanikawa
 */
public class SingleValueLoader extends ValueLoader<SingleDefinition> {

    private static Logger log = LoggerFactory.getLogger(SingleValueLoader.class);

    public SingleValueLoader(Definition definition) {
        super(definition);
    }

    @Override
    public void load(XlBean bean) {
        SingleDefinition definition = getDefinition();
        String value = getValue(definition, definition.getCell().getRow(), definition.getCell().getColumn());
        if (log.isTraceEnabled()) {
            log.trace(value.toString());
        }
        FieldAccessHelper.setValue(getDefinition().getName(), value, bean);
    }
}
