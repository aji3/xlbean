package org.xlbean;

import java.io.FileInputStream;
import java.io.InputStream;

import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithub2 {

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("example/optionExample.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList noOptionTable = bean.list("noOptionTable");
        System.out.println(noOptionTable.get(0).get("defaultCellType")); // 0.0
        System.out.println(noOptionTable.get(0).get("stringCellType")); // 0

        System.out.println(noOptionTable.get(1).get("defaultCellType")); // 1.0
        System.out.println(noOptionTable.get(1).get("stringCellType")); // 1

        XlList optionTable = bean.list("optionTable");
        System.out.println(optionTable.get(0).get("defaultCellType")); // 0 <= Treated as string
        System.out.println(optionTable.get(0).get("stringCellType")); // 0

        System.out.println(optionTable.get(1).get("defaultCellType")); // 1 <= Treated as string
        System.out.println(optionTable.get(1).get("stringCellType")); // 1

        XlList limitedTable = bean.list("limitedTable");
        System.out.println(limitedTable.size()); // 5 <= Number of rows loaded is limited to 5
        System.out.println(limitedTable.get(0).value("value")); // 0.0 <= The list is started from row 1
        System.out.println(limitedTable.get(1).value("value")); // 1.0
    }
}
