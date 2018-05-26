package org.xlbean.util;

import java.util.List;

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

    public XlBean createBean() {
        return new XlBean();
    }

    @SuppressWarnings("unchecked")
    public <T extends List<?>> T createList() {
        return (T) new XlList();
    }

}
