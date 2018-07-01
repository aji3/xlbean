
package org.xlbean.data.value.table;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.util.FieldAccessHelper;

public class TableValueLoaderTest {

    @Test
    public void listToPropOption() {
        InputStream in = TableValueLoaderTest.class.getResourceAsStream("TestBook_definitionList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(bean.get("xldef1"), is(instanceOf(String.class)));
        assertThat(bean.get("xldef3"), is(instanceOf(XlBean.class)));
        assertThat(bean.get("xldef4"), is(instanceOf(ArrayList.class)));

        assertThat(FieldAccessHelper.getValue("xldef1", bean), is("12345.0"));
        assertThat(FieldAccessHelper.getValue("xldef2", bean), is("aaaaaa"));
        assertThat(FieldAccessHelper.getValue("xldef3.xxx", bean), is("xxxxx"));
        assertThat(FieldAccessHelper.getValue("xldef3.yyy", bean), is("yyyyy"));
        assertThat(FieldAccessHelper.getValue("xldef3.zzz", bean), is("zzzzz"));
        assertThat(FieldAccessHelper.getValue("xldef4[0].aaa", bean), is("aaa0"));
        assertThat(FieldAccessHelper.getValue("xldef4[0].bbb", bean), is("bbb0"));
        assertThat(FieldAccessHelper.getValue("xldef4[1].aaa", bean), is("aaa1"));
        assertThat(FieldAccessHelper.getValue("xldef4[1].bbb", bean), is("bbb1"));
        assertThat(FieldAccessHelper.getValue("xldef4[2].aaa", bean), is("aaa2"));
        assertThat(FieldAccessHelper.getValue("xldef4[2].bbb", bean), is("bbb2"));

        assertThat(bean.get("obj"), is(instanceOf(XlBean.class)));
        assertThat(bean.bean("obj").get("xldef1"), is(instanceOf(String.class)));
        assertThat(bean.bean("obj").get("xldef3"), is(instanceOf(XlBean.class)));
        assertThat(bean.bean("obj").get("xldef4"), is(instanceOf(ArrayList.class)));

        assertThat(FieldAccessHelper.getValue("obj.xldef1", bean), is("12345.0"));
        assertThat(FieldAccessHelper.getValue("obj.xldef2", bean), is("aaaaaa"));
        assertThat(FieldAccessHelper.getValue("obj.xldef3.xxx", bean), is("xxxxx"));
        assertThat(FieldAccessHelper.getValue("obj.xldef3.yyy", bean), is("yyyyy"));
        assertThat(FieldAccessHelper.getValue("obj.xldef3.zzz", bean), is("zzzzz"));
        assertThat(FieldAccessHelper.getValue("obj.xldef4[0].aaa", bean), is("aaa0"));
        assertThat(FieldAccessHelper.getValue("obj.xldef4[0].bbb", bean), is("bbb0"));
        assertThat(FieldAccessHelper.getValue("obj.xldef4[1].aaa", bean), is("aaa1"));
        assertThat(FieldAccessHelper.getValue("obj.xldef4[1].bbb", bean), is("bbb1"));
        assertThat(FieldAccessHelper.getValue("obj.xldef4[2].aaa", bean), is("aaa2"));
        assertThat(FieldAccessHelper.getValue("obj.xldef4[2].bbb", bean), is("bbb2"));
    }

}
