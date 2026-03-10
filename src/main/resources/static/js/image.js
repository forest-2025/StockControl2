"use strict";
$(function() {
    const maxFileSize = 20 * 1024 * 1024; // 20MB

    $('#uploadForm').on('submit', function(e) {//formのid
        $('#fileError').text(''); // 前回のエラーをクリア エラーメッセージのid

        const fileInput = $('#imageFile')[0];// inputタグからFileオブジェクトの１つ目を取り出す
        if (fileInput.files.length === 0) {
            return; // ファイルが選択されていない場合は何もしない
        }

        const file = fileInput.files[0];
        if (file.size > maxFileSize) {
            e.preventDefault(); // 送信を止める
            $('#fileError').text('アップロード可能なファイルサイズは5MBまでです');
        }
    });
});
$('#uploadForm').on('submit', function (event) {
    // 操作対象の要素を const で定義
    const $fileInput = $('#formFile');
    const file = $fileInput[0].files[0]; // jQueryオブジェクトから生のDOMを取り出し、1番目のファイルを取得
    const maxSize = 5 * 1024 * 1024;   // 制限サイズ（例: 5MB）

    // 1. 前回のバリデーション結果（赤枠）をリセット
    $fileInput.removeClass('is-invalid');

    // 2. ファイルが選択されている、かつサイズが制限を超えている場合
    if (file && file.size > maxSize) {
        // 3. サーバーへの送信を中止（Spring Boot側のエラーを防ぐ）
        event.preventDefault();
        
        // 4. Bootstrap 5 のエラー表示を適用
        $fileInput.addClass('is-invalid');
    }
});

/* <div id="jsFileError" class="invalid-feedback">
        ファイルサイズが大きすぎます。
    </div>べつでつける */