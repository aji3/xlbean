package org.xlbean.reader;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.definition.DefinitionLoader;
import org.xlbean.definition.DefinitionRepository;
import org.xlbean.definition.ExcelCommentDefinitionLoader;
import org.xlbean.definition.ExcelR1C1DefinitionLoader;
import org.xlbean.excel.XlWorkbook;
import org.xlbean.testbean.Country;
import org.xlbean.testbean.PresidentEnum;
import org.xlbean.testbean.PresidentEnum.States;
import org.xlbean.testbean.TestBean;
import org.xlbean.util.FileUtil;

public class XlBeanReaderTest {

    @Test
    public void testFormat() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_format.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        XlList table = bean.list("format");

        assertThat(table.get(0).get("number"), is("12345.6789"));
        assertThat(table.get(1).get("number"), is("12345.6789"));
        assertThat(table.get(2).get("number"), is("12345.6789"));
        assertThat(table.get(3).get("number"), is("12345.6789"));
        assertThat(table.get(4).get("number"), is("12345.6789"));
        assertThat(table.get(5).get("number"), is("12345.6789"));
        assertThat(table.get(6).get("number"), is("12345.6789"));
        assertThat(table.get(7).get("number"), is("12345.6789"));
        assertThat(table.get(8).get("number"), is("12345.6789"));
        assertThat(table.get(9).get("number"), is("12345.6789"));
        assertThat(table.get(10).get("number"), is("12345.6789"));
        assertThat(table.get(11).get("number"), is("1.0"));
        assertThat(table.get(12).get("number"), is("0.0"));
        assertThat(table.get(13).get("number"), nullValue());

        assertThat(table.get(0).get("currency"), is("100.12"));
        assertThat(table.get(1).get("currency"), is("100.12"));
        assertThat(table.get(2).get("currency"), is("100.12"));
        assertThat(table.get(3).get("currency"), is("100.12"));
        assertThat(table.get(4).get("currency"), is("100.12"));
        assertThat(table.get(5).get("currency"), is("100.12"));
        assertThat(table.get(6).get("currency"), is("100.12"));
        assertThat(table.get(7).get("currency"), is("100.12"));
        assertThat(table.get(8).get("currency"), is("100.12"));
        assertThat(table.get(9).get("currency"), is("0.0"));
        assertThat(table.get(10).get("currency"), is(nullValue()));

        assertThat(table.get(0).get("accounting"), is("100.12"));
        assertThat(table.get(1).get("accounting"), is("100.12"));
        assertThat(table.get(2).get("accounting"), is("100.12"));
        assertThat(table.get(3).get("accounting"), is("0.0"));
        assertThat(table.get(4).get("accounting"), is(nullValue()));

