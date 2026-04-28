# 在庫管理アプリ
商品の在庫を管理するアプリケーションです。

## 機能一覧
 - 商品情報管理: 一覧、登録・修正・削除
 - 商品在庫管理: 入荷・出荷・在庫修正
 - ユーザー管理: 一覧、登録・修正・削除
 - 入荷先管理: 一覧、登録・修正・削除
 - 出荷先管理: 一覧、登録・修正・削除

## 仕様書
 - [要件定義書](https://github.com/forest-2025/StockControl2/tree/master/doc/要件定義書.png)
 - [画面設計書](https://github.com/forest-2025/StockControl2/tree/master/doc/画面設計書.pdf)
 - [URL一覧](https://github.com/forest-2025/StockControl2/tree/master/doc/URL一覧.pdf)
 - [テーブル定義書](https://github.com/forest-2025/StockControl2/tree/master/doc/テーブル定義書.pdf)

## 使用技術

### バックエンド
 - Java: 21
 - Spring Boot: 3.5.7
 - Database: H2 Database
　 起動時にテーブルと初期データが自動生成されます。

### フロントエンド
 - Thymeleaf
 - Bootstrap: 5.3.8
 - Bootstrap Icons: 1.13.1
 - JavaScript
 - jQuery: 3.7.1
 - Popper.js: 2.9.2

## 実行方法
1. リポジトリーを保存するフォルダに移動して、リポジトリーをクローンしてください。
```
git clone https://github.com/forest-2025/StockControl2.git
```

1. 商品画像ファイルを保存するディレクトリを作成してください。

1. 作成した商品画像ファイルの保存先のパスを application.properties の **file.upload-dir** に設定してください。

[application.properties]
```
# 画像保存先（任意）.
file.upload-dir=C:/'任意のディレクトリ'/
```

1. SpringBootが起動できるIDE (Pleiades Eclipseなど)でプロジェクトをインポートしてアプリを実行してください。

  JDK21（Java 21）以降をご用意いただければ、IDEを使用せずにシェルでアプリを実行することができます。<br>
  その場合は、プロジェクトのルートディレクトリ（pom.xmlがある場所）に移動し、以下のコマンドを実行してください。
```
./mvnw spring-boot:run
```

1. ブラウザで[http://localhost:8080/login](http://localhost:8080/login "StockControl2")を開いてください。

## ログイン情報
一般ユーザー
- メールアドレス：`tokyo@company.co.jp`
- パスワード：12345

管理者
- メールアドレス：`osaka@company.co.jp`
- パスワード：12345


## 画像イメージ
- 商品一覧
「![代替テキスト](https://github.com/user-attachments/assets/74500c3f-9de0-4dee-b794-08d9e24220b6)
<img width="1888" height="846" alt="スクリーンショット 2026-04-21 141030" src="https://github.com/user-attachments/assets/74500c3f-9de0-4dee-b794-08d9e24220b6" />
<img width="1884" height="677" alt="スクリーンショット 2026-04-21 131720" src="https://github.com/user-attachments/assets/400fafd6-aa27-45c6-bcdd-b7dc194631e6" />
 <br>

・詳細情報<br>
 
<img width="1886" height="797" alt="スクリーンショット 2026-04-21 131837" src="https://github.com/user-attachments/assets/39987aeb-ca70-4a2a-9af7-954260d3d263" />
<img width="1887" height="414" alt="スクリーンショット 2026-04-21 131853" src="https://github.com/user-attachments/assets/dbb822cd-f20f-4b17-961a-3f0eac7ed987" />
 
## 工夫した点
・年配の方が見やすいように文字を**大きめ**にいたしました。<br>
・ヘッダーの背景色を、入荷画面と入荷先に関する画面は赤、出荷画面と出荷先に関する画面は青など、項目ごとに変化させることでどの項目の操作をしているかを視覚的に把握しやすくなるようにいたしました。<br>
・スマートフォン・タブレット・パソコンでの運用を想定しているため、レスポンシブにすることでデバイスのサイズにかかわらず画面が見やすく、操作しやすいようにいたしました。<br>
・ページネーション・商品画像・各種並べ替え機能（昇順・降順）を実装することでユーザが操作しやすくなるようにいたしました。
