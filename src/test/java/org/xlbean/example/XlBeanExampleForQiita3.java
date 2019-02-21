package org.xlbean.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.testbean.Country;
import org.xlbean.testbean.President;
import org.xlbean.testbean.Stats;

public class XlBeanExampleForQiita3 {

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        // 単一Beanの変換
        Stats stats = bean.beanOf("stats", Stats.class);
        System.out.println(stats);

        // List単位の変換
        List<President> presidents = bean.listOf("presidents", President.class);
        System.out.println(presidents);

        // ネストされたBeanの変換
        Country usa = bean.of(Country.class);
        System.out.println(usa);
    }
}
