package org.xlbean.converter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.XlList;
import org.xlbean.converter.impl.BeanConverterImpl;

public class BeanConverterImplTest {

    @Test
    public void toMap() {
        BeanConverterImpl converter = new BeanConverterImpl();

        List<String> list = new ArrayList<>();
        list.add("test");

        XlList ret = (XlList) converter.toMap(list);
        XlBean bean = ret.get(0);

        assertThat(bean.get("value"), is("test"));
    }
}
