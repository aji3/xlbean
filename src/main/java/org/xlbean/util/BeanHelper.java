package org.xlbean.util;

import java.util.List;
import java.util.Map;

public class BeanHelper {

    /**
     * Set {@code data} to {@code index} of {@code list}. If index is bigger than
     * current size of {@code list}, then fills the list with null.
     * 
     * @param list
     * @param index
     * @param data
     */
    public static <T> void setFillNull(List<T> list, int index, T data) {
        while (list.size() < index) {
            list.add(null);
        }
        if (list.size() == index) {
            list.add(data);
        } else {
            list.set(index, data);
        }
    }

    /**
     * Returns true if this XlBean contains no value or all values for this Map is
     * empty value.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValuesEmpty(Map<String, ?> map) {
        for (Object item : map.values()) {
            if (item == null) {
                continue;
            }
            if (item instanceof String && !((String) item).isEmpty()) {
                return false;
            } else if (item instanceof List && ((List<?>) item).size() != 0) {
                for (Object obj : (List<?>) item) {
                    if (obj == null) {
                        continue;
                    }
                    if (obj instanceof Map) {
                        if (!isValuesEmpty((Map<String, ?>) obj)) {
                            return false;
                        }
                    } else if (obj instanceof String) {
                        if (!((String) obj).isEmpty()) {
                            return false;
                        }
                    }
                }
            } else if (item instanceof Map) {
                if (!(isValuesEmpty((Map<String, ?>) item))) {
                    return false;
                }
            }
        }
        return true;
    }

}
