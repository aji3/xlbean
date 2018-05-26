package org.xlbean.util;

import java.util.List;
import java.util.Map;

import org.xlbean.XlBean;
import org.xlbean.XlList;

public class XlBeanFactory {

    private static XlBeanFactory INSTANCE = new XlBeanFactory();

    public static XlBeanFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(XlBeanFactory instance) {
        XlBeanFactory.INSTANCE = instance;
    }

    @SuppressWarnings("unchecked")
    public <T extends Map<String, Object>> T createBean() {
        return (T) new XlBean();
    }

    @SuppressWarnings("unchecked")
    public <T extends List<?>> T createList() {
        return (T) new XlList();
    }

}
