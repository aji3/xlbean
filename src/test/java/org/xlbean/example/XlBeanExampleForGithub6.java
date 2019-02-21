package org.xlbean.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.testbean.President;
import org.xlbean.util.XlBeanFactory;
import org.xlbean.writer.XlBeanWriter;

public class XlBeanExampleForGithub6 {

    public static void main(String[] args) throws FileNotFoundException {
        // Read data from Excel file
        InputStream in = new FileInputStream("example/presidents.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        // Convert XlBean to other class
        List<President> presidents = bean.listOf("presidents", President.class);

        // Make a small update
        List<President> sortedPresidents = presidents
            .stream()
            .sorted(Comparator.comparing(President::getNumberOfDaysInOffice).reversed())
            .collect(Collectors.toList());

        // XlBean to write out to Excel file
        XlBean outBean = new XlBeanFactory().createBean();
        // Since `sortedPresidents` is a list of non-XlBean, using `set(String key,
        // Object value)` to set to XlBean.
        outBean.set("presidents", sortedPresidents);

        // Write `outBean` to `newPresidents.xlsx`
        XlBeanWriter writer = new XlBeanWriter();
        writer.write(
            new FileInputStream("example/presidents_blank.xlsx"),
            outBean,
            new FileOutputStream("newPresidents.xlsx"));
    }
}
