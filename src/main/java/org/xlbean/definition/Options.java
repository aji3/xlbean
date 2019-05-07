package org.xlbean.definition;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder of options.
 * 
 * @author Kazuya Tanikawa
 *
 */
public class Options {

    private Map<String, String> optionsMap = new HashMap<>();

    private Options parent;

    /**
     * Get Option value of the {@code key} from this Options instance and return if
     * not null. If the value is null, call getOptionDeep of the {@code parent}
     * instance and return so that any option set to parent can be obtained.
     * 
     * @param key
     * @return
     */
    public String getOptionDeep(String key) {
        if (key == null) {
            return null;
        }
        String value = optionsMap.get(key);
        if (value == null && parent != null) {
            return parent.getOptionDeep(key);
        } else {
            return value;
        }
    }

    public String getOption(String key) {
        if (key == null) {
            return null;
        }
        return optionsMap.get(key);
    }

    /**
     * Put key-value into the Options map. If the given {@code key} already exists,
     * then it will override.
     * 
     * @param key
     * @param value
     */
    public void addOption(String key, String value) {
        optionsMap.put(key, value);
    }

    /**
     * Put all key-values in the given {@code newOptions} into the Options map. If
     * any of the the given {@code key} already exists, then it will override.
     * 
     * @param key
     * @param value
     */
    public void addOptions(Map<String, String> newOptions) {
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
        clone.addOptions(optionsMap);
        return clone;
    }

    @Override
    public String toString() {
        return "Options [optionsMap=" + optionsMap + "]";
    }

    public Options getParent() {
        return parent;
    }

    public void setParent(Options parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

}
