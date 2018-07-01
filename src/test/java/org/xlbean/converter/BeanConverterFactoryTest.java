package org.xlbean.converter;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.XlBeanImpl;

public class BeanConverterFactoryTest {

    private static class TestBeanConverter extends BeanConverterImpl {}

    @Test
    public void setInstance() throws Exception {
        BeanConverterFactory.setInstance(
            new BeanConverterFactory() {
                @Override
                public BeanConverter createBeanConverter() {
                    return new TestBeanConverter();
                }
            });

        XlBean bean = new XlBeanImpl();

        Field f = XlBeanImpl.class.getDeclaredField("converter");
        f.setAccessible(true);

        assertThat(f.get(bean), is(instanceOf(TestBeanConverter.class)));
    }
}
