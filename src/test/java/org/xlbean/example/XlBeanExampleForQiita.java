package org.xlbean.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForQiita {

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<XlBean> list = bean.beans("presidents");
        list.forEach(System.out::println);
    }
}
