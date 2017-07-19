package org.xlbean.data;

import org.xlbean.XlBean;
import org.xlbean.definition.Definition;
import org.xlbean.excel.XlSheet.ValueType;

public abstract class ValueSaver<T extends Definition> {

	abstract public void save(XlBean bean);

	private T definition;

	public ValueSaver(T definition) {
		this.definition = (T) definition;
	}

	public T getDefinition() {
		return definition;
	}

	public void setDefinition(T definition) {
		this.definition = definition;
	}

	protected void setValue(Definition definition, int row, int column, String value) {
		String type = definition.getOptions().get("type");
		if (type == null) {
			definition.getSheet().setCellValue(row, column, value);
		} else {
			definition.getSheet().setCellValue(row, column, value, ValueType.valueOf(type));
		}
	}

	@SuppressWarnings("unchecked")
	protected <S> S getValueFromXlBean(String name, XlBean bean) {
		if (bean == null) {
			return null;
		}
		if (name.contains(".")) {
			Object value = bean.get(name.substring(0, name.indexOf('.')));
			if (value instanceof XlBean) {
				return (S) getValueFromXlBean(name.substring(name.indexOf('.') + 1, name.length()), (XlBean) value);
			} else {
				return null;
			}
		} else {
			return (S) bean.get(name);
		}
	}

}
