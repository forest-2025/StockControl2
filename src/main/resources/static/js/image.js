"use strict";
$('#uploadForm').on('submit', function (event) {
    const $fileInput = $('#productFile');
    const file = $fileInput[0].files[0]; 
    const maxSize = 20 * 1024 * 1024; 

    $fileInput.removeClass('is-invalid');


    if (file?.size > maxSize) {
        event.preventDefault();
        $fileInput.addClass('is-invalid');
    }
});

/*  id=uploadFormがついているタグの送信(submit)がクリックされることをトリガーにしたｊｓを設定している(submitイベントはformタグに使う.
buttonタグとかはclickイベント).
引数の(event)は何かが起きたときにブラウザが自動的に作成するその出来事に関する情報がはいっているイベントオブジェクト.
eventオブジェクトのメソッド,preventDefaultメソッドを使用したいため,引数で渡している.
画像ファイルのinputタグの値(選択された画像)を$fileInputに代入する($はjQueryのオブジェクトがはいっているという意味).
$fileInputの最初の画像を取り出してfileに代入する(fileはｊｓのFileオブジェクトでjQueryオブジェクトのままだと画像ファイルの中身にアクセスできないため).
$fileInput.removeClass('is-invalid');で$fileInputのタグ(id=productFile)のclass属性のis-invalidを消している.
これはこのあと画像サイズの大きさを見て画像サイズが大きければclass属性にis-invalidが付与されるが,
確定を押すのが2回目だとis-invalidがつけっぱなしのままになっているので,この後のコードのためにもここで一旦とっておく.
if (file?.size > maxSize)はもしfileがnullやundefinedだったらエラーを出さずにundefinedを返してmaxSizeと比較する.
(undefined > maxSizeとなり両方を数値に変換しようとするがmaxSizeは変換できるがundefinedは変換できずNaN（Not a Number：非数）,
と判定される.そして,jsにはNaNが絡む比較はどんな相手（自分自身を含む）であっても必ずfalseにするというきまりがあるため,falseになる).
fileがnullでもundefinedないならそのサイズがmaxSizeより大きいならtrue,そうでないならfalseという判別がなされる.
trueなら,event.preventDefault();でブラウザが行う次の動作(今回は確定ボタンが押されたためそれをサーバーに送るという動作)を強制的にキャンセルし,
$fileInputのタグ(id=productFile)のclass属性にis-invalidを付与している.
これにより,inputタグの下のdivタグのclass属性についているinvalid-feedbackが効果を発揮し(is-invalidがないと非表示),
エラーが表示される. */
