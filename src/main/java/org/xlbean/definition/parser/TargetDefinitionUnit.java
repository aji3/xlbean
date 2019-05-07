package org.xlbean.definition.parser;

import java.util.List;

/**
 * Definition class applicable only for target sheet symbol "####".
 * 
 * @author Kazuya Tanikawa
 *
 */
public class TargetDefinitionUnit {

    private List<DefinitionOption> options;

    public TargetDefinitionUnit(List<DefinitionOption> options) {
        this.options = options;
    }

    public List<DefinitionOption> getOptions() {
        return options;
    }

    public void setOptions(List<DefinitionOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "TargetDefinitionUnit [options=" + options + "]";
    }
}
