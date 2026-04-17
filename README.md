# 在庫管理アプリ
商品の在庫を管理するアプリケーションです。

## 使用技術
・フロントエンド：HTML・CSS・Bootstrap・JavaScript・Thymeleaf

・バックエンド：Spring boot・Java・H2 Database

## 実行方法
1.リポジトリーを保存したいファイルに移動してクローンしてください。
```
git clone https://github.com/forest-2025/StockControl2.git
```

2.商品画像ファイルを保存するディレクトリを設定をしてください。<br>
application.propertiesの<br>
```
# 画像保存先（任意）.
file.upload-dir=C:/任意のディレクトリ/<br>
```
に、任意のディレクトリを設定して画像ファイルの保存先を設定してください。

3.EclipseなどのIDEで開いて、アプリを起動させる。

4.ブラウザで[http://localhost:8080/](http://localhost:8080/ "StockControl2")を開く

## 工夫した点
文字を大きめにしました。
