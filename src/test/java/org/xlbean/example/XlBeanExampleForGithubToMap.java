package org.xlbean.example;

import java.io.FileInputStream;
import java.io.InputStream;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithubToMap {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/optionExample.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlBean toMapExample1 = bean.bean("toMapExample1");
        System.out.println(toMapExample1);
        XlBean toMapExample2 = bean.bean("toMapExample2");
        System.out.println(toMapExample2);
    }
}
