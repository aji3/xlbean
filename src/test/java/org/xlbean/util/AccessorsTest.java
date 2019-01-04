package org.xlbean.util;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.XlBeanImpl;
import org.xlbean.reader.XlBeanReader;
import org.xlbean.reader.XlBeanReaderTest;

public class AccessorsTest {

    @Test
    public void getValue() {
        InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_beaninbean.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        assertThat(Accessors.getValue("format[0].id", bean), is("1.0"));
        assertThat(Accessors.getValue("format[0].aaa", bean), is("111.0"));
        assertThat(Accessors.getValue("format[0].bbb.b1", bean), is("222.0"));
        assertThat(Accessors.getValue("format[0].bbb.b2.b3", bean), is("333.0"));
        assertThat(Accessors.getValue("format[0].ccc.c1.c2.c3", bean), is(nullValue()));

        assertThat(Accessors.getValue("format[1].id", bean), is("2.0"));
        assertThat(Accessors.getValue("format[1].aaa", bean), is("111.0"));
        assertThat(Accessors.getValue("format[1].bbb.b1", bean), is("222.0"));
        assertThat(Accessors.getValue("format[1].bbb.b2.b3", bean), is(nullValue()));
        assertThat(Accessors.getValue("format[1].ccc.c1.c2.c3", bean), is(nullValue()));

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
        Accessors.setValue("aaa", "aaa", target);
        Accessors.setValue("bbb.b1", "b1", target);
        Accessors.setValue("bbb.b2", "b2", target);
        Accessors.setValue("ccc.list[2]", "c2", target);
        Accessors.setValue("ccc.list[4]", "c4", target);
        Accessors.setValue("list[1]", "l2", target);
        Accessors.setValue("list[0]", "l1", target);
        Accessors.setValue("listinlist[1][2]", "1-2", target);
        Accessors.setValue("listinlist[1][4]", "1-4", target);
        Accessors.setValue("listinlist[2][0]", "2-0", target);
        Accessors.setValue("listinlist[2][2]", "2-2", target);
        Accessors.setValue("beaninlist[1].bbb.ccc", "bil1", target);
        Accessors.setValue("beaninlist[0].bbb.ccc", "bil0c", target);
        Accessors.setValue("beaninlist[0].bbb.ddd", "bil0d", target);

        System.out.println(target);

        assertThat(Accessors.getValue("aaa", target), is("aaa"));
        assertThat(Accessors.getValue("bbb.b1", target), is("b1"));
        assertThat(Accessors.getValue("bbb.b2", target), is("b2"));
        assertThat(Accessors.getValue("ccc.list[2]", target), is("c2"));
        assertThat(Accessors.getValue("ccc.list[4]", target), is("c4"));
        assertThat(Accessors.getValue("list[1]", target), is("l2"));
        assertThat(Accessors.getValue("list[0]", target), is("l1"));
        assertThat(Accessors.getValue("listinlist[1][2]", target), is("1-2"));
        assertThat(Accessors.getValue("listinlist[1][4]", target), is("1-4"));
        assertThat(Accessors.getValue("listinlist[2][0]", target), is("2-0"));
        assertThat(Accessors.getValue("listinlist[2][2]", target), is("2-2"));
        assertThat(Accessors.getValue("beaninlist[1].bbb.ccc", target), is("bil1"));
        assertThat(Accessors.getValue("beaninlist[0].bbb.ccc", target), is("bil0c"));
        assertThat(Accessors.getValue("beaninlist[0].bbb.ddd", target), is("bil0d"));

        XlBeanFactory.setInstance(new XlBeanFactory());

    }

    @Test
    public void setValue() {

        XlBean bean = new XlBeanImpl();
        Accessors.setValue("aaa", "testValue", bean);
        Accessors.setValue("bbb.b1", "bbb.b1test", bean);
        Accessors.setValue("bbb.b2.b3", "bbb.b2.b3test", bean);
        Accessors.setValue("bbb.b2.b4", "bbb.b2.b4test", bean);
        Accessors.setValue("ccc[0].c1", "ccc[0].c1test", bean);
        Accessors.setValue("ccc[0].c2", "ccc[0].c2test", bean);
        Accessors.setValue("ccc[1].c1", "ccc[1].c1test", bean);
        Accessors.setValue("ccc[1].c2", "ccc[1].c2test", bean);

        System.out.println(bean);
        assertThat(Accessors.getValue("aaa", bean), is("testValue"));
        assertThat(Accessors.getValue("bbb.b1", bean), is("bbb.b1test"));
        assertThat(Accessors.getValue("bbb.b2.b3", bean), is("bbb.b2.b3test"));
        assertThat(Accessors.getValue("bbb.b2.b4", bean), is("bbb.b2.b4test"));
        assertThat(Accessors.getValue("ccc[0].c1", bean), is("ccc[0].c1test"));
        assertThat(Accessors.getValue("ccc[0].c2", bean), is("ccc[0].c2test"));
        assertThat(Accessors.getValue("ccc[1].c1", bean), is("ccc[1].c1test"));
        assertThat(Accessors.getValue("ccc[1].c2", bean), is("ccc[1].c2test"));

    }

    @Test
    public void testNoNullValueFalse() {
        Accessors.setInstance(new Accessors(false));

        XlBean bean = new XlBeanImpl();
        Accessors.setValue("aaa.bbb.ccc", "somevalue", bean);
        Accessors.setValue("aaa2.bbb.ccc", "somevalue2", bean);
        System.out.println(bean);
        assertThat(Accessors.getValue("aaa.bbb.ccc", bean), is("somevalue"));
        assertThat(Accessors.getValue("aaa2.bbb.ccc", bean), is("somevalue2"));

        Accessors.setValue("aaa.bbb.ccc", null, bean);
        System.out.println(bean);
        assertThat(Accessors.getValue("aaa.bbb.ccc", bean), is(nullValue()));
        assertThat(bean.containsKey("aaa"), is(true));
        assertThat(bean.containsKey("aaa2"), is(true));

        Accessors.setInstance(new Accessors());
    }

    @Test
    public void testNoNullValue() {
        XlBean bean = new XlBeanImpl();
        Accessors.setValue("aaa.bbb.ccc", "somevalue", bean);
        Accessors.setValue("aaa2.bbb.ccc", "somevalue2", bean);
        Accessors.setValue("list[0].bbb.ccc", "somevalue0-1", bean);
        System.out.println(bean);
        assertThat(Accessors.getValue("aaa.bbb.ccc", bean), is("somevalue"));
        assertThat(Accessors.getValue("aaa2.bbb.ccc", bean), is("somevalue2"));
        assertThat(Accessors.getValue("list[0].bbb.ccc", bean), is("somevalue0-1"));

        Accessors.setValue("aaa.bbb.ccc", null, bean);
        Accessors.setValue("list[0].bbb.ccc", null, bean);
        System.out.println(bean);
        assertThat(Accessors.getValue("aaa.bbb.ccc", bean), is(nullValue()));
        assertThat(bean.containsKey("aaa"), is(false));
        assertThat(bean.containsKey("aaa2"), is(true));

    }
}
