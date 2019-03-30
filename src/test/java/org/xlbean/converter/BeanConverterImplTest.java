package org.xlbean.converter;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.xlbean.XlBean;
import org.xlbean.converter.impl.BeanConverterImpl;
import org.xlbean.reader.XlBeanReader;

@SuppressWarnings("rawtypes")
public class BeanConverterImplTest {

    @Test
    public void toMap() {
        BeanConverterImpl converter = new BeanConverterImpl();

        List<String> list = new ArrayList<>();
        list.add("test");

        List<XlBean> ret = (List) converter.toMap(list);
        XlBean bean = ret.get(0);

        assertThat(bean.get("value"), is("test"));
    }

    @Test
    public void toObject() {

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(BeanConverterImplTest.class.getResourceAsStream("TestBook_BeanConverter.xlsx"));

        BeanConverterImpl converter = new BeanConverterImpl();

        C1 c1 = converter.toBean(bean, C1.class);
        System.out.println(c1);

        assertThat(c1.getList().get(0), is("555"));
        assertThat(c1.getList().get(1), is("666"));
        assertThat(c1.getIntList().get(0), is(555));
        assertThat(c1.getIntList().get(1), is(666));
        assertThat(c1.getMap().get("aaa"), is("333"));
        assertThat(c1.getMap().get("bbb"), is("444"));
        assertThat(c1.getTestMap().get("aaa"), is("111"));
        assertThat(c1.getTestMap().get("bbb"), is("222"));

        C2 c2 = converter.toBean(bean, C2.class);
        System.out.println(c2);

        assertThat(c2.getList().get(0), instanceOf(C3.class));
        assertThat(c2.getList().get(1), instanceOf(C3.class));
        assertThat(c2.getIntList().get(0), is("555"));
        assertThat(c2.getIntList().get(1), is("666"));

        C4 c4 = converter.toBean(bean, C4.class);
        System.out.println(c4);

        Map<String, File> test = new HashMap<>();
        test.put("file", new File("test"));
        C5 c5 = converter.toBean(test, C5.class);
        assertThat(c5.getFile().getName(), is("test"));
        System.out.println(c5);
    }

    public static class C1 {
        private List list;
        private List<Integer> intList;
        private Map map;
        private Map<String, ?> testMap;

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }

        public List<Integer> getIntList() {
            return intList;
        }

        public void setIntList(List<Integer> intList) {
            this.intList = intList;
        }

        @Override
        public String toString() {
            return "C1 [list=" + list + ", intList=" + intList + ", map=" + map + ", testMap=" + testMap + "]";
        }

        public Map<String, ?> getTestMap() {
            return testMap;
        }

        public void setTestMap(Map<String, ?> testMap) {
            this.testMap = testMap;
        }
    }

    public static class C2 {
        private List<C3> list;
        private List<Object> intList;
        private Map<Object, C3> map;

        public List<C3> getList() {
            return list;
        }

        public void setList(List<C3> list) {
            this.list = list;
        }

        public List<Object> getIntList() {
            return intList;
        }

        public void setIntList(List<Object> intList) {
            this.intList = intList;
        }

        public Map<Object, C3> getMap() {
            return map;
        }

        public void setMap(Map<Object, C3> map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return "C2 [list=" + list + ", intList=" + intList + ", map=" + map + "]";
        }

    }

    public static class C3 {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        @Override
        public String toString() {
            return "C3 [test=" + test + "]";
        }
    }

    public static class C4 {
        private int[] list;
        private Integer[] intList;

        public int[] getList() {
            return list;
        }

        public void setList(int[] list) {
            this.list = list;
        }

        public Integer[] getIntList() {
            return intList;
        }

        public void setIntList(Integer[] intList) {
            this.intList = intList;
        }

        @Override
        public String toString() {
            return "C4 [list=" + Arrays.toString(list) + ", intList=" + Arrays.toString(intList) + "]";
        }

    }

    public static class C5 {
        private File file;

        @Override
        public String toString() {
            return "C5 [file=" + file + "]";
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

    }
}
