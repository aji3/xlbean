package org.xlbean.converter;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ValueConverterFactoryTest {

    @Test
    public void setInstance() throws Exception {
        ValueConverterFactory.setInstance(
            new ValueConverterFactory() {
                @Override
                public List<ValueConverter<?>> getValueConverters() {
                    List<ValueConverter<?>> list = new ArrayList<>();
                    list.addAll(super.getValueConverters());
                    list.add(new TestValueConverter());
                    return list;
                }
            });

        ValueConverter<?> conv = ValueConverters.getValueConverterByName("test");

        assertThat(conv, is(instanceOf(TestValueConverter.class)));
    }

    private static class TestValueConverter implements ValueConverter<String> {

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public boolean canConvert(Class<?> clazz) {
            return false;
        }

        @Override
        public String toString(Object obj) {
            return null;
        }

        @Override
        public String toObject(String value, Class<?> valueClass) {
            return null;
        }

    }
}
