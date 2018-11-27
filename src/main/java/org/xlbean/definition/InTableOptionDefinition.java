package org.xlbean.definition;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.excel.XlCellAddress;

public class InTableOptionDefinition extends Definition {

    private static Logger log = LoggerFactory.getLogger(InTableOptionDefinition.class);

    private List<OptionKey> optionKeys = new ArrayList<>();

    @Override
    public boolean validate() {
        // at least one of row or column should exist
        return optionKeys.size() > 0;
    }

    @Override
    public void merge(Definition newDefinition) {
        if (newDefinition == null) {
            return;
        }
        if (newDefinition.getName() == null
                || !newDefinition.getName().equals(this.getName())
                || !(newDefinition instanceof InTableOptionDefinition)) {
            log.warn(
                "Definition instance {} cannot be merged to {}.",
                newDefinition.getName(),
                getName());
            return;
        }
        InTableOptionDefinition definition = (InTableOptionDefinition) newDefinition;
        optionKeys.addAll(definition.getOptionKeys());
    }

    public void addOption(String optionKey, XlCellAddress cell) {
        OptionKey opt = new OptionKey();
        opt.setOptionKey(optionKey);
        opt.setCell(cell);
        optionKeys.add(opt);
    }

    public static class OptionKey {
        private String optionKey;
        private XlCellAddress cell;

        public String getOptionKey() {
            return optionKey;
        }

        public void setOptionKey(String optionKey) {
            this.optionKey = optionKey;
        }

        public XlCellAddress getCell() {
            return cell;
        }

        public void setCell(XlCellAddress cell) {
            this.cell = cell;
        }
    }

    public List<OptionKey> getOptionKeys() {
        return optionKeys;
    }

}
