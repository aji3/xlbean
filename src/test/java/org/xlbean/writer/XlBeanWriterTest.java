package org.xlbean.writer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.XlBeanImpl;
import org.xlbean.converter.impl.BeanConverterImpl;
import org.xlbean.definition.BeanDefinitionLoader;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.Definitions;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.definition.Options;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderTest;
import org.xlbean.util.FileUtil;
import org.xlbean.writer.XlBeanWriter.XlBeanWriterBuilder;

public class XlBeanWriterTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        Files.createDirectories(Paths.get("build", "XlBeanWriterTest"));
    }

    /**
     * Format excel file to other excel file.
     */
    @Test
    public void test_write() throws FileNotFoundException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);
        in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");

        XlBeanWriter writer = new XlBeanWriter();
        try (OutputStream resultFile = new FileOutputStream("build/XlBeanWriterTest/test.xlsx");) {
            writer.write(in, bean, resultFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write data directly to format excel file
     */
    @Test
    public void test_write2() throws FileNotFoundException {
        test_write();

        XlBean bean = new XlBeanImpl();
        bean.put("testDate", "2018-10-30T11:22:33.123");

        System.out.println(bean);

        XlBeanWriter writer = new XlBeanWriter();
        writer.write(new File("build/XlBeanWriterTest/test.xlsx"), bean, new File("build/XlBeanWriterTest/test.xlsx"));

    }

    /**
     * Bean definition
     * 
     */
    @Test
    public void test_write_with_bean_definition() throws IOException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlBeanWriter writer = new XlBeanWriterBuilder().definitionLoader(new BeanDefinitionLoader()).build();
        writer
            .write(
                bean,
                bean,
                new FileOutputStream("build/XlBeanWriterTest/test_with_bean_definition.xlsx"));
    }

    /**
     * Bean definition with list in list
     */
    @Test
    public void test_write_with_bean_definition_listinlist() throws IOException {
        InputStream in = XlBeanWriterTest.class.getResourceAsStream("TestBook_BeanDefinitionLoader.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean expectedBean = reader.read(in);

        System.out.println(expectedBean);
        String expected = expectedBean.toString();

        XlBeanWriter writer = new XlBeanWriterBuilder().definitionLoader(new BeanDefinitionLoader(10)).build();
        writer
            .write(
                expectedBean,
                expectedBean,
                new FileOutputStream("build/XlBeanWriterTest/test_write_with_bean_definition_listinlist.xlsx"));

        XlBean beanFromWriter = reader
            .read(
                new File("build/XlBeanWriterTest/test_write_with_bean_definition_listinlist.xlsx"));

        System.out.println(beanFromWriter);

        assertThat(beanFromWriter.toString(), is(expected));
    }

    @Test
    public void test_write_with_bean_definition2() throws IOException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");

        Definitions definitions = null;
        try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in))) {
            DefinitionLoader loader = new ExcelR1C1DefinitionLoader(new Options());
            definitions = loader.load(wb);
        } catch (EncryptedDocumentException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");
        XlBeanWriter writer = new XlBeanWriterBuilder().definitionLoader(new BeanDefinitionLoader()).build();
        XlBean bean = new XlBeanImpl();
        bean.put("definitions", new BeanConverterImpl().toMap(definitions));
        writer
            .write(
                bean,
                bean,
                new FileOutputStream("build/XlBeanWriterTest/test_with_bean_definition2.xlsx"));
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");

    @Test
    public void test_write_converterOption() throws IOException, ParseException {
        InputStream in = XlBeanWriterTest.class.getResourceAsStream("TestBook_ValueConverter.xlsx");

        XlBean bean = new XlBeanImpl();
        List<XlBean> beans = new ArrayList<>();
        bean.put("testConverter", beans);
        XlBean elem = new XlBeanImpl();
        elem.put("_BigDecimal", new BigDecimal("2.345"));
        elem.put("_BigInteger", new BigInteger("1234"));
        elem.put("_Boolean", true);
        elem.put("_Character", new Character('a'));
        elem.put("_Date", sdf.parse("2019-03-12-12:34:56.789"));
        elem.put("_Double", 123.45);
        elem.put("_Integer", 123);
        elem.put("_LocalDate", LocalDate.of(2019, 3, 12));
        elem.put("_LocalDateTime", LocalDateTime.of(2019, 3, 12, 12, 34, 56));
        elem.put("_LocalTime", LocalTime.of(5, 6, 7));
        elem.put("_Long", 12345678901234l);
        elem.put("_Short", 123);
        elem.put("_String", 1);
        elem.put("_boolean", false);
        elem.put("_char", 'b');
        elem.put("_double", 123.456);
        elem.put("_int", 9876);
        elem.put("_long", 999999999999l);
        elem.put("_short", 23);
        beans.add(elem);
        XlBeanWriter writer = new XlBeanWriter();
        try (OutputStream os = new FileOutputStream("build/XlBeanWriterTest/test_ValueConverter.xlsx")) {
            writer.write(in, bean, os);
        }

        XlBeanReader reader = new XlBeanReader();
        try (InputStream is = new FileInputStream("build/XlBeanWriterTest/test_ValueConverter.xlsx")) {
            XlBean resultBean = reader.read(is);
            System.out.println(resultBean);

            List<XlBean> results = resultBean.beans("testConverter");
            assertThat(results.get(0).get("_BigDecimal"), is(new BigDecimal("2.345")));
            assertThat(results.get(0).get("_BigInteger"), is(new BigInteger("1234")));
            assertThat(results.get(0).get("_Boolean"), is(true));
            assertThat(results.get(0).get("_Character"), is(new Character('a')));
            assertThat(results.get(0).get("_Date"), is(sdf.parse("2019-03-12-12:34:56.789")));
            assertThat(results.get(0).get("_Double"), is(123.45));
            assertThat(results.get(0).get("_Integer"), is(123));
            assertThat(results.get(0).get("_LocalDate"), is(LocalDate.of(2019, 3, 12)));
            assertThat(results.get(0).get("_LocalDateTime"), is(LocalDateTime.of(2019, 3, 12, 12, 34, 56)));
            assertThat(results.get(0).get("_LocalTime"), is(LocalTime.of(5, 6, 7)));
            assertThat(results.get(0).get("_Long"), is(12345678901234l));
            assertThat(results.get(0).get("_Short"), is((short) 123));
            assertThat(results.get(0).get("_String"), is("1.0"));
            assertThat(results.get(0).get("_boolean"), is(false));
            assertThat(results.get(0).get("_char"), is('b'));
            assertThat(results.get(0).get("_double"), is(123.456));
            assertThat(results.get(0).get("_int"), is(9876));
            assertThat(results.get(0).get("_long"), is(999999999999l));
            assertThat(results.get(0).get("_short"), is((short) 23));
        }
    }
}
