package org.xlbean.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithubFieldType {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/optionExample.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<XlBean> fieldType = bean.beans("fieldTypeExample");
        System.out.println(fieldType);

    }
}
