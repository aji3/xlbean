package org.xlbean.data.value;

import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.excel.XlSheet.ValueType;

public abstract class ValueLoader<T extends Definition> {

    /**
     * Populate {@code bean} with {@code definition}.
     *
     * @param bean
     */
    public abstract void load(XlBean bean);

    private T definition;

    @SuppressWarnings("unchecked")
    public ValueLoader(Definition definition) {
        this.definition = (T) definition;
    }

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }

    protected String getValue(Definition definition, int row, int column) {
        String type = definition.getOptions().get("type");
        if (type == null) {
            return definition.getSheet().getCellValue(row, column);
        } else {
            return definition.getSheet().getCellValue(row, column, ValueType.valueOf(type));
        }
    }

}
