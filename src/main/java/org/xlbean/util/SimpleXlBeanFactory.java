package org.xlbean.util;

import java.util.ArrayList;
import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.XlBeanImpl;

public class SimpleXlBeanFactory extends XlBeanFactory {

    @Override
    public XlBean createBean() {
        return new SimpleXlBean();
    }

    @Override
    public List<XlBean> createList() {
        return new ArrayList<XlBean>();
    }

    @SuppressWarnings("serial")
    public static class SimpleXlBean extends XlBeanImpl {
        @Override
        protected boolean canPut(Object value) {
            return true;
        }
    }
}
