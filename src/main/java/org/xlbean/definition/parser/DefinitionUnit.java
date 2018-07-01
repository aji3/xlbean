package org.xlbean.definition.parser;

import java.util.List;

/**
 * Class to hold definition name.
 * 
 * @author tanikawa
 *
 */
public class DefinitionUnit {

    private String name;
    private List<DefinitionOption> options;

    public DefinitionUnit(String name, List<DefinitionOption> options) {
        this.name = name;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DefinitionOption> getOptions() {
        return options;
    }

    public void setOptions(List<DefinitionOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "DefinitionUnit [name=" + name + ", options=" + options + "]";
    }

}
