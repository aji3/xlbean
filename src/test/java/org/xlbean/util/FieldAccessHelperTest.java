package org.xlbean.util;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Test case added for
     * #16_Refactor_FieldAccessHelper_to_able_to_access_to_any_json_representable_format.
     * Now FieldAccessHelper need to support any json representable format.
     */
    @Test
    public void getValue_additional() {

        XlBeanFactory.setInstance(new SimpleXlBeanFactory());
        Map<String, Object> target = new HashMap<>();
        FieldAccessHelper.setValue("aaa", "aaa", target);
        FieldAccessHelper.setValue("bbb.b1", "b1", target);
        FieldAccessHelper.setValue("bbb.b2", "b2", target);
        FieldAccessHelper.setValue("ccc.list[2]", "c2", target);
        FieldAccessHelper.setValue("ccc.list[4]", "c4", target);
        FieldAccessHelper.setValue("list[1]", "l2", target);
        FieldAccessHelper.setValue("list[0]", "l1", target);
        FieldAccessHelper.setValue("listinlist[1][2]", "1-2", target);
        FieldAccessHelper.setValue("listinlist[1][4]", "1-4", target);
        FieldAccessHelper.setValue("listinlist[2][0]", "2-0", target);
        FieldAccessHelper.setValue("listinlist[2][2]", "2-2", target);
        FieldAccessHelper.setValue("beaninlist[1].bbb.ccc", "bil1", target);
        FieldAccessHelper.setValue("beaninlist[0].bbb.ccc", "bil0c", target);
        FieldAccessHelper.setValue("beaninlist[0].bbb.ddd", "bil0d", target);

        System.out.println(target);

        assertThat(FieldAccessHelper.getValue("aaa", target), is("aaa"));
        assertThat(FieldAccessHelper.getValue("bbb.b1", target), is("b1"));
        assertThat(FieldAccessHelper.getValue("bbb.b2", target), is("b2"));
        assertThat(FieldAccessHelper.getValue("ccc.list[2]", target), is("c2"));
        assertThat(FieldAccessHelper.getValue("ccc.list[4]", target), is("c4"));
        assertThat(FieldAccessHelper.getValue("list[1]", target), is("l2"));
        assertThat(FieldAccessHelper.getValue("list[0]", target), is("l1"));
        assertThat(FieldAccessHelper.getValue("listinlist[1][2]", target), is("1-2"));
        assertThat(FieldAccessHelper.getValue("listinlist[1][4]", target), is("1-4"));
        assertThat(FieldAccessHelper.getValue("listinlist[2][0]", target), is("2-0"));
        assertThat(FieldAccessHelper.getValue("listinlist[2][2]", target), is("2-2"));
        assertThat(FieldAccessHelper.getValue("beaninlist[1].bbb.ccc", target), is("bil1"));
        assertThat(FieldAccessHelper.getValue("beaninlist[0].bbb.ccc", target), is("bil0c"));
        assertThat(FieldAccessHelper.getValue("beaninlist[0].bbb.ddd", target), is("bil0d"));

        XlBeanFactory.setInstance(new XlBeanFactory());

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
