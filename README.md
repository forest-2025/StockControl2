# 在庫管理アプリ
商品の在庫を管理するアプリケーションです。

## 使用技術
・フロントエンド：HTML・CSS・Bootstrap・JavaScript・Thymeleaf

・バックエンド：Spring boot・Java・H2 Database

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
4.EclipseなどのIDEでプロジェクトを開いてアプリを実行してください。<br>
または、コマンドプロンプトなどでプロジェクトのルートディレクトリ（pom.xmlがある場所）に移動して以下で実行してください。<br>

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


## 画像
・商品一覧<br>

<img width="1885" height="849" alt="スクリーンショット 2026-04-21 131356" src="https://github.com/user-attachments/assets/0905cf5a-2d14-4e72-9f9d-bf38c58aee33" />
<img width="1884" height="677" alt="スクリーンショット 2026-04-21 131720" src="https://github.com/user-attachments/assets/400fafd6-aa27-45c6-bcdd-b7dc194631e6" />
 <br>

 ・詳細情報<br>
 
<img width="1886" height="797" alt="スクリーンショット 2026-04-21 131837" src="https://github.com/user-attachments/assets/39987aeb-ca70-4a2a-9af7-954260d3d263" />
<img width="1887" height="414" alt="スクリーンショット 2026-04-21 131853" src="https://github.com/user-attachments/assets/dbb822cd-f20f-4b17-961a-3f0eac7ed987" />
 
## 工夫した点
文字を見やすいように**大きめ**にしました。<br>
ヘッダーの背景色を項目ごとに変化するようにしました。<br>
画面のサイズによって見えやすいようにしました。<br>
はじめは、各情報の登録・修正・削除・検索機能、商品の入出荷処理、入出荷の履歴の表示などのシンプルな機能でしたが、ページネーション・商品画像・各種並べ替え機能（昇順・降順）を追加することで、ユーザーが操作しやすいように工夫しました。
