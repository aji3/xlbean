package org.xlbean.util;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderTest;

public class FieldAccessHelperTest {

    @Test
    public void getValue() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_beaninbean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(FieldAccessHelper.getValue("format[0].id", bean), is("1.0"));
        assertThat(FieldAccessHelper.getValue("format[0].aaa", bean), is("111.0"));
        assertThat(FieldAccessHelper.getValue("format[0].bbb.b1", bean), is("222.0"));
        assertThat(FieldAccessHelper.getValue("format[0].bbb.b2.b3", bean), is("333.0"));
        assertThat(FieldAccessHelper.getValue("format[0].ccc.c1.c2.c3", bean), is(nullValue()));

        assertThat(FieldAccessHelper.getValue("format[1].id", bean), is("2.0"));
        assertThat(FieldAccessHelper.getValue("format[1].aaa", bean), is("111.0"));
        assertThat(FieldAccessHelper.getValue("format[1].bbb.b1", bean), is("222.0"));
        assertThat(FieldAccessHelper.getValue("format[1].bbb.b2.b3", bean), is(nullValue()));
        assertThat(FieldAccessHelper.getValue("format[1].ccc.c1.c2.c3", bean), is(nullValue()));

    }

    @Test
    public void setValue() {

        XlBean bean = new XlBean();
        FieldAccessHelper.setValue("aaa", "testValue", bean);
        FieldAccessHelper.setValue("bbb.b1", "bbb.b1test", bean);
        FieldAccessHelper.setValue("bbb.b2.b3", "bbb.b2.b3test", bean);
        FieldAccessHelper.setValue("bbb.b2.b4", "bbb.b2.b4test", bean);
        FieldAccessHelper.setValue("ccc[0].c1", "ccc[0].c1test", bean);
        FieldAccessHelper.setValue("ccc[0].c2", "ccc[0].c2test", bean);
        FieldAccessHelper.setValue("ccc[1].c1", "ccc[1].c1test", bean);
        FieldAccessHelper.setValue("ccc[1].c2", "ccc[1].c2test", bean);

        System.out.println(bean);
        assertThat(FieldAccessHelper.getValue("aaa", bean), is("testValue"));
        assertThat(FieldAccessHelper.getValue("bbb.b1", bean), is("bbb.b1test"));
        assertThat(FieldAccessHelper.getValue("bbb.b2.b3", bean), is("bbb.b2.b3test"));
        assertThat(FieldAccessHelper.getValue("bbb.b2.b4", bean), is("bbb.b2.b4test"));
        assertThat(FieldAccessHelper.getValue("ccc[0].c1", bean), is("ccc[0].c1test"));
        assertThat(FieldAccessHelper.getValue("ccc[0].c2", bean), is("ccc[0].c2test"));
        assertThat(FieldAccessHelper.getValue("ccc[1].c1", bean), is("ccc[1].c1test"));
        assertThat(FieldAccessHelper.getValue("ccc[1].c2", bean), is("ccc[1].c2test"));

    }
}
