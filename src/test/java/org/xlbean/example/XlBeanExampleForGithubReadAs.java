package org.xlbean.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithubReadAs {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/optionExample.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        List<XlBean> readAsExample = bean.beans("readAs");

        // no option
        System.out.println(readAsExample.get(0).get("defType")); // 0.0
        System.out.println(readAsExample.get(0).get("strType")); // 0

        System.out.println(readAsExample.get(1).get("defType")); // 1.0
        System.out.println(readAsExample.get(1).get("strType")); // 1

        // readAs=text
        System.out.println(readAsExample.get(0).get("defWithOption")); // 0 <- Changed from 0.0 by readAs=text
        System.out.println(readAsExample.get(0).get("strWithOption")); // 0

        System.out.println(readAsExample.get(1).get("defWithOption")); // 1 <- Changed from 0.0 by readAs=text
        System.out.println(readAsExample.get(1).get("strWithOption")); // 1

    }
}
