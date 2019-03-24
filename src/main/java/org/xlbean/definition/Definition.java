package org.xlbean.definition;

import org.xlbean.excel.XlSheet;

/**
 * Super class for all definitions.
 *
 * @see SingleDefinition
 * @see TableDefinition
 * @author Kazuya Tanikawa
 */
public abstract class Definition {

    private String name;
    private Options options = new Options();
    private String sheetName;
    private XlSheet sheet;

    public abstract boolean validate();

    /**
     * Merge given {@link Definition} object with this object.
     *
     * <p>
     * If given object cannot be merged, it returns without doing anything nor any
     * error.
     *
     * @param newDefinition
     */
    public abstract void merge(Definition newDefinition);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinitionId() {
        return sheetName + "_" + name;
    }

    public Options getOptions() {
        return options;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public XlSheet getSheet() {
        return sheet;
    }

    public void setSheet(XlSheet sheet) {
        this.sheet = sheet;
    }

}
