[[Japanese]](https://github.com/aji3/xlbean/blob/master/README.ja.md)

xlbean
====
[![Build Status](https://travis-ci.org/aji3/xlbean.svg?branch=master)](https://travis-ci.org/aji3/xlbean)
[![Coverage Status](https://coveralls.io/repos/github/aji3/xlbean/badge.svg?branch=master)](https://coveralls.io/github/aji3/xlbean?branch=master)

Java utility to read/write data defined in excel sheet **by defining mapping definition on excel sheet itself.**

This library will be useful when you want to **use excel sheet from Java program rapidly and simply.**

## Getting Started

### Install

#### 1. Dependency

**For Maven**
```
<repositories>
    <repository>
        <id>xlbean</id>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>org.xlbean</groupId>
        <artifactId>xlbean</artifactId>
        <version>0.1.7</version>
    </dependency>
</dependencies>
```

**For Gradle**
```
repositories {
     jcenter()
}
dependencies {
    compile group: 'org.xlbean', name: 'xlbean', version:'0.1.7'
}
```

#### 2. Define Mapping Definition on Excel sheet

1. Define name for each column of table in Excel sheet.

    Define **"YOUR_TABLE_NAME#YOUR_COLUMN_NAME"** on the row 1 of the corresponding column.

2. Mark the row which the data of the table starts from.

    Input **"YOUR_TABLE_NAME#~(tilda)"** on the column 1 of the corresponding row.

3. Mark the sheet as the target to be loaded by this utility.

    Input **"####"** on R1C1.

    ![Excelシートの例](https://user-images.githubusercontent.com/23234132/29244923-4f5cba56-8002-11e7-929d-617a9ea38d83.png "Excelシートの例")

##### 3. Java Program
```java
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

XlList list = bean.list("presidents");
// List filled with Map of String
System.out.println(list.get(0).get("name")); // John F. Kennedy
System.out.println(list.get(0).get("dateOfBirth")); // 1917-05-29T00:00:00.000

// The map can be mapped to Class
List<President> presidents = bean.listOf("presidents", President.class);
```


## List of Functions

### Reading Individual Cell (non-table cell)

Define name of the cell in both row 1 and the column 1 of the cell.

<img width="583" alt="example_excel_single_item_1" src="https://user-images.githubusercontent.com/23234132/29818012-5a50272c-8cf5-11e7-8e79-98f05eee87c6.PNG">

```java
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

// Get the value of the individual cell
String name = bean.value("name");
System.out.println(name);// United States of America
```

### Reading Multiple Tables, Cells

If multiple definition to be defined in a cell, **connect the definition by comma.**


### Define Map

As shown in the image for "Reading Individual Cell", value can be layered by period separated name.

```java
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

// Get value defined as Map
XlBean stats = bean.bean("stats");
System.out.println(stats.value("totalArea"));// 9833520.0
System.out.println(stats.value("gdp"));// 18558000000000000
```

### Options for Reading Mode

In consequence of the characteristics of Excel or Apache POI which this library wraps, data loaded into the program might be different from the expression on the Excel sheet.

For instance, table shown in the example below will have different values for the left and right column.
<img width="471" alt="example_excel_option" src="https://user-images.githubusercontent.com/23234132/29818705-8c904700-8cf8-11e7-8077-6d0a237df149.PNG">

```
InputStream in = new FileInputStream("example/optionExample.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

XlList noOptionTable = bean.list("noOptionTable");
System.out.println(noOptionTable.get(0).get("defaultCellType"));// 0.0
System.out.println(noOptionTable.get(0).get("stringCellType")); // 0

System.out.println(noOptionTable.get(1).get("defaultCellType"));// 1.0
System.out.println(noOptionTable.get(1).get("stringCellType")); // 1
```

This is because on the left column Excel treat the value as ** Number ** and on the right it is ** String **.

To control the case like this, xlbean has a ** concept of "options" to add behavior to each definition.**

For this specific case, this can be controlled by using ** type ** option shown below.

#### Option：type - Explicitly declare the type of the cell

Available types：
* **string** - Force to load value as String. Format of the String follow the specification of `org.apache.poi.ss.usermodel.DataFormatter` of Apache POI. There are known issue on formatting Date type.

<img width="575" alt="example_excel_option_1" src="https://user-images.githubusercontent.com/23234132/29819102-22e46f78-8cfa-11e7-83f6-b8b2d9a50495.PNG">

```java
XlList optionTable = bean.list("optionTable");
System.out.println(optionTable.get(0).get("defaultCellType"));// 0 <= Loaded as String
System.out.println(optionTable.get(0).get("stringCellType")); // 0

System.out.println(optionTable.get(1).get("defaultCellType"));// 1 <= Loaded as String
System.out.println(optionTable.get(1).get("stringCellType")); // 1
```

#### Option：limit - Limit of number of rows to be loaded

<img width="260" alt="example_excel_option_2" src="https://user-images.githubusercontent.com/23234132/29819268-e40a802a-8cfa-11e7-8f56-ce8cf5df61e8.PNG">

```java
XlList limitedTable = bean.list("limitedTable");
System.out.println(limitedTable.size());// 5 <= Number of rows loaded is limited to 5
System.out.println(limitedTable.get(0).value("value"));// 0.0 <= The list is started from row 1
System.out.println(limitedTable.get(1).value("value"));// 1.0
```

#### Option：index - Enable index search by using value of the column 

<img width="450" alt="example_excel_option_3" src="https://user-images.githubusercontent.com/23234132/30059107-b105da8e-9278-11e7-9810-03b2d45a2395.PNG">

```java
XlList presidents = bean.list("presidents");
Map<String, String> condition = new HashMap<>();
condition.put("id", "45");

XlBean p = presidents.find(condition);
System.out.println(p);// {name=Donald Trump, dateOfBirth=1946-06-14T00:00:00.000, id=45, ...}
p = presidents.findByIndex(condition);
System.out.println(p);// {name=Donald Trump, dateOfBirth=1946-06-14T00:00:00.000, id=45, ...}
```

2 methods for searching `XlBean#find` and `XlBean#findByIndex` will returns the same result eventually, however the behavior inside is different.

`XlBean#find` loops for all the data until it gets the bean corresponds to the condition given.

`XlBean#findByIndex` doesn't loop at all. When index option is configured, xlbean creates a HashMap using value of the column with index option as keys and the row itself as values, and `findByIndex` checks the HashMap to get the value.
This feature might be useful for usecase when the loaded value need to be searched multiple times.

Composite key can be used by defining index option of the same name to multiple columns.

#### Option: listToProp - Define multiple single values in a list

To define a single cell value, definition like below is needed:

|| val1, val2, val3 |
|---|---|
| val1 | this is a test value |
| val2 | another test value |
| val3 | again another test value |

It is not convenient when a lot of variable names are there in a column. 

Instead of above, by using listToProp=key and listToProp=value option, they can be defined in a simple list:

||some_list#key?listToProp=key|some_list#value?listToProp=value|
|---|---|---|
|some_list#~|val1| this is a test value |
|| val2 | another test value |
|| val3 | again another test value |

### Table to Sideway

Table grows to sideways can also be loaded.

<img width="563" alt="example_excel_tabletoright" src="https://user-images.githubusercontent.com/23234132/30060386-1b64ab26-927e-11e7-9743-eb2ff1b66410.png">

No difference on Java program.


### "Comment" Definition Mode

Instead of using row 1 and column 1 for the definition, comment feature of Excel can be used.

<img width="544" alt="example_excel_comment" src="https://user-images.githubusercontent.com/23234132/30059718-41e2e266-927b-11e7-8cda-52b9da2c9c36.PNG">

```java
XlBeanReader reader = new XlBeanReader(new ExcelCommentDefinitionLoader());
InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents_comment.xlsx");
XlBean bean = reader.read(in);
```

As shown in the above list, `ExcelCommentDefinitionLoader` need to be given to the constructor of `XlBeanReader`.

Note） Mark to validate the sheet is needed as a comment for R1C1.

### Options for Comment Definition Mode

Basically the same options for R1C1 definition mode can be used.

In addition to them, the below option is required specifically for this mode.

#### Option：direction - Define table direction(for Comment definition mode only)

When table to sideways to be loaded, define option `?direction=right`.


### Multiple Sheets in 1 Workbook

When a Workbook has multiple sheets, only the sheets with **"####"** defined in R1C1 cell will become targets to be loaded.

If the target sheets has columns with the same name, they will be loaded by the rule below:

* Individual cell - The value to be loaded last will be returned.
* Table - All the values will be put into the result list.


## Javadoc

https://aji3.github.io/xlbean/


## Licence

http://www.apache.org/licenses/LICENSE-2.0
