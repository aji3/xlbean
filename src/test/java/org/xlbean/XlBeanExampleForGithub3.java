package org.xlbean;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithub3 {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/presidents_index.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList presidents = (XlList) bean.beans("presidents");
        Map<String, String> condition = new HashMap<>();
        condition.put("id", "45");

        XlBean p = presidents.find(condition);
        System.out.println(
            p); // {name=Donald Trump, dateOfBirth=1946-06-14T00:00:00.000, id=45,
                // inOfficeFrom=2017-01-20T00:00:00.000, stateOfBirth=New York}
        p = presidents.findByIndex(condition);
        System.out.println(
            p); // {name=Donald Trump, dateOfBirth=1946-06-14T00:00:00.000, id=45,
                // inOfficeFrom=2017-01-20T00:00:00.000, stateOfBirth=New York}
    }
}
