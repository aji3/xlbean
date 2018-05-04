package org.xlbean.data.value;

import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.excel.XlSheet.ValueType;

public abstract class ValueSaver<T extends Definition> {

    public abstract void save(XlBean bean);

    private T definition;

    public ValueSaver(T definition) {
        this.definition = (T) definition;
    }

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }

    protected void setValue(Definition definition, int row, int column, String value) {
        String type = definition.getOptions().get("type");
        if (type == null) {
            definition.getSheet().setCellValue(row, column, value);
        } else {
            definition.getSheet().setCellValue(row, column, value, ValueType.valueOf(type));
        }
    }
}