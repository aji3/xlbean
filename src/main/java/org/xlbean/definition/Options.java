package org.xlbean.definition;

import java.util.HashMap;
import java.util.Map;

public class Options {

    private Map<String, String> optionsMap = new HashMap<>();

    public String getOption(String key) {
        if (key == null) {
            return null;
        }
        return optionsMap.get(key);
    }

    public void setOption(String key, String value) {
        optionsMap.put(key, value);
    }

    public void setOptions(Map<String, String> newOptions) {
        if (newOptions == null) {
            return;
        }
        optionsMap.putAll(newOptions);
    }

    /**
     * 
     * 
     * @param newOptions
     */
    public void merge(Options newOptions) {
        optionsMap.putAll(newOptions.getMap());
    }

    public Map<String, String> getMap() {
        return optionsMap;
    }

    public boolean isEmpty() {
        return optionsMap.isEmpty();
    }

    public Options clone() {
        Options clone = new Options();
        clone.setOptions(optionsMap);
        return clone;
    }

    @Override
    public String toString() {
        return "Options [optionsMap=" + optionsMap + "]";
    }

}
