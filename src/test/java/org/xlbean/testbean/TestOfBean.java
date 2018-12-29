package org.xlbean.testbean;

import java.util.List;

public class TestOfBean {

    private String aaa;

    private TestOfBeanBBB bbb1;

    private List<TestOfBeanCCC> ccc;

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public TestOfBeanBBB getBbb1() {
        return bbb1;
    }

    public void setBbb1(TestOfBeanBBB bbb1) {
        this.bbb1 = bbb1;
    }

    public List<TestOfBeanCCC> getCcc() {
        return ccc;
    }

    public void setCcc(List<TestOfBeanCCC> ccc) {
        this.ccc = ccc;
    }

    @Override
    public String toString() {
        return "TestOfBean [aaa=" + aaa + ", bbb1=" + bbb1 + ", ccc=" + ccc + "]";
    }
}
