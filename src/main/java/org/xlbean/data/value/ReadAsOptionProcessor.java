package org.xlbean.data.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.definition.Definition;
import org.xlbean.excel.XlSheet.ReadAsOption;

public class ReadAsOptionProcessor {
    public static final String OPTION_READAS = "readAs";

    private static Logger log = LoggerFactory.getLogger(ConverterOptionProcessor.class);

    public boolean hasOption(Definition definition) {
        return getOption(definition) != null;
    }

    public ReadAsOption getOption(Definition definition) {
        String option = definition.getOptions().get(OPTION_READAS);
        if (option == null) {
            return null;
        }
        try {
            return ReadAsOption.valueOf(option);
        } catch (IllegalArgumentException e) {
            // ignore
            log.warn("Invalid readAs option value: {}", option);
        }
        return null;
    }

}
