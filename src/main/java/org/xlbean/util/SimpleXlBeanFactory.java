package org.xlbean.util;

import java.util.ArrayList;
import java.util.List;

import org.xlbean.XlBean;

public class SimpleXlBeanFactory extends XlBeanFactory {

    @Override
    public XlBean createBean() {
        return new SimpleXlBean();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends List<?>> T createList() {
        return (T) new ArrayList<Object>();
    }

    @SuppressWarnings("serial")
    public static class SimpleXlBean extends XlBean {
        @Override
        protected boolean canPut(Object value) {
            return true;
        }
    }
}
