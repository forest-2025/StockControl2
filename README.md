# 在庫管理アプリ
商品の在庫を管理するアプリケーションです。

## 機能一覧
 ・商品在庫管理: 商品一覧、商品登録、入荷・出荷・在庫調整<br>
 ・ユーザー管理: 一覧、登録・修正・削除<br>
 ・入荷先管理: 一覧、登録・修正・削除<br>
 ・出荷先管理: 一覧、登録・修正・削除<br>

## 仕様書
 ・[要件定義書](https://github.com/forest-2025/StockControl2/tree/master/doc/要件定義書.png)<br>
 ・[画面設計書](https://github.com/forest-2025/StockControl2/tree/master/doc/画面設計書.pdf)<br>
 ・[URL一覧](https://github.com/forest-2025/StockControl2/tree/master/doc/URL一覧.pdf)<br>
 ・[テーブル定義書](https://github.com/forest-2025/StockControl2/tree/master/doc/テーブル定義書.pdf)<br>

## 使用技術

### バックエンド
・ Java: 21<br>
・ Spring Boot: 3.5.7<br>
・ Database: H2 Database<br>
　 起動時にテーブルと初期データが自動生成されます。<br>

### フロントエンド
・ Thymeleaf<br>
・ Bootstrap: 5.3.8<br>
・ Bootstrap Icons: 1.13.1<br>
・ JavaScript<br>
・ jQuery: 3.7.1<br>
・ Popper.js: 2.9.2<br>

## 実行方法
1.リポジトリーを保存するフォルダに移動して、リポジトリーをクローンしてください。
```
git clone https://github.com/forest-2025/StockControl2.git
```
<br>
2.商品画像ファイルを保存するディレクトリを作成してください。<br><br><br>

3.作成した商品画像ファイルの保存先のパスを application.properties の **file.upload-dir** に設定してください。<br>

[application.properties]
```
# 画像保存先（任意）.
file.upload-dir=C:/'任意のディレクトリ'/
```
<br>
4.SpringBootが起動できるIDE (Pleiades Eclipseなど)でプロジェクトをインポートしてアプリを実行してください。<br>
IDEを使用せずにシェルで実行する場合は、JDK21（Java 21）以上をインストールし、プロジェクトのルートディレクトリ（pom.xmlがある場所）に移動して、以下のコマンドを実行してください。<br>

<br>


```
./mvnw spring-boot:run
```
<br>

5.ブラウザで[http://localhost:8080/login](http://localhost:8080/login "StockControl2")を開いてください。



## ログイン情報
一般ユーザー<br>
・メールアドレス：`tokyo@company.co.jp`<br>
・パスワード：12345

管理者<br>
・メールアドレス：`osaka@company.co.jp`<br>
・パスワード：12345


## 画像イメージ
・商品一覧<br>

<img width="1888" height="846" alt="スクリーンショット 2026-04-21 141030" src="https://github.com/user-attachments/assets/74500c3f-9de0-4dee-b794-08d9e24220b6" />
<img width="1884" height="677" alt="スクリーンショット 2026-04-21 131720" src="https://github.com/user-attachments/assets/400fafd6-aa27-45c6-bcdd-b7dc194631e6" />
 <br>

・詳細情報<br>
 
<img width="1886" height="797" alt="スクリーンショット 2026-04-21 131837" src="https://github.com/user-attachments/assets/39987aeb-ca70-4a2a-9af7-954260d3d263" />
<img width="1887" height="414" alt="スクリーンショット 2026-04-21 131853" src="https://github.com/user-attachments/assets/dbb822cd-f20f-4b17-961a-3f0eac7ed987" />
 
## 工夫した点
・文字を見やすいように**大きめ**にしました。<br>
・ヘッダーの背景色を項目ごとに変化するようにしました。<br>
・画面のサイズによって見えやすいようにしました。<br>
・各情報の登録・修正・削除・検索機能、商品の入出荷処理、入出荷の履歴の表示などのシンプルな機能でしたが、ページネーション・商品画像・各種並べ替え機能（昇順・降順）を追加することで、ユーザーが操作しやすくなるように工夫しました。
