package org.xlbean.util;

import java.util.ArrayList;
import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.XlBeanImpl;

public class XlBeanFactory {

    private static XlBeanFactory INSTANCE = new XlBeanFactory();

    public static XlBeanFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(XlBeanFactory instance) {
        XlBeanFactory.INSTANCE = instance;
    }

    public XlBean createBean() {
        return new XlBeanImpl();
    }

    public List<XlBean> createList() {
        return new ArrayList<>();
    }

}
