package org.xlbean;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.HashMap;

import org.junit.Test;
import org.xlbean.reader.XlBeanReader;

public class XlListTest {

    @Test
    public void aggregate() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        XlList addresses = (XlList) bean.beans("addresses");
        XlList cities = addresses.aggregate("streets", "prefecture", "city");
        XlList prefectures = cities.aggregate("cities", "prefecture");

        System.out.println(prefectures);

        assertThat(prefectures.get(0).string("prefecture"), is("Tokyo-to"));

        assertThat(prefectures.get(0).beans("cities").get(0).string("city"), is("Chiyoda-ku"));
        assertThat(prefectures.get(0).beans("cities").get(1).string("city"), is("Chuo-ku"));

        assertThat(
            prefectures.get(0).beans("cities").get(0).beans("streets").get(0).string("street"),
            is("Iidabashi 1-1"));
        assertThat(
            prefectures.get(0).beans("cities").get(0).beans("streets").get(8).string("street"),
            is("Iidabashi 3-4"));

        assertThat(prefectures.get(1).string("prefecture"), is("Kanagawa-ken"));

        assertThat(prefectures.get(1).beans("cities").get(0).string("city"), is("Kawasaki-shi"));

        assertThat(
            prefectures.get(1).beans("cities").get(0).beans("streets").get(0).string("street"),
            is("Asano-cho 1-1"));
    }

    @Test
    public void aggregate_indexedTable() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        System.out.println(bean);

        XlList addressesIndexed = (XlList) bean.beans("addressesIndexed");
        XlList cities = addressesIndexed.aggregate("streets", "prefecture", "city");
        XlList prefectures = cities.aggregate("cities", "prefecture");

        System.out.println(prefectures);

        assertThat(prefectures.get(0).string("prefecture"), is("Tokyo-to"));

        assertThat(prefectures.get(0).beans("cities").get(0).string("city"), is("Chiyoda-ku"));
        assertThat(prefectures.get(0).beans("cities").get(1).string("city"), is("Chuo-ku"));

        assertThat(
            prefectures.get(0).beans("cities").get(0).beans("streets").get(0).string("street"),
            is("Iidabashi 1-1"));
        assertThat(
            prefectures.get(0).beans("cities").get(0).beans("streets").get(8).string("street"),
            is("Iidabashi 3-4"));

        assertThat(prefectures.get(1).string("prefecture"), is("Kanagawa-ken"));

        assertThat(prefectures.get(1).beans("cities").get(0).string("city"), is("Kawasaki-shi"));

        assertThat(
            prefectures.get(1).beans("cities").get(0).beans("streets").get(0).string("street"),
            is("Asano-cho 1-1"));
    }

    @SuppressWarnings("serial")
    @Test
    public void find_returnsnull_when_conditiondoesntmatch() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        XlBean found = addresses
            .find(
                new HashMap<String, String>() {
                    {
                        put("prefecture", "Hello");
                    }
                });

        assertThat(found, is(nullValue()));
    }

    @Test
    public void findAll() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        @SuppressWarnings("serial")
        XlList list = addresses
            .findAll(
                new HashMap<String, String>() {
                    {
                        put("prefecture", "Kanagawa-ken");
                    }
                });

        assertThat(list.size(), is(9));
    }

    @Test
    public void findAll_returnsblanklist_when_conditiondoesntmatch() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        @SuppressWarnings("serial")
        XlList list = addresses
            .findAll(
                new HashMap<String, String>() {
                    {
                        put("prefecture", "Hello");
                    }
                });

        assertThat(list.size(), is(0));
    }

    @Test
    public void findAll_returnsblanklist_when_conditionisnull() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        XlList list = addresses.findAll(null);

        assertThat(list.size(), is(0));
    }

    @Test
    public void find() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        XlBean address = addresses.find(3);

        assertThat(address.string("street"), is("Iidabashi 2-1"));
    }

    @SuppressWarnings("serial")
    @Test
    public void findByIndex_withIndexNameSpecified() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addressesMultiIndexed = (XlList) bean.beans("addressesMultiIndexed");
        XlBean address = addressesMultiIndexed
            .findByIndex(
                new HashMap<String, String>() {
                    {
                        put("id", "1.0");
                    }
                },
                "id");

        assertThat(address.string("street"), is("Iidabashi 1-1"));

        address = addressesMultiIndexed
            .findByIndex(
                new HashMap<String, String>() {
                    {
                        put("street", "Iidabashi 2-1");
                    }
                },
                "street");

        assertThat(address.string("street"), is("Iidabashi 2-1"));
    }

    @Test
    public void findByIndex_returnsNull_when_illegalIndexNameWasGiven() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addressesMultiIndexed = (XlList) bean.beans("addressesMultiIndexed");
        @SuppressWarnings("serial")
        XlBean address = addressesMultiIndexed
            .findByIndex(
                new HashMap<String, String>() {
                    {
                        put("id", "1.0");
                    }
                },
                "illegalIdName");

        assertThat(address, is(nullValue()));
    }

    @Test
    public void findByIndex_returnsnull_when_noindexnamewasspecifiedtomultiindexedtable() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addressesMultiIndexed = (XlList) bean.beans("addressesMultiIndexed");
        @SuppressWarnings("serial")
        XlBean address = addressesMultiIndexed
            .findByIndex(
                new HashMap<String, String>() {
                    {
                        put("test", "test");
                    }
                });

        assertThat(address, is(nullValue()));
    }

    @Test
    public void find_outofindex_tobe_null() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        XlBean address = addresses.find(300);

        assertThat(address, is(nullValue()));
    }

    @Test
    public void toMap() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");

        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        XlList addresses = (XlList) bean.beans("addresses");
        XlBean streets = addresses.toMap("street");

        assertThat(streets.bean("Tsukishima 1-3").string("prefecture"), is("Tokyo-to"));
    }
}
