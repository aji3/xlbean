package org.xlbean.definition;

import java.util.HashMap;
import java.util.Map;

import org.xlbean.excel.XlSheet;

/**
 * Super class for all definitions.
 *
 * @author Kazuya Tanikawa
 */
public abstract class Definition {

    private String name;
    private Map<String, String> options = new HashMap<>();
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

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
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

    public void addOptions(Map<String, String> options) {
        getOptions().putAll(options);
    }

}
