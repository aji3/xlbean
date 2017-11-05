package org.xlbean.exception;

@SuppressWarnings("serial")
public class XlBeanException extends RuntimeException {

  public XlBeanException(Exception e) {
    super(e);
  }
}
