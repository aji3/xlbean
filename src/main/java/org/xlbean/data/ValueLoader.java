package org.xlbean.data;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.definition.Definition;
import org.xlbean.excel.XlSheet.ValueType;
import org.xlbean.util.XlBeanFactory;

public abstract class ValueLoader<T extends Definition> {

	/**
	 * Populate {@code bean} with {@code definition}.
	 * 
	 * @param bean
	 */
	abstract public void load(XlBean bean);

	private T definition;

	@SuppressWarnings("unchecked")
	public ValueLoader(Definition definition) {
		this.definition = (T) definition;
	}

	public T getDefinition() {
		return definition;
	}

	public void setDefinition(T definition) {
		this.definition = definition;
	}

	protected String getValue(Definition definition, int row, int column) {
		String type = definition.getOptions().get("type");
		if (type == null) {
			return definition.getSheet().getCellValue(row, column);
		} else {
			return definition.getSheet().getCellValue(row, column, ValueType.valueOf(type));
		}
	}

    protected void convertDottedStringToBean(String name, Object data, XlBean bean) {
        if (data == null) {
            return;
        }
        if (name.contains(".")) {
            XlBean newBean = toBean(name, data);
            mergeMap(bean, newBean);
        } else {
            bean.put(name, data);
        }
    }

    private void mergeMap(XlBean base, XlBean additional) {
        if (additional == null) {
            return;
        }
        for (Entry<String, Object> entry : additional.entrySet()) {
            Object baseValue = base.get(entry.getKey());
            if (baseValue == null) {
                base.put(entry.getKey(), entry.getValue());
            } else {
                if (baseValue instanceof Map && entry.getValue() instanceof Map) {
                    mergeMap((XlBean) baseValue, (XlBean) entry.getValue());
                } else if (baseValue instanceof List && entry.getValue() instanceof List) {
                    mergeList((XlList) baseValue, (XlList) entry.getValue());
                } else {
                    base.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void mergeList(XlList baseList, XlList additional) {
        for (int i = 0; i < additional.size(); i++) {
            XlBean add = additional.get(i);
            if (add != null) {
                if (baseList.size() > i && baseList.get(i) != null) {
                    mergeMap(baseList.get(i), add);
                } else {
                    baseList.setFillNull(i, add);
                }
            }
        }
    }

    private XlBean toBean(String name, Object data) {
        if (data == null) {
            return null;
        }
		XlBean ret = XlBeanFactory.getInstance().createBean();
        if (name.contains(".")) {
            String thisName = name.substring(0, name.indexOf('.'));
            String childName = name.substring(name.indexOf('.') + 1);
            XlBean childBean = toBean(childName, data);
            if (childBean == null || childBean.isValuesEmpty()) {
                return null;
            }
            if (thisName.contains("[")) {
                String thisListName = name.substring(0, name.indexOf('['));
                int index = Integer.parseInt(thisName.substring(thisName.indexOf('[') + 1, thisName.length() - 1));
                XlList childList = ret.list(thisListName);
                if (childList == null) {
                    childList = XlBeanFactory.getInstance().createList();
                }
                childList.setFillNull(index, childBean);
                ret.put(thisListName, childList);
            } else {
                ret.put(thisName, childBean);
            }
        } else {
            ret.put(name, data);    
        }
        return ret;
    }
}
