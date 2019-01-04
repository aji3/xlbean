package org.xlbean.reader;

import org.xlbean.XlBean;
import org.xlbean.definition.Definitions;

public class XlBeanReaderContext {

    private XlBean xlBean;
    private Definitions definitions;

    public XlBean getXlBean() {
        return xlBean;
    }

    public void setXlBean(XlBean xlBean) {
        this.xlBean = xlBean;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}
