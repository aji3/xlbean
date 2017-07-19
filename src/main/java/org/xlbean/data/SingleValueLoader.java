package org.xlbean.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.definition.SingleDefinition;

/**
 * Value loader to load a value of a cell.
 * 
 * @author Kazuya Tanikawa
 *
 */
public class SingleValueLoader extends ValueLoader<SingleDefinition> {

	private static Logger log = LoggerFactory.getLogger(SingleValueLoader.class);

	public SingleValueLoader(Definition definition) {
		super(definition);
	}

	/**
	 * 
	 */
	@Override
	public void load(XlBean bean) {
		SingleDefinition definition = getDefinition();
		String value = getValue(definition, definition.getCell().getRow(), definition.getCell().getColumn());
		if (log.isTraceEnabled()) {
		    log.trace(value.toString());
		}
		convertDottedStringToBean(getDefinition().getName(), value, bean);
	}

}
