package org.xlbean;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.xlbean.reader.XlBeanReader;
import org.xlbean.testbean.President;

public class XlBeanExampleForGithub {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        // Get value as String
        List<XlBean> list = bean.beans("presidents");
        System.out.println(list.get(0).get("name")); // John F. Kennedy
        System.out.println(list.get(0).get("dateOfBirth")); // 1917-05-29T00:00:00.000

        // Map value to Class
        List<President> presidents = bean.listOf("presidents", President.class);
        System.out.println(presidents);

        // Get value of a single cell
        String name = bean.string("name"); // United States of America
        System.out.println(name);

        // Get value of a map
        XlBean stats = bean.bean("stats");
        System.out.println(stats.string("totalArea")); // 9833520.0
        System.out.println(stats.string("gdp")); // 18558000000000000
    }
}
