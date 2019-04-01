package org.xlbean.data.value;

import org.xlbean.XlBean;
import org.xlbean.definition.Definition;

public abstract class ValueSaver<T extends Definition> {

    public abstract void save(XlBean bean);

    private T definition;

    private ReadAsOptionProcessor readAsOptionProcessor = new ReadAsOptionProcessor();
    private FieldTypeOptionProcessor converterOptionProcessor = new FieldTypeOptionProcessor();

    public ValueSaver(T definition) {
        this.definition = (T) definition;
    }

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }

    protected void setValue(Definition definition, int row, int column, Object obj) {
        String value = processConverterOption(obj, definition);
        if (readAsOptionProcessor.hasOption(definition)) {
            definition.getSheet().setCellValue(row, column, value, readAsOptionProcessor.getOption(definition));
        } else {
            definition.getSheet().setCellValue(row, column, value);
        }
    }

    protected String processConverterOption(Object obj, Definition definition) {
        if (obj == null) {
            return null;
        }
        if (converterOptionProcessor.hasConverterOption(definition)) {
            return converterOptionProcessor.toString(obj, definition);
        } else {
            return obj.toString();
        }

    }
}
