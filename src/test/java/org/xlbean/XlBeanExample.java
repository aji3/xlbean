package org.xlbean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.xlbean.reader.XlBeanReader;
import org.xlbean.testbean.President;
import org.xlbean.testbean.Stats;
import org.xlbean.writer.XlBeanWriter;

public class XlBeanExample {

    public static void main(String[] args) throws FileNotFoundException {

        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<XlBean> list = bean.beans("presidents");
        list.forEach(System.out::println);

        System.out.println(bean.get("name"));
        System.out.println(bean.bean("stats").get("totalArea"));
        System.out.println(bean.bean("stats").get("gdp"));

        List<President> presidents = bean.listOf("presidents", President.class);
        System.out.println(presidents);

        Stats stats = bean.beanOf("stats", Stats.class);
        Stats stats2 = bean.bean("stats").of(Stats.class);
        System.out.println(stats);
        System.out.println(stats2);

        XlBeanWriter writer = new XlBeanWriter();
        XlBean outBean = new XlBeanImpl();
        outBean.set("presidents", presidents);
        writer.write(
            new FileInputStream("example/presidents_blank.xlsx"),
            outBean,
            new FileOutputStream("newPresidents.xlsx"));
    }
}
