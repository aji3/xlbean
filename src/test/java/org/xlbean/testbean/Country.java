package org.xlbean.testbean;

import java.util.List;

public class Country {

  private String name;
  private Stats stats;
  private List<President> presidents;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Stats getStats() {
    return stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public List<President> getPresidents() {
    return presidents;
  }

  public void setPresidents(List<President> presidents) {
    this.presidents = presidents;
  }

  @Override
  public String toString() {
    return "Country [name=" + name + ", stats=" + stats + ", presidents=" + presidents + "]";
  }
}
