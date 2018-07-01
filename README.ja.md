[[English]](https://github.com/aji3/xlbean/blob/master/README.md)

xlbean
====
[![Build Status](https://travis-ci.org/aji3/xlbean.svg?branch=master)](https://travis-ci.org/aji3/xlbean)
[![Coverage Status](https://coveralls.io/repos/github/aji3/xlbean/badge.svg?branch=master)](https://coveralls.io/github/aji3/xlbean?branch=master)

Excelシート上のデータのJavaプログラムからの読み書きを、**Excelシート上にマッピング定義を記載**することで実現するユーティリティ。

**とにかく早く・シンプルにExcelに定義した値をJavaプログラムから使いたい**という用途におすすめです。

## 始め方

### インストール

#### 1. 依存関係の設定

**Mavenの場合**
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
        <version>0.2.0</version>
    </dependency>
</dependencies>
```

**Gradleの場合**
```
repositories {
     jcenter()
}
dependencies {
    compile group: 'org.xlbean', name: 'xlbean', version:'0.2.0'
}
```


#### 2. Excelシートへの定義の記述

1. Excelシート上の表形式データの各カラムに名前を付ける

    R1の該当するカラムに **"テーブル名#カラム名"**

2. データが始まる行をマークする

    C1の該当する行に **"テーブル名#~(チルダ)"**

3. Excelファイル内の読み込み対象シートにマークする

    R1C1に **"####"** と入力

    ![Excelシートの例](https://user-images.githubusercontent.com/23234132/29244923-4f5cba56-8002-11e7-929d-617a9ea38d83.png "Excelシートの例")

##### 3. Javaプログラムの記述
```java
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

XlList list = bean.beans("presidents");
// "文字列の入ったMapのList"となります
System.out.println(list.get(0).get("name")); // John F. Kennedy
System.out.println(list.get(0).get("dateOfBirth")); // 1917-05-29T00:00:00.000

// BeanをClassにマップして利用することもできます
List<President> presidents = bean.listOf("presidents", President.class);
```


## 機能一覧

### 単一セルの読み込み

単一セルの1行目と1カラム目に該当するセルに名前を定義してください。

<img width="583" alt="example_excel_single_item_1" src="https://user-images.githubusercontent.com/23234132/29818012-5a50272c-8cf5-11e7-8e79-98f05eee87c6.PNG">

```java
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

// 単一セルの値を取得
String name = bean.string("name");
System.out.println(name);// United States of America
```

### 複数のテーブル、セルの読み込み

単一セルの読み込みのサンプル画像のとおり、1行目もしくは1列目の定義セルに複数の定義が入る場合、**カンマ区切り** で記入してください。



### マップの定義

単一セルの読み込みのサンプル画像の通り、セル名をピリオド区切りで定義することでマップによって階層化されます。

```java
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

// マップとして定義した値を取得
XlBean stats = bean.bean("stats");
System.out.println(stats.string("totalArea"));// 9833520.0
System.out.println(stats.string("gdp"));// 18558000000000000
```

### 読み込みオプションの指定

Excelの特性、および、当ライブラリがラッピングしているApache POIの特性により、データの読み込み結果が必ずしもExcelの表示と同一にならないケースがあります。

例えば、以下のようなシートを当ライブラリで読み込んだ場合、左右の列で結果は異なります。
<img width="471" alt="example_excel_option" src="https://user-images.githubusercontent.com/23234132/29818705-8c904700-8cf8-11e7-8077-6d0a237df149.PNG">

```
InputStream in = new FileInputStream("example/optionExample.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

XlList noOptionTable = bean.beans("noOptionTable");
System.out.println(noOptionTable.get(0).get("defaultCellType"));// 0.0
System.out.println(noOptionTable.get(0).get("stringCellType")); // 0

System.out.println(noOptionTable.get(1).get("defaultCellType"));// 1.0
System.out.println(noOptionTable.get(1).get("stringCellType")); // 1
```

これは、Excelは左列を **数値**として扱っており、右列ではそれを **文字列**として扱っているためです。

このようなケースに対応するため、当ライブラリは **各定義に意味を追加するための"オプション"機能** を持っています。

この例に対応したオプションとしては、以下のように **type** プロパティを利用することで同じ形式で読み込めるようになります。

#### オプション：type - セルの型を明示的に指定

利用可能なtypeは下記のとおりです：
* **string** - 強制的に表示されている通りの文字列として読み込みます。読み込み結果については、Apache POIのorg.apache.poi.ss.usermodel.DataFormatterの挙動に準拠します。ただし、日付型の場合には既知の障害があり、Excelの表示と読み込み結果が同一にならないケースがあります。

<img width="575" alt="example_excel_option_1" src="https://user-images.githubusercontent.com/23234132/29819102-22e46f78-8cfa-11e7-83f6-b8b2d9a50495.PNG">

```java
XlList optionTable = bean.beans("optionTable");
System.out.println(optionTable.get(0).get("defaultCellType"));// 0 <= 文字列扱いとなる
System.out.println(optionTable.get(0).get("stringCellType")); // 0

System.out.println(optionTable.get(1).get("defaultCellType"));// 1 <= 文字列扱いとなる
System.out.println(optionTable.get(1).get("stringCellType")); // 1
```

#### オプション：limit - 読み込みの上限件数を指定

<img width="260" alt="example_excel_option_2" src="https://user-images.githubusercontent.com/23234132/29819268-e40a802a-8cfa-11e7-8f56-ce8cf5df61e8.PNG">

```java
XlList limitedTable = bean.beans("limitedTable");
System.out.println(limitedTable.size());// 5 <= Number of rows loaded is limited to 5
System.out.println(limitedTable.get(0).string("value"));// 0.0 <= The list is started from row 1
System.out.println(limitedTable.get(1).string("value"));// 1.0
```

#### オプション：index - indexとして指定したカラムの値をハッシュ検索用のキーとする

<img width="450" alt="example_excel_option_3" src="https://user-images.githubusercontent.com/23234132/30059107-b105da8e-9278-11e7-9810-03b2d45a2395.PNG">

```java
XlList presidents = bean.beans("presidents");
Map<String, String> condition = new HashMap<>();
condition.put("id", "45");

XlBean p = presidents.find(condition);
System.out.println(p);// {name=Donald Trump, dateOfBirth=1946-06-14T00:00:00.000, id=45, ...}
p = presidents.findByIndex(condition);
System.out.println(p);// {name=Donald Trump, dateOfBirth=1946-06-14T00:00:00.000, id=45, ...}
```

`XlBean#find`と`XlBean#findByIndex`で結果的に取得できる行は同じですが、内部の挙動は異なります。

`XlBean#find`では、内部的にすべてのデータに対してループを回しており、最初に条件に合致する行がでるまですべての行に対して比較を行っています。

`XlBean#findByIndex`では、Excelから読み込むタイミングでindexで指定された値をキーとするHashMapを内部で構築しており、`findByIndex`メソッドが呼び出されたタイミングではハッシュ値で検索しています。
Excelからの読み込み後に繰り返し検索するようなユースケースで利用することを想定しています。

なお、複数カラムに同一index名を付けることで複合キーとすることもできます。


#### Option: listToProp - 複数の単一セルをテーブル形式で定義する

単一セルの値を定義するには、通常は以下ように定義する必要があります。

|| val1, val2, val3 |
|---|---|
| val1 | this is a test value |
| val2 | another test value |
| val3 | again another test value |

このやり方では変数の数が多い場合、定義が煩雑になってしまいます。

上記のやり方の代わりに、 listToProp=keyとlistToProp=valueオプションを利用することでリスト形式で定義することができます:

||some_list#key?listToProp=key|some_list#value?listToProp=value|
|---|---|---|
|some_list#~|val1| this is a test value |
|| val2 | another test value |
|| val3 | again another test value |

### 横向きのテーブル

横に向かってデータを並べるテーブルは以下のように定義してください。

<img width="563" alt="example_excel_tabletoright" src="https://user-images.githubusercontent.com/23234132/30060386-1b64ab26-927e-11e7-9743-eb2ff1b66410.png">

Javaプログラムは縦方向と同じです。


### "コメント" を利用した定義（コメント定義モード）

Excelシートの1行目および1列目を定義に利用するのではなく、セルにコメントをつけることで読み込み対象セルを指定することも可能です。

<img width="544" alt="example_excel_comment" src="https://user-images.githubusercontent.com/23234132/30059718-41e2e266-927b-11e7-8cda-52b9da2c9c36.PNG">

```java
XlBeanReader reader = new XlBeanReader(new ExcelCommentDefinitionLoader());
InputStream in = XlBeanReaderTest.class.getResourceAsStream("TestBook_presidents_comment.xlsx");
XlBean bean = reader.read(in);
```

上述のサンプルプログラムのとおり、`ExcelCommentDefinitionLoader`を`XlBeanReader`の定義時に指定してください。

注記）上記サンプルのとおり、シートを読み込むかどうかの判定はR1C1のコメントを利用しています。

### コメント定義モードにおけるオプション

1行目および1列目を利用する定義モードと同様のオプションが利用可能です。

下記に示すdirectionオプションがコメント定義モードで必要となります。

#### オプション：direction - 読み込み方向を指定する（コメント定義モード時に利用）

コメント定義モードで横向きに伸びるテーブルを定義する場合、オプションに`?direction=right`を指定してください。



### 複数のシートの扱い

ワークブックが複数のシートを持つ場合、R1C1セルに**"####"** が定義がされたシートを読み込みます。

複数のシートに同一の名前が定義されている場合、以下のルールで読み込みます。

* 単一セル - 後に読み込まれた値で上書きされます。
* テーブル - すべての結果が一つのリストに読み込まれます。

同一のテーブルと単一セルで取り扱いが異なるため注意してください。


### 無効なシートの扱い

Excelのワークブックに複数のシートが含まれている場合、まずR1C1を参照し、**"####"** の記載がある場合のみ読み込みます。

したがって読み込み対象のシートと対象外のシートがワークブックに同居することが可能です。




## Javadoc

https://aji3.github.io/xlbean/


## Licence

http://www.apache.org/licenses/LICENSE-2.0
