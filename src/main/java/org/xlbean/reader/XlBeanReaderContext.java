package org.xlbean.reader;

import org.xlbean.XlBean;
import org.xlbean.definition.DefinitionRepository;

public class XlBeanReaderContext {

    private XlBean xlBean;
    private DefinitionRepository definitions;

    public XlBean getXlBean() {
        return xlBean;
    }

    public void setXlBean(XlBean xlBean) {
        this.xlBean = xlBean;
    }

    public DefinitionRepository getDefinitions() {
        return definitions;
    }

    public void setDefinitions(DefinitionRepository definitions) {
        this.definitions = definitions;
    }
}
