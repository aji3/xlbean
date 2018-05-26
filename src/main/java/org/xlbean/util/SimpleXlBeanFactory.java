package org.xlbean.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleXlBeanFactory extends XlBeanFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Map<String, Object>> T createBean() {
        return (T) new HashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends List<?>> T createList() {
        return (T) new ArrayList<Object>();
    }
}
