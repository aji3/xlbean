package org.xlbean.definition;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlSheet;

/**
 * Definition for table.
 *
 * <p>
 * To represent a structure of table, which is a set of pre-defined columns and
 * any number of rows, this class has a {@code attributes} property as a Map of
 * {@link SingleDefinition}s. For a column, name of the column and column number
 * is set to {@link SingleDefinition} object and set to {@code attributes}. For
 * rows, only the start row number is set to {@link SingleDefinition} object and
 * set to {@code attributes}.
 *
 * @author Kazuya Tanikawa
 */
public class TableDefinition extends Definition {

    public static final String START_MARK = "~";

    private static Logger log = LoggerFactory.getLogger(TableDefinition.class);

    private Map<String, SingleDefinition> attributes = new HashMap<>();

    /**
     * Returns true if the direction of the table is down.
     *
     * <p>
     * It returns false, which means direction is "down", when this definition meets
     * either of the following conditions.
     *
     * <ul>
     * <li>Has a option which name is "direction" and value is "right"
     * <li>"~" cell is defined at the top row.
     * </ul>
     *
     * @return
     */
    public boolean isDirectionDown() {
        String direction = getOptions().get("direction");
        if (direction == null) {
            return getStartCell().getRow() != null;
        } else {
            return !"right".equals(direction);
        }
    }

    /**
     * Returns {@code attributes} which is a map to hold all the columns and start
     * cell information.
     *
     * @return
     */
    public Map<String, SingleDefinition> getAttributes() {
        return attributes;
    }

    /**
     * Add given {@code attributeDefinition} to this table definition.
     *
     * @param attributeDefinition
     */
    public void addAttribute(SingleDefinition attributeDefinition) {
        attributes.put(attributeDefinition.getName(), attributeDefinition);
    }

    /**
     * Returns instance of {@link XlCellAddress} of start cell.
     *
     * @return
     */
    public XlCellAddress getStartCell() {
        if (attributes.get(START_MARK) == null) {
            return null;
        }
        return attributes.get(START_MARK).getCell();
    }

    /**
     * Merges given {@link TableDefinition} instance with this instance.
     *
     * <p>
     * It iterates for all the {@code attributes} and merges them. If {@code name}
     * of the given instance and this instance is not the same, it writes out
     * {@code warn} level log.
     *
     * @param newDefinition
     */
    @Override
    public void merge(Definition newDefinition) {
        if (newDefinition.getName() == null
                || !newDefinition.getName().equals(this.getName())
                || !(newDefinition instanceof TableDefinition)) {
            log.warn("TableDefinition {} cannot be merged to {}.", newDefinition.getName(), getName());
        }
        TableDefinition definition = (TableDefinition) newDefinition;
        definition
            .getAttributes()
            .forEach(
                (key, newColumn) ->
                {
                    SingleDefinition columnDefinition = attributes.get(key);
                    if (columnDefinition == null) {
                        attributes.put(key, newColumn);
                    } else {
                        columnDefinition.merge(newColumn);
                    }
                });
        addOptions(definition.getOptions());
    }

    @Override
    public boolean validate() {
        return getStartCell() != null && attributes.size() > 0;
    }

    /**
     * Sets {@code sheet} to this object and SingleDefinition objects in attributes.
     */
    @Override
    public void setSheet(XlSheet sheet) {
        super.setSheet(sheet);
        for (SingleDefinition attr : getAttributes().values()) {
            attr.setSheet(sheet);
        }
    }

    /**
     * Sets {@code sheetName} to this object and SingleDefinition objects in
     * attributes.
     */
    @Override
    public void setSheetName(String sheetName) {
        super.setSheetName(sheetName);
        for (SingleDefinition attr : getAttributes().values()) {
            attr.setSheetName(sheetName);
        }
    }

    @Override
    public String toString() {
        return String.format("TableDefinition [name=%s, attributes=%s]", getName(), attributes);
    }
}
