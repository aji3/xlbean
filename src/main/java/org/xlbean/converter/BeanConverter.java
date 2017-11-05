package org.xlbean.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xlbean.XlBean;

/**
 * Converter for {@link XlBean} to generic class or vice-versa.
 *
 * @author Kazuya Tanikawa
 */
public interface BeanConverter {

  public <T> T toBean(Object srcObj, Class<?> destinationClazz);

  /**
   * Convert {@code srcList} to {@link ArrayList}. If {@code srcList} is not an instance of {@link
   * Iterable}, then it returns blank {@link ArrayList}.
   *
   * @param srcList
   * @param beanClass
   * @return
   */
  public <T> List<T> toBeanList(Object srcList, Class<?> beanClass);

  public <T> T toBean(Map<String, Object> sourceMap, Class<?> destinationClass);

  public <T> T toBean(Map<String, Object> sourceMap, Object destinationObj);

  public Object toMap(Object value);
}
