package org.xlbean;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithub4 {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/tableToRight.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<XlBean> forecasts = bean.beans("forecasts");
        System.out.println(forecasts);
    }
}
