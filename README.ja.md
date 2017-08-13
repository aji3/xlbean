xlbean
====

Excelシート上のデータのJavaプログラムからの読み書きを、**Excelシート上にマッピング定義を記載**することで実現するユーティリティ。

**Excelシートを多少よごしてもよいから、とにかく早く・シンプルにJavaプログラムから使いたい**という用途におすすめです。

## 始め方

### インストール

#### 1. 依存関係の設定

**Mavenの場合**
```
<repositories>
    <repository>
        <id>xlbean</id>
        <url>https://raw.github.com/aji3/xlbean/mvn-repo/</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>org.xlbean</groupId>
        <artifactId>xlbean</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

**Gradleの場合**
```
repositories {
     mavenCentral()
     maven { url "https://raw.github.com/aji3/xlbean/mvn-repo/" }
}
dependencies {
    compile group: 'org.xlbean', name: 'xlbean', version:'0.1.0'
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
```
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

XlList list = bean.list("presidents");
// "文字列の入ったMapのList"となります
System.out.println(list.get(0).get("name")); // John F. Kennedy
System.out.println(list.get(0).get("dateOfBirth")); // 1917-05-29T00:00:00.000

// BeanをClassにマップして利用することもできます
List<President> presidents = bean.listOf("presidents", President.class);
```


## 機能一覧

### 単一セルの読み込み



### 複数のテーブル、セルの読み込み

単一セルの読み込みの記述の通り、1行目もしくは1列目の定義セルに複数の定義が入る場合、**カンマ区切り** で記入してください。

### マップの定義



### "コメント" を利用した定義



注記）上記サンプルのとおり、シートを読み込むかどうかの判定はR1C1のコメントを利用しています。

### 横向きのテーブル

横に向かってデータを並べるテーブルは以下のように定義してください。

Javaプログラム側は同じになります。

### 読み込みオプションの指定

Excelの特性、もしくは、当ライブラリがラッピングしているApache POIの特性により、データの読み込みで意図した動きにならないケースがあります。

例えば、以下のようなシートを当ライブラリで読み込んだ場合、左右の列で結果は異なります。


これは、Excelは左列を **数値**として扱っており、右列ではそれを **文字列**として扱っているためです。

このようなケースに対応するため、当ライブラリは **各定義に意味を追加するオプション** 機能を持っています。

この例に対応したオプションとしては、以下のように **type** プロパティを利用することで同じ形式で読み込めるようになります。

### オプション：type - セルの型を明示的に指定

利用可能なtypeは下記のとおりです：
* string - 強制的に表示されている通りの文字列として読み込みます。読み込み結果については、Apache POIのorg.apache.poi.ss.usermodel.DataFormatterの挙動に準拠します。Date型の場合に既知の障害があります。
* data - 
* date - 

### オプション：limit - 読み込みの上限件数を指定


### オプション：index - 読み込み結果のリストをindexとして指定した値をキーに検索できるマップを持つ



### オプション：direction - 読み込み方向を指定する（コメント定義モード時に利用）

コメント定義モードで横向きのテーブルを読み込む場合、以下のように定義してください。



### 複数のシートの扱い

ワークブックがR1C1セルに**"####"** が定義がされた複数のシートを持つ場合、すべての対象シートを読み込みます。

* 単一セル - 後に読み込まれた値で上書きされます。
* テーブル - すべての結果が一つのリストに読み込まれます。

同一のテーブルと単一セルで取り扱いが異なるため注意してください。


### 無効なシートの扱い

Excelのワークブックに複数のシートが含まれている場合、まずR1C1を参照し、**"####"** の記載がある場合のみ読み込みます。

したがって読み込み対象のシートと対象外のシートがワークブックに同居することが可能です。


### Excelへの書き出し


### テンプレートのExcelを用意しない書き出し


### ライブラリの拡張

### XlBeanReaderの拡張

### XlBeanWriterの拡張




## Javadoc




## Licence
