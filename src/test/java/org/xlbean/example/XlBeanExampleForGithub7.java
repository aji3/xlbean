package org.xlbean.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;

public class XlBeanExampleForGithub7 {

    public static void main(String[] args) throws FileNotFoundException {
        // Read data from Excel file
        InputStream in = new FileInputStream("example/optionExample.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

    }
}
