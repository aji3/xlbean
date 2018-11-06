package org.xlbean.definition;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.xlbean.XlBeanTest;
import org.xlbean.excel.XlCellAddress;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.util.FileUtil;

public class DefinitionRepositoryTest {

    @Test
    public void validate_errorCellDef() throws Exception {

        PrintStream originalPs = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream testPs = new PrintStream(baos);
        System.setOut(testPs);

        InputStream in = DefinitionRepositoryTest.class.getResourceAsStream("TestBook_withError.xlsx");

        XlBeanReader reader = new XlBeanReader();
        reader.read(in);

        System.setOut(originalPs);

        String message = new String(baos.toByteArray());
        System.out.println(message);
        assertThat(
            message,
            is(
                containsString(
                    "WARN  o.x.definition.BeanDefinitionLoader - Invalid definition [columnOnly] (org.xlbean.definition.SingleDefinition)")));
    }

    @Test
    public void validate_errorSheetName() throws Exception {

        PrintStream originalPs = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream testPs = new PrintStream(baos);
        System.setOut(testPs);

        InputStream in = XlBeanTest.class.getResourceAsStream("TestBook_XlBean.xlsx");
        Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in));

        XlWorkbook xlwb = XlWorkbook.wrap(wb);

        SingleDefinition def = new SingleDefinition();
        def.setName("errorDef");
        XlCellAddress cell = new XlCellAddress.Builder().row(3).column(3).build();
        def.setCell(cell);
        def.setSheetName("errorSheet");

        DefinitionRepository repo = new DefinitionRepository();
        repo.addDefinition(def);
        repo.activate(xlwb);

        System.setOut(originalPs);

        String message = new String(baos.toByteArray());
        System.out.println(message);
        assertThat(
            message,
            is(
                containsString(
                    "WARN  o.x.definition.BeanDefinitionLoader - No sheet named \"errorSheet\" was found for definition \"errorDef\". (org.xlbean.definition.SingleDefinition)")));
    }
}
