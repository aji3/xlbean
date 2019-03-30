package org.xlbean.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithub3 {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/presidents_index.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<XlBean> presidents = bean.beans("presidents");
        Map<String, String> condition = new HashMap<>();
        condition.put("id", "45");

    }
}
