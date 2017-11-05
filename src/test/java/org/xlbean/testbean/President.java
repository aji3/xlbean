package org.xlbean.testbean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class President {
  private String name;
  private LocalDate dateOfBirth;
  private String stateOfBirth;
  private Date inOfficeFrom;
  private LocalDateTime inOfficeTo;
  private int numberOfDaysInOffice;
  // Getter / Setter は省略
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getStateOfBirth() {
    return stateOfBirth;
  }

  public void setStateOfBirth(String stateOfBirth) {
    this.stateOfBirth = stateOfBirth;
  }

  public Date getInOfficeFrom() {
    return inOfficeFrom;
  }

  public void setInOfficeFrom(Date inOfficeFrom) {
    this.inOfficeFrom = inOfficeFrom;
  }

  public LocalDateTime getInOfficeTo() {
    return inOfficeTo;
  }

  public void setInOfficeTo(LocalDateTime inOfficeTo) {
    this.inOfficeTo = inOfficeTo;
  }

  public int getNumberOfDaysInOffice() {
    return numberOfDaysInOffice;
  }

  public void setNumberOfDaysInOffice(int numberOfDaysInOffice) {
    this.numberOfDaysInOffice = numberOfDaysInOffice;
  }

  @Override
  public String toString() {
    return "President [name="
        + name
        + ", dateOfBirth="
        + dateOfBirth
        + ", stateOfBirth="
        + stateOfBirth
        + ", inOfficeFrom="
        + inOfficeFrom
        + ", inOfficeTo="
        + inOfficeTo
        + ", numberOfDaysInOffice="
        + numberOfDaysInOffice
        + "]";
  }
}
