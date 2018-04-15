package org.xlbean.testbean;

import java.math.BigDecimal;

public class Stats {
    private Long totalArea;
    private BigDecimal gdp;

    public Long getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Long totalArea) {
        this.totalArea = totalArea;
    }

    public BigDecimal getGdp() {
        return gdp;
    }

    public void setGdp(BigDecimal gdp) {
        this.gdp = gdp;
    }

    @Override
    public String toString() {
        return "Stats [totalArea=" + totalArea + ", gdp=" + gdp + "]";
    }
}
