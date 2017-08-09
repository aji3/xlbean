package org.xlbean.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.converter.BeanConverter;
import org.xlbean.definition.BeanDefinitionLoader;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderTest;
import org.xlbean.util.FileUtil;
import org.xlbean.writer.XlBeanWriter;

public class XlBeanWriterTest {

    @Test
    public void test_write() throws FileNotFoundException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        System.out.println(bean);
        in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");
        XlBeanWriter writer = new XlBeanWriter();
        writer.write(in, bean, new FileOutputStream("test.xlsx"));
    }
    
    @Test
    public void test_write_with_bean_definition() throws IOException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlBeanWriter writer = new XlBeanWriter(new BeanDefinitionLoader());
        writer.write(bean, null, bean, new FileOutputStream("test_with_bean_definition.xlsx"));
    }
    
    @Test
    public void test_write_with_bean_definition2() throws IOException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");

        DefinitionRepository definitions = null;
        try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in))) {
            DefinitionLoader<?> loader = new ExcelR1C1DefinitionLoader();
            loader.initialize(wb);
            definitions = loader.load();
        } catch (EncryptedDocumentException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        
        in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");
        XlBeanWriter writer = new XlBeanWriter(new BeanDefinitionLoader());
        XlBean bean = new XlBean();
        bean.put("definitions", new BeanConverter().toMap(definitions));
        writer.write(bean, null, bean, new FileOutputStream("test_with_bean_definition2.xlsx"));
    }
     
}
