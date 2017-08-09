xlbean
====

Excelシート上のデータのJavaプログラムからの読み書きを、**Excelシート上にマッピング定義を記載**することで実現するユーティリティ。

**Excelシートを多少よごしてもよいから、とにかく早くJavaプログラムから使いたい**という用途におすすめです。

## Getting Started

### Installing

1. 依存関係の設定

Mavenの場合
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

Gradleの場合
```

```

2. Excelシートへの読み込み定義の追記
2-1. Excelシート上の表形式データの各カラムに名前を付け、データが始まる行をマークする
2-2. Excelファイル内の読み込み対象シートにマークする

3. Javaプログラムの記述
```
InputStream in = new FileInputStream("example/presidents.xlsx");
XlBeanReader reader = new XlBeanReader();
XlBean bean = reader.read(in);

XlList list = bean.list("presidents");
```



* 表形式のデータの各列に名前を付けて、読み込み対象の行を指定すると、各行がjava.util.HashMapに、表全体がそのjava.util.ArrayListにマップされます。
* 単一セルの定義も可能。
* 書き出しもできます。

Excelシートに定義されたデータを利用するJavaライブラリとしては、[Apache POI](https://poi.apache.org/)を代表とした低レベルなAPIから、[Jxls](http://jxls.sourceforge.net/)といった中～高レベルのAPIまですでに色々なものがありますが、Excel帳票を作成することが主目的であり出力結果の整形にフォーカスされたライブラリが多く、多少Excelシート上をよごしてもよいから早くJavaプログラムから使いたい、という方向性のライブラリがないように感じたため作成しました。




## Licence


