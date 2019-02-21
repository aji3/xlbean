package org.xlbean.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForQiita2 {

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean.get("name"));// United States of America
        System.out.println(bean.bean("stats").get("totalArea"));// 9833520.0
        System.out.println(bean.bean("stats").get("gdp"));// 18558000000000000

    }
}
