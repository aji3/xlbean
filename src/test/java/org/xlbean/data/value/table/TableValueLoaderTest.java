
package org.xlbean.data.value.table;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.ArrayList;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.util.Accessors;

public class TableValueLoaderTest {

    @Test
    public void toBeanOption() {
        InputStream in = TableValueLoaderTest.class.getResourceAsStream("TestBook_definitionList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(bean.get("xldef1"), is(instanceOf(String.class)));
        assertThat(bean.get("xldef3"), is(instanceOf(XlBean.class)));
        assertThat(bean.get("xldef4"), is(instanceOf(ArrayList.class)));

        assertThat(Accessors.getValue("xldef1", bean), is("12345.0"));
        assertThat(Accessors.getValue("xldef2", bean), is("aaaaaa"));
        assertThat(Accessors.getValue("xldef3.xxx", bean), is("xxxxx"));
        assertThat(Accessors.getValue("xldef3.yyy", bean), is("yyyyy"));
        assertThat(Accessors.getValue("xldef3.zzz", bean), is("zzzzz"));
        assertThat(Accessors.getValue("xldef4[0].aaa", bean), is("aaa0"));
        assertThat(Accessors.getValue("xldef4[0].bbb", bean), is("bbb0"));
        assertThat(Accessors.getValue("xldef4[1].aaa", bean), is("aaa1"));
        assertThat(Accessors.getValue("xldef4[1].bbb", bean), is("bbb1"));
        assertThat(Accessors.getValue("xldef4[2].aaa", bean), is("aaa2"));
        assertThat(Accessors.getValue("xldef4[2].bbb", bean), is("bbb2"));

        System.out.println(bean.get("obj"));
        assertThat(bean.get("obj"), is(instanceOf(XlBean.class)));
        assertThat(bean.bean("obj").get("xldef1"), is(instanceOf(String.class)));
        assertThat(bean.bean("obj").get("xldef3"), is(instanceOf(XlBean.class)));
        assertThat(bean.bean("obj").get("xldef4"), is(instanceOf(ArrayList.class)));

        assertThat(Accessors.getValue("obj.xldef1", bean), is("12345.0"));
        assertThat(Accessors.getValue("obj.xldef2", bean), is("aaaaaa"));
        assertThat(Accessors.getValue("obj.xldef3.xxx", bean), is("xxxxx"));
        assertThat(Accessors.getValue("obj.xldef3.yyy", bean), is("yyyyy"));
        assertThat(Accessors.getValue("obj.xldef3.zzz", bean), is("zzzzz"));
        assertThat(Accessors.getValue("obj.xldef4[0].aaa", bean), is("aaa0"));
        assertThat(Accessors.getValue("obj.xldef4[0].bbb", bean), is("bbb0"));
        assertThat(Accessors.getValue("obj.xldef4[1].aaa", bean), is("aaa1"));
        assertThat(Accessors.getValue("obj.xldef4[1].bbb", bean), is("bbb1"));
        assertThat(Accessors.getValue("obj.xldef4[2].aaa", bean), is("aaa2"));
        assertThat(Accessors.getValue("obj.xldef4[2].bbb", bean), is("bbb2"));
    }

}
