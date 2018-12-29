package org.xlbean.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.excel.XlCellAddress;

/**
 * Definition for a value defined in a single cell or column in a table.
 * 
 * <p>
 * When it is used for column definition for {@link TableDefinition},
 * {@link XlCellAddress} could only have either of row or column.
 * </p>
 *
 * @author Kazuya Tanikawa
 */
public class SingleDefinition extends Definition {

    private static Logger log = LoggerFactory.getLogger(SingleDefinition.class);

    private XlCellAddress cell;

    public SingleDefinition() {};

    public XlCellAddress getCell() {
        return cell;
    }

    public void setCell(XlCellAddress cell) {
        this.cell = cell;
    }

    @Override
    public void merge(Definition newDefinition) {
        if (newDefinition == null) {
            return;
        }
        if (newDefinition.getName() == null
                || !newDefinition.getName().equals(this.getName())
                || !(newDefinition instanceof SingleDefinition)) {
            log.warn(
                "SingleValueDefinition {} cannot be merged to {}.",
                newDefinition.getName(),
                getName());
            return;
        }
        SingleDefinition definition = (SingleDefinition) newDefinition;
        if (cell != null) {
            cell.merge(definition.getCell());
        }
    }

    @Override
    public boolean validate() {
        return cell != null && cell.getRow() != null && cell.getColumn() != null;
    }

    @Override
    public String toString() {
        return String.format("SingleDefinition [name=%s, cell=%s, options=%s]", getName(), cell, getOptions());
    }
}
