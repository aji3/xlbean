package org.xlbean;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithub4 {

  public static void main(String[] args) throws Exception {
    InputStream in = new FileInputStream("example/tableToRight.xlsx");
    XlBeanReader reader = new XlBeanReader();
    XlBean bean = reader.read(in);

    XlList forecasts = bean.list("forecasts");
    System.out.println(forecasts);
  }
}
