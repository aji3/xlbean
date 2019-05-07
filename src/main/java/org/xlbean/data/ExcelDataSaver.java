package org.xlbean.data;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.data.value.ValueSaver;
import org.xlbean.data.value.single.SingleValueSaver;
import org.xlbean.data.value.table.TableValueSaver;
import org.xlbean.definition.Definition;
import org.xlbean.definition.Definitions;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.definition.TargetDefinition;
import org.xlbean.exception.XlBeanException;

public class ExcelDataSaver {

    private static Logger log = LoggerFactory.getLogger(ExcelDataSaver.class);

    public void save(XlBean bean, Definitions definitions, OutputStream out) {
        definitions
            .stream()
            .filter(d -> !(d instanceof TargetDefinition))
            .forEach(
                definition ->
                {
                    long now = System.currentTimeMillis();
                    log.debug("Start saving data: {}", definition.getName());
                    getValueSaver(definition).save(bean);
                    log
                        .info(
                            "Saved data: {} [{} msec]",
                            definition.getName(),
                            (System.currentTimeMillis() - now));
                });

        try {
            definitions.write(out);
        } catch (IOException e) {
            throw new XlBeanException(e);
        }
    }

    protected ValueSaver<?> getValueSaver(Definition definition) {
        if (definition instanceof SingleDefinition) {
            return new SingleValueSaver((SingleDefinition) definition);
        } else if (definition instanceof TableDefinition){
            return new TableValueSaver((TableDefinition) definition);
        } else {
            throw new IllegalArgumentException("Unexpected Definition instance: " + definition.getClass());
        }
    }
}
