package org.xlbean.data.value;

import org.xlbean.XlBean;
import org.xlbean.definition.Definition;

public abstract class ValueLoader<T extends Definition> {

    /**
     * Populate {@code bean} with {@code definition}.
     *
     * @param bean
     */
    public abstract void load(XlBean bean);

    private T definition;

    private ReadAsOptionProcessor readAsOptionProcessor = new ReadAsOptionProcessor();
    private ConverterOptionProcessor converterOptionProcessor = new ConverterOptionProcessor();

    @SuppressWarnings("unchecked")
    public ValueLoader(Definition definition) {
        setDefinition((T) definition);
    }

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }

    protected Object getValue(Definition definition, int row, int column) {
        String value = readValue(definition, row, column);
        return processConverterOption(value, definition);
    }

    protected String readValue(Definition definition, int row, int column) {
        String value = null;
        if (readAsOptionProcessor.hasOption(definition)) {
            value = definition.getSheet().getCellValue(row, column, readAsOptionProcessor.getOption(definition));
        } else {
            value = definition.getSheet().getCellValue(row, column);
        }
        return value;
    }

    protected Object processConverterOption(String value, Definition definition) {
        if (converterOptionProcessor.hasConverterOption(definition)) {
            return converterOptionProcessor.toObject(value, definition);
        } else {
            return value;
        }
    }

}
