package org.xlbean.definition.parser;

/**
 * Class to hold parsed option value.
 * 
 * @author tanikawa
 *
 */
public class DefinitionOption {

    private String name;
    private String value;

    public DefinitionOption(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DefinitionOption [name=" + name + ", value=" + value + "]";
    }
}