        assertThat(table.get(0).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(1).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(2).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(3).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(4).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(5).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(6).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(7).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(8).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(9).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(10).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(11).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(12).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(13).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(14).get("date"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(15).get("date"), is("1899-12-30T00:00:00.000"));
        assertThat(table.get(16).get("date"), is(nullValue()));

        assertThat(table.get(0).get("time"), is("06:55:00.000"));
        assertThat(table.get(1).get("time"), is("06:55:00.000"));
        assertThat(table.get(2).get("time"), is("06:55:00.000"));
        assertThat(table.get(3).get("time"), is("06:55:00.000"));
        assertThat(table.get(4).get("time"), is("06:55:00.000"));
        assertThat(
            table.get(5).get("time"),
            is(
                "1899-12-30T06:55:00.000")); // This is actually not a time format, but it is correct
                                             // because this is the format which is specified on the
                                             // excel sheet.
        assertThat(
            table.get(6).get("time"),
            is(
                "1899-12-30T06:55:00.000")); // This is actually not a time format, but it is correct
                                             // because this is the format which is specified on the
                                             // excel sheet.
        assertThat(table.get(7).get("time"), is("06:55:00.000"));
        assertThat(table.get(8).get("time"), is("06:55:00.000"));
        assertThat(table.get(9).get("time"), is("06:55:00.000"));
        assertThat(table.get(10).get("time"), is("00:00:00.000"));
        assertThat(table.get(11).get("time"), is(nullValue()));

        assertThat(table.get(0).get("percentage"), is("0.1"));
        assertThat(table.get(1).get("percentage"), is("1.1"));
        assertThat(table.get(2).get("percentage"), is("0.0"));
        assertThat(table.get(3).get("percentage"), is(nullValue()));

        assertThat(table.get(0).get("string"), is("abcde"));
        assertThat(table.get(1).get("string"), is("12345"));
        assertThat(table.get(2).get("string"), is(nullValue()));

        assertThat(table.get(0).get("user"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(1).get("user"), is("2017-03-18T00:00:00.000"));
        assertThat(table.get(2).get("user"), is("06:59:00.000"));
        assertThat(table.get(3).get("user"), is("06:59:00.000"));
        assertThat(table.get(4).get("user"), is("1234.0"));
        assertThat(table.get(5).get("user"), is(nullValue()));

        assertThat(table.get(0).get("standardAndFormulaInside"), is("AAABBB"));
        assertThat(table.get(1).get("standardAndFormulaInside"), is("1.0"));
        assertThat(table.get(2).get("standardAndFormulaInside"), is("1"));
        assertThat(table.get(3).get("standardAndFormulaInside"), is("1.0"));
        assertThat(table.get(4).get("standardAndFormulaInside"), is("1"));
        assertThat(table.get(5).get("standardAndFormulaInside"), is(nullValue()));

        XlList tableStr = bean.list("formatStr");

        assertThat(tableStr.get(0).get("number"), is("12345.6789"));
        assertThat(tableStr.get(1).get("number"), is("12346"));
        assertThat(tableStr.get(2).get("number"), is("12346"));
        assertThat(tableStr.get(3).get("number"), is("12346"));
        assertThat(tableStr.get(4).get("number"), is("12346"));
        assertThat(tableStr.get(5).get("number"), is("12346"));
        assertThat(tableStr.get(6).get("number"), is("12346"));
        assertThat(tableStr.get(7).get("number"), is("12346"));
        assertThat(tableStr.get(8).get("number"), is("12346"));
        assertThat(tableStr.get(9).get("number"), is("12345.68"));
        assertThat(tableStr.get(10).get("number"), is("12,346"));
        assertThat(tableStr.get(11).get("number"), is("1"));
        assertThat(tableStr.get(12).get("number"), is("0"));
        assertThat(tableStr.get(13).get("number"), nullValue());

        // CAUTION: ¥ on excel is not backslash!!
        assertThat(tableStr.get(0).get("currency"), is("¥100"));
        assertThat(tableStr.get(1).get("currency"), is("¥100"));
        assertThat(tableStr.get(2).get("currency"), is("¥100"));
        assertThat(tableStr.get(3).get("currency"), is("¥100"));
        assertThat(tableStr.get(4).get("currency"), is("¥100"));
        assertThat(tableStr.get(5).get("currency"), is("¥100"));
        assertThat(tableStr.get(6).get("currency"), is("¥100.12"));
        // but $ is $
        assertThat(tableStr.get(7).get("currency"), is("$100.12"));
        assertThat(tableStr.get(8).get("currency"), is("100.12"));
        assertThat(tableStr.get(9).get("currency"), is("¥0.00"));
        assertThat(tableStr.get(10).get("currency"), is(nullValue()));

        assertThat(tableStr.get(0).get("accounting"), is("¥   100"));
        assertThat(tableStr.get(1).get("accounting"), is("¥   100.120"));
        assertThat(tableStr.get(2).get("accounting"), is("100"));
        assertThat(tableStr.get(3).get("accounting"), is("¥   -  0")); //
        assertThat(tableStr.get(4).get("accounting"), is(nullValue()));

        assertThat(tableStr.get(0).get("date"), is("3/18/17")); // Not the same
        assertThat(tableStr.get(1).get("date"), is("土曜日, 3月 18, 2017")); // Not the same
        assertThat(tableStr.get(2).get("date"), is("2017\"年\"3\"月\"18\"日\"")); // Not the same
        assertThat(tableStr.get(3).get("date"), is("2017\"年\"3\"月\"")); // Not the same
        assertThat(tableStr.get(4).get("date"), is("3\"月\"18\"日\"")); // Not the same
        assertThat(tableStr.get(5).get("date"), is("2017/3/18"));
        assertThat(tableStr.get(6).get("date"), is("2017/3/18 12:00 午前")); // Not the same
        assertThat(tableStr.get(7).get("date"), is("2017/3/18 0:00"));
        assertThat(tableStr.get(8).get("date"), is("3/18"));
        assertThat(tableStr.get(9).get("date"), is("03/18/17"));
        assertThat(tableStr.get(10).get("date"), is("18-3")); // Not the same
        assertThat(tableStr.get(11).get("date"), is("3-17")); // Not the same
        assertThat(tableStr.get(12).get("date"), is("3月-17")); // Not the same
        // assertThat(tableStr.get(13).get("date"), is("2017-03-18T00:00:00.000"));//
        // can't even assert
        // assertThat(tableStr.get(14).get("date"), is("2017-03-18T00:00:00.000"));//
        // can't even assert
        assertThat(
            tableStr.get(15).get("date"),
            is(
                "1899/12/31")); // Not the same as excel presentation, however meaning wise it is the
                                // same.
        assertThat(tableStr.get(16).get("date"), is(nullValue()));

        assertThat(tableStr.get(0).get("time"), is("6:55:00 午前")); // Not the same
        assertThat(tableStr.get(1).get("time"), is("6:55"));
        assertThat(tableStr.get(2).get("time"), is("6:55 午前")); // Not the same
        assertThat(tableStr.get(3).get("time"), is("6:55:00"));
        assertThat(tableStr.get(4).get("time"), is("6:55:00 午前")); // Not the same
        assertThat(tableStr.get(5).get("time"), is("1899/12/31 6:55 午前")); // Not the same
        assertThat(tableStr.get(6).get("time"), is("1899/12/31 6:55")); // Not the same
        assertThat(tableStr.get(7).get("time"), is("0.2881944444444445")); // Not the same
        assertThat(tableStr.get(8).get("time"), is("0.2881944444444445")); // Not the same
        assertThat(tableStr.get(9).get("time"), is("6:55"));
        assertThat(tableStr.get(10).get("time"), is("0:00"));
        assertThat(tableStr.get(11).get("time"), is(nullValue()));

        assertThat(tableStr.get(0).get("percentage"), is("10%"));
        assertThat(tableStr.get(1).get("percentage"), is("110.0%"));
        assertThat(tableStr.get(2).get("percentage"), is("0.0%"));
        assertThat(tableStr.get(3).get("percentage"), is(nullValue()));

        assertThat(tableStr.get(0).get("string"), is("abcde"));
        assertThat(tableStr.get(1).get("string"), is("12345"));
        assertThat(tableStr.get(2).get("string"), is(nullValue()));

        assertThat(tableStr.get(0).get("user"), is("2017"));
        assertThat(tableStr.get(1).get("user"), is("3"));
        assertThat(tableStr.get(2).get("user"), is("6"));
        assertThat(tableStr.get(3).get("user"), is("659"));
        assertThat(tableStr.get(4).get("user"), is("1234.000"));
        assertThat(tableStr.get(5).get("user"), is(nullValue()));

        assertThat(tableStr.get(0).get("standardAndFormulaInside"), is("AAABBB"));
        assertThat(tableStr.get(1).get("standardAndFormulaInside"), is("1"));
        assertThat(tableStr.get(2).get("standardAndFormulaInside"), is("1"));
        assertThat(tableStr.get(3).get("standardAndFormulaInside"), is("1"));
        assertThat(tableStr.get(4).get("standardAndFormulaInside"), is("1"));
        assertThat(tableStr.get(5).get("standardAndFormulaInside"), is(nullValue()));

        assertThat(bean.get("testDate"), is("2017-09-21T00:00:00.000"));
    }

    @Test
    public void testBeanMapping() throws Exception {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        Country america = bean.of(Country.class);

        assertThat(america.getName(), is("United States of America"));
        assertThat(america.getStats().getTotalArea(), comparesEqualTo(9833520L));
        assertThat(america.getStats().getGdp(), comparesEqualTo(new BigDecimal("18558000000000000")));

        assertThat(america.getPresidents().get(0).getName(), is("John F. Kennedy"));
        assertThat(america.getPresidents().get(0).getDateOfBirth(), is(LocalDate.of(1917, 5, 29)));
        assertThat(america.getPresidents().get(0).getStateOfBirth(), is("Massachusetts"));
        assertThat(
            america.getPresidents().get(0).getInOfficeFrom(),
            is(new SimpleDateFormat("yyyy-MM-dd").parse("1961-01-20")));
        assertThat(
            america.getPresidents().get(0).getInOfficeTo(),
            is(LocalDateTime.of(1963, 11, 22, 0, 0)));
        assertThat(america.getPresidents().get(0).getNumberOfDaysInOffice(), comparesEqualTo(1036));

        assertThat(america.getPresidents().get(10).getName(), is("Donald Trump"));
        assertThat(america.getPresidents().get(10).getDateOfBirth(), is(LocalDate.of(1946, 6, 14)));
        assertThat(america.getPresidents().get(10).getStateOfBirth(), is("New York"));
        assertThat(
            america.getPresidents().get(10).getInOfficeFrom(),
            is(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20")));
        assertThat(america.getPresidents().get(10).getInOfficeTo(), nullValue());
        assertThat(america.getPresidents().get(10).getNumberOfDaysInOffice(), is(0));
    }

    @Test
    public void testBeanMappingForEnum() throws ParseException {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        List<PresidentEnum> presidents = bean.listOf("presidents", PresidentEnum.class);

        System.out.println(presidents);

        // check that enum and other values are properly loaded
        assertThat(presidents.get(0).getName(), is("John F. Kennedy"));
        assertThat(presidents.get(0).getDateOfBirth(), is(LocalDate.of(1917, 5, 29)));
        assertThat(presidents.get(0).getStateOfBirth(), is(States.Massachusetts));
        assertThat(
            presidents.get(0).getInOfficeFrom(),
            is(new SimpleDateFormat("yyyy-MM-dd").parse("1961-01-20")));
        assertThat(
            presidents.get(0).getInOfficeTo(),
            is(LocalDateTime.of(1963, 11, 22, 0, 0)));
        assertThat(presidents.get(0).getNumberOfDaysInOffice(), comparesEqualTo(1036));

        // check that non-enum values are ignored as null without any exception
        assertThat(presidents.get(10).getName(), is("Donald Trump"));
        assertThat(presidents.get(10).getDateOfBirth(), is(LocalDate.of(1946, 6, 14)));
        assertThat(presidents.get(10).getStateOfBirth(), is(nullValue()));
        assertThat(
            presidents.get(10).getInOfficeFrom(),
            is(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20")));
        assertThat(
            presidents.get(10).getInOfficeTo(),
            is(nullValue()));
        assertThat(presidents.get(10).getNumberOfDaysInOffice(), comparesEqualTo(0));
    }

    @Test
    public void testBigData() {
        // InputStream in =
        // XlBeanReaderTest.class.getResourceAsStream("TestBook_bigdata.xlsx");
        File file = new File(XlBeanReaderTest.class.getResource("TestBook_bigdata.xlsx").getFile());
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(file);

        System.out.println(bean);
    }

    @Test
    public void testIndex() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_bigdata_index.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList list = bean.list("bigtable");
        @SuppressWarnings({ "rawtypes", "serial", "unchecked" })
        Map<String, String> conditionMap = new HashMap() {
            {
                put("aaa", "2499.0");
            }
        };
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            list.findByIndex(conditionMap);
        }
        System.out.println(String.format("with index :-> %d", (System.currentTimeMillis() - time)));
        time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            list.find(conditionMap);
        }
        System.out.println(String.format("without index :-> %d", (System.currentTimeMillis() - time)));
        System.out.println();
    }

    @Test
    public void testConvertFromObjectToXlBean() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_bigdata_index.xlsx");

        try (Workbook wb = WorkbookFactory.create(FileUtil.copyToInputStream(in))) {

            DefinitionLoader<XlWorkbook> definitionLoader = new ExcelR1C1DefinitionLoader();
            definitionLoader.initialize(wb);
            DefinitionRepository definitions = definitionLoader.load();

            XlBean bean = new XlBean();
            bean.set("definitions", definitions);

        } catch (InvalidFormatException | EncryptedDocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCommentDefinitionReader() throws ParseException {
        // XlBeanReader reader = new XlBeanReader(){
        // @Override
        // protected DefinitionLoader<?> createDefinitionLoader(Object definitionSource)
        // {
        // return new ExcelCommentDefinitionLoader(wrap((Workbook)definitionSource));
        // }
        // };
        XlBeanReader reader = new XlBeanReader(new ExcelCommentDefinitionLoader());
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents_comment.xlsx");
        XlBean bean = reader.read(in);

        Country america = bean.of(Country.class);

        assertThat(america.getName(), is("United States of America"));
        assertThat(america.getStats().getTotalArea(), comparesEqualTo(9833520L));
        assertThat(america.getStats().getGdp(), comparesEqualTo(new BigDecimal("18558000000000000")));

        assertThat(america.getPresidents().get(0).getName(), is("John F. Kennedy"));
        assertThat(america.getPresidents().get(0).getDateOfBirth(), is(LocalDate.of(1917, 5, 29)));
        assertThat(america.getPresidents().get(0).getStateOfBirth(), is("Massachusetts"));
        assertThat(
            america.getPresidents().get(0).getInOfficeFrom(),
            is(new SimpleDateFormat("yyyy-MM-dd").parse("1961-1-20")));
        assertThat(
            america.getPresidents().get(0).getInOfficeTo(),
            is(LocalDateTime.of(1963, 11, 22, 0, 0)));
        assertThat(america.getPresidents().get(0).getNumberOfDaysInOffice(), comparesEqualTo(1036));

        assertThat(america.getPresidents().get(10).getName(), is("Donald Trump"));
        assertThat(america.getPresidents().get(10).getDateOfBirth(), is(LocalDate.of(1946, 6, 14)));
        assertThat(america.getPresidents().get(10).getStateOfBirth(), is("New York"));
        assertThat(
            america.getPresidents().get(10).getInOfficeFrom(),
            is(new SimpleDateFormat("yyyy-MM-dd").parse("2017-1-20")));
        assertThat(america.getPresidents().get(10).getInOfficeTo(), nullValue());
        assertThat(america.getPresidents().get(10).getNumberOfDaysInOffice(), is(0));
    }

    @Test
    public void testIndexedListColumn() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_IndexedListColumn.xlsx");
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        assertThat(bean.list("tests").get(0).list("bbbb").size(), is(6));
        assertThat(bean.list("tests").get(0).list("bbbb").get(0).value("key"), is("key1"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(0).value("value"), is("1.0"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(1).value("key"), is("key2"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(1).value("value"), is("2.0"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(2).value("key"), is("key3"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(2).value("value"), is("3.0"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(3).value("key"), is("key4"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(3).value("value"), is("4.0"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(4).value("key"), is("key5"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(4).value("value"), is("5.0"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(5).value("key"), is("key6"));
        assertThat(bean.list("tests").get(0).list("bbbb").get(5).value("value"), is("6.0"));

        assertThat(bean.list("tests").get(1).list("bbbb").size(), is(5));
        assertThat(bean.list("tests").get(1).list("bbbb").get(0).value("key"), is("key1"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(0).value("value"), is("1.0"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(1).value("key"), is("key2"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(1).value("value"), is("2.0"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(2).value("key"), is("key3"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(2).value("value"), is("3.0"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(3).value("key"), is("key4"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(3).value("value"), is("4.0"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(4).value("key"), is("key5"));
        assertThat(bean.list("tests").get(1).list("bbbb").get(4).value("value"), is("5.0"));

        assertThat(bean.list("tests").get(2).list("bbbb").size(), is(5));
        assertThat(bean.list("tests").get(2).list("bbbb").get(0).value("key"), is("key1"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(0).value("value"), is("1.0"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(1), is(nullValue()));
        assertThat(bean.list("tests").get(2).list("bbbb").get(2).value("key"), is("key3"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(2).value("value"), is("3.0"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(3).value("key"), is("key4"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(3).value("value"), is("4.0"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(4).value("key"), is("key5"));
        assertThat(bean.list("tests").get(2).list("bbbb").get(4).value("value"), is("5.0"));

        assertThat(bean.list("tests").get(3).list("bbbb").size(), is(2));
        assertThat(bean.list("tests").get(3).list("bbbb").get(0).value("key"), is("key1"));
        assertThat(bean.list("tests").get(3).list("bbbb").get(0).value("value"), is("1.0"));
        assertThat(bean.list("tests").get(3).list("bbbb").get(1).value("key"), is("key2"));
        assertThat(bean.list("tests").get(3).list("bbbb").get(1).value("value"), is("2.0"));

        assertThat(bean.list("tests").get(4).list("bbbb").size(), is(1));
        assertThat(bean.list("tests").get(4).list("bbbb").get(0).value("key"), is(nullValue()));
        assertThat(bean.list("tests").get(4).list("bbbb").get(0).value("value"), is("1.0"));

        assertThat(bean.list("tests").get(5).list("bbbb").size(), is(6));
        assertThat(bean.list("tests").get(5).list("bbbb").get(0), is(nullValue()));
        assertThat(bean.list("tests").get(5).list("bbbb").get(1), is(nullValue()));
        assertThat(bean.list("tests").get(5).list("bbbb").get(2), is(nullValue()));
        assertThat(bean.list("tests").get(5).list("bbbb").get(3), is(nullValue()));
        assertThat(bean.list("tests").get(5).list("bbbb").get(4), is(nullValue()));
        assertThat(bean.list("tests").get(5).list("bbbb").get(5).value("key"), is("key6"));
        assertThat(bean.list("tests").get(5).list("bbbb").get(5).value("value"), is("6.0"));

        assertThat(bean.list("tests").get(6).list("bbbb").size(), is(1));
        assertThat(bean.list("tests").get(6).list("bbbb").get(0).value("key"), is("key1"));
        assertThat(bean.list("tests").get(6).list("bbbb").get(0).value("value"), is(nullValue()));
    }

    @Test
    public void testReadingSheetWithErrorCell() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_errorcell.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(bean.list("error").get(0).get("formulaError"), is("Before Formula error"));
        assertThat(bean.list("error").get(0).get("refError"), is("Before Ref error"));
        assertThat(bean.list("error").get(0).get("numberError"), is("Before Num error"));

        assertThat(bean.list("error").get(1).get("formulaError"), is(nullValue()));
        assertThat(bean.list("error").get(1).get("refError"), is("Before Ref error"));
        assertThat(bean.list("error").get(1).get("numberError"), is("Before Num error"));

        assertThat(bean.list("error").get(2).get("formulaError"), is("After Formula error"));
        assertThat(bean.list("error").get(2).get("refError"), is(nullValue()));
        assertThat(bean.list("error").get(2).get("numberError"), is("Before Num error"));

        assertThat(bean.list("error").get(3).get("formulaError"), is("After Formula error"));
        assertThat(bean.list("error").get(3).get("refError"), is("After Ref error"));
        assertThat(bean.list("error").get(3).get("numberError"), is(nullValue()));

        assertThat(bean.list("error").get(4).get("formulaError"), is("After Formula error"));
        assertThat(bean.list("error").get(4).get("refError"), is("After Ref error"));
        assertThat(bean.list("error").get(4).get("numberError"), is("After Num error"));

        assertThat(bean.list("error").size(), is(5));
    }

    @Test
    public void testBeanInBean() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_beaninbean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(bean.list("format").get(0).get("aaa"), is("111.0"));
        assertThat(bean.list("format").get(0).bean("bbb").get("b1"), is("222.0"));
        assertThat(bean.list("format").get(0).bean("bbb").bean("b2").get("b3"), is("333.0"));
        assertThat(bean.list("format").get(0).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(1).get("aaa"), is("111.0"));
        assertThat(bean.list("format").get(1).bean("bbb").get("b1"), is("222.0"));
        assertThat(bean.list("format").get(1).bean("bbb").bean("b2"), is(nullValue()));
        assertThat(bean.list("format").get(1).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(2).get("aaa"), is("111.0"));
        assertThat(bean.list("format").get(2).bean("bbb").get("b1"), is(nullValue()));
        assertThat(bean.list("format").get(2).bean("bbb").bean("b2").get("b3"), is("333.0"));
        assertThat(bean.list("format").get(2).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(3).get("aaa"), is("111.0"));
        assertThat(bean.list("format").get(3).bean("bbb"), is(nullValue()));
        assertThat(bean.list("format").get(3).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(4).get("aaa"), is(nullValue()));
        assertThat(bean.list("format").get(4).bean("bbb").get("b1"), is("222.0"));
        assertThat(bean.list("format").get(4).bean("bbb").bean("b2").get("b3"), is("333.0"));
        assertThat(bean.list("format").get(4).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(5).get("aaa"), is(nullValue()));
        assertThat(bean.list("format").get(5).bean("bbb").get("b1"), is("222.0"));
        assertThat(bean.list("format").get(5).bean("bbb").bean("b2"), is(nullValue()));
        assertThat(bean.list("format").get(5).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(6).get("aaa"), is(nullValue()));
        assertThat(bean.list("format").get(6).bean("bbb").get("b1"), is(nullValue()));
        assertThat(bean.list("format").get(6).bean("bbb").bean("b2").get("b3"), is("333.0"));
        assertThat(bean.list("format").get(6).bean("ccc"), is(nullValue()));

        assertThat(bean.list("format").get(7).bean("ccc").bean("c1").bean("c2").get("c3"), is("444.0"));

    }

    @Test
    public void testSidewayList() {

        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_sidewaylist.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(bean.list("data").get(0).bean("bean").list("list").get(0).value("val"), is("1.0"));
        assertThat(bean.list("data").get(0).bean("bean").list("list").get(1).value("val"), is("2.0"));
        assertThat(bean.list("data").get(1).bean("bean").list("list").get(0), is(nullValue()));
        assertThat(bean.list("data").get(1).bean("bean").list("list").get(1).value("val"), is("3.0"));

        bean.listOf("data", TestBean.class);

    }
}
