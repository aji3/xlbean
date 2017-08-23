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
        
        XlList cities = bean.list("addresses").aggregate("streets", "prefecture", "city");
        XlList prefectures = cities.aggregate("cities", "prefecture");
        
        System.out.println(prefectures);

        assertThat(prefectures.get(0).value("prefecture"), is("Tokyo-to"));
        
        assertThat(prefectures.get(0).list("cities").get(0).value("city"), is("Chiyoda-ku"));
        assertThat(prefectures.get(0).list("cities").get(1).value("city"), is("Chuo-ku"));

        assertThat(prefectures.get(0).list("cities").get(0).list("streets").get(0).value("street"), is("Iidabashi 1-1"));
        assertThat(prefectures.get(0).list("cities").get(0).list("streets").get(8).value("street"), is("Iidabashi 3-4"));
        

        assertThat(prefectures.get(1).value("prefecture"), is("Kanagawa-ken"));

        assertThat(prefectures.get(1).list("cities").get(0).value("city"), is("Kawasaki-shi"));
        
        assertThat(prefectures.get(1).list("cities").get(0).list("streets").get(0).value("street"), is("Asano-cho 1-1"));
    }
    
    @Test
    public void aggregate_indexedTable() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        System.out.println(bean);
        
        XlList cities = bean.list("addressesIndexed").aggregate("streets", "prefecture", "city");
        XlList prefectures = cities.aggregate("cities", "prefecture");
        
        System.out.println(prefectures);

        assertThat(prefectures.get(0).value("prefecture"), is("Tokyo-to"));
        
        assertThat(prefectures.get(0).list("cities").get(0).value("city"), is("Chiyoda-ku"));
        assertThat(prefectures.get(0).list("cities").get(1).value("city"), is("Chuo-ku"));

        assertThat(prefectures.get(0).list("cities").get(0).list("streets").get(0).value("street"), is("Iidabashi 1-1"));
        assertThat(prefectures.get(0).list("cities").get(0).list("streets").get(8).value("street"), is("Iidabashi 3-4"));
        

        assertThat(prefectures.get(1).value("prefecture"), is("Kanagawa-ken"));

        assertThat(prefectures.get(1).list("cities").get(0).value("city"), is("Kawasaki-shi"));
        
        assertThat(prefectures.get(1).list("cities").get(0).list("streets").get(0).value("street"), is("Asano-cho 1-1"));
        
    }
    
    
    
    @SuppressWarnings("serial")
    @Test
    public void find_returnsnull_when_conditiondoesntmatch() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlBean found = bean.list("addresses").find(new HashMap<String, String>(){{put("prefecture", "Hello");}});
        
        assertThat(found, is(nullValue()));
    }

    @Test
    public void findAll() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        @SuppressWarnings("serial")
        XlList list = bean.list("addresses").findAll(new HashMap<String, String>(){{put("prefecture", "Kanagawa-ken");}});
        
        assertThat(list.size(), is(9));
    }

    @Test
    public void findAll_returnsblanklist_when_conditiondoesntmatch() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);

        @SuppressWarnings("serial")
        XlList list = bean.list("addresses").findAll(new HashMap<String, String>(){{put("prefecture", "Hello");}});
        
        assertThat(list.size(), is(0));
    }

    @Test
    public void findAll_returnsblanklist_when_conditionisnull() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlList list = bean.list("addresses").findAll(null);
        
        assertThat(list.size(), is(0));
    }

    @Test
    public void find() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlBean address = bean.list("addresses").find(3);
        
        assertThat(address.value("street"), is("Iidabashi 2-1"));
    }
    
    @SuppressWarnings("serial")
    @Test
    public void findByIndex_withIndexNameSpecified() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlBean address = bean.list("addressesMultiIndexed").findByIndex(new HashMap<String, String>(){{put("id", "1.0");}}, "id");
        
        assertThat(address.value("street"), is("Iidabashi 1-1"));

        address = bean.list("addressesMultiIndexed").findByIndex(new HashMap<String, String>(){{put("street", "Iidabashi 2-1");}}, "street");
        
        assertThat(address.value("street"), is("Iidabashi 2-1"));
    }
    
    @Test
    public void findByIndex_returnsNull_when_illegalIndexNameWasGiven() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        @SuppressWarnings("serial")
        XlBean address = bean.list("addressesMultiIndexed").findByIndex(new HashMap<String, String>(){{put("id", "1.0");}}, "illegalIdName");
        
        assertThat(address, is(nullValue()));
    }
    
    @Test
    public void findByIndex_returnsnull_when_noindexnamewasspecifiedtomultiindexedtable() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        @SuppressWarnings("serial")
        XlBean address = bean.list("addressesMultiIndexed").findByIndex(new HashMap<String, String>(){{put("test", "test");}});
        
        assertThat(address, is(nullValue()));
    }

    @Test
    public void find_outofindex_tobe_null() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlBean address = bean.list("addresses").find(300);
        
        assertThat(address, is(nullValue()));
    }
    
    @Test
    public void toMap() {
        InputStream in = XlListTest.class.getResourceAsStream("TestBook_XlList.xlsx");
        
        XlBeanReader reader = new XlBeanReader();
        XlBean bean = reader.read(in);
        
        XlBean streets = bean.list("addresses").toMap("street");
        
        assertThat(streets.bean("Tsukishima 1-3").value("prefecture"), is("Tokyo-to"));
    }
}
