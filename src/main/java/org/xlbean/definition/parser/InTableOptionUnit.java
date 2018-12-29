package org.xlbean.definition.parser;

public class InTableOptionUnit {

    private String name;
    private String optionKey;

    public InTableOptionUnit(String name, String optionKey) {
        this.name = name;
        this.optionKey = optionKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    @Override
    public String toString() {
        return "OptionPair [name=" + name + ", optionKey=" + optionKey + "]";
    }

}
