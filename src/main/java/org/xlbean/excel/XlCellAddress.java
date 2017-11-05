package org.xlbean.excel;

/**
 * Class to represent a cell of excel sheet.
 *
 * @author Kazuya Tanikawa
 */
public class XlCellAddress {

  private Integer row;
  private Integer column;

  private XlCellAddress() {}

  public Integer getRow() {
    return row;
  }

  public void setRow(Integer row) {
    this.row = row;
  }

  public Integer getColumn() {
    return column;
  }

  public void setColumn(Integer column) {
    this.column = column;
  }

  public void merge(XlCellAddress cell) {
    if (cell.getRow() != null) {
      this.setRow(cell.getRow());
    }
    if (cell.getColumn() != null) {
      this.setColumn(cell.getColumn());
    }
  }

  public XlCellAddress clone() {
    XlCellAddress ret = new XlCellAddress();
    ret.setRow(this.row);
    ret.setColumn(this.column);
    return ret;
  }

  public static class Builder {
    private Integer row;
    private Integer column;

    public Builder row(Integer row) {
      this.row = row;
      return this;
    }

    public Builder column(Integer column) {
      this.column = column;
      return this;
    }

    public XlCellAddress build() {
      XlCellAddress cell = new XlCellAddress();
      cell.setRow(row);
      cell.setColumn(column);
      return cell;
    }
  }

  @Override
  public String toString() {
    return "XlCellAddress [row=" + row + ", column=" + column + "]";
  }
}
