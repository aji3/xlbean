package org.xlbean.converter;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;
import org.xlbean.XlBean;

public class ValueConverterFactoryTest {

    private static class TestValueConverter extends ValueConverterImpl {}

    @Test
    public void setInstance() throws Exception {
        ValueConverterFactory.setInstance(
            new ValueConverterFactory() {
                @Override
                public ValueConverter createValueConverter() {
                    return new TestValueConverter();
                }
            });

        XlBean bean = new XlBean();

        Field f = XlBean.class.getDeclaredField("converter");
        f.setAccessible(true);
        Field f2 = BeanConverterImpl.class.getDeclaredField("converter");
        f2.setAccessible(true);

        assertThat(f2.get(f.get(bean)), is(instanceOf(TestValueConverter.class)));
    }
}
