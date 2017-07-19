package org.xlbean.data;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.SingleDefinition;
import org.xlbean.definition.TableDefinition;
import org.xlbean.excel.XlWorkbook;

public class ExcelDataSaver {

	private static Logger log = LoggerFactory.getLogger(ExcelDataSaver.class);

	public void save(XlBean bean, DefinitionRepository definitions, XlWorkbook workbook, OutputStream out) {
	    definitions.validate(workbook);
		
		definitions.forEach(definition -> {
			long now = System.currentTimeMillis();
			log.debug("Start saving data: {}", definition.getName());
			getValueSaver(definition).save(bean);
			log.info("Saved data: {} [{} msec]", definition.getName(), (System.currentTimeMillis() - now));
		});
		
		try {
			workbook.write(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected ValueSaver<?> getValueSaver(Definition definition) {
		if (definition instanceof SingleDefinition) {
			return new SingleValueSaver((SingleDefinition)definition);
		} else {
			return new TableValueSaver((TableDefinition)definition);
		}
	}
}
