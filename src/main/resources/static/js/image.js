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