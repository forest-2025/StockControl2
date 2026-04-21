# 在庫管理アプリ
商品の在庫を管理するアプリケーションです。

## 使用技術
・フロントエンド：HTML・CSS・Bootstrap・JavaScript・Thymeleaf

・バックエンド：Spring boot・Java・H2 Database

## 実行方法
1.リポジトリーを保存するフォルダに移動してリポジトリーをクローンしてください。
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
<img width="1891" height="743" alt="スクリーンショット 2026-04-21 115427" src="https://github.com/user-attachments/assets/d0b71662-3668-4dc1-ad70-9578ce10c919" />
<img width="1889" height="916" alt="スクリーンショット 2026-04-21 115405" src="https://github.com/user-attachments/assets/f6f66ff8-e6f8-45ed-9539-0e42aca7a02a" />

 
## 工夫した点
文字を見やすいように**大きめ**にしました。<br>
ヘッダーの背景色を項目ごとに変化するようにしました。<br>
