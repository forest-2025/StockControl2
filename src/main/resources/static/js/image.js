"use strict";
$('#uploadForm').on('submit', function (event) {
    // 操作対象の要素を const で定義
    const $fileInput = $('#productFile');
    const file = $fileInput[0].files[0]; // jQueryオブジェクトから生のDOMを取り出し、1番目のファイルを取得
    const maxSize = 0 * 1024 * 1024;   // 制限サイズ（例: 5MB）

    // 1. 前回のバリデーション結果（赤枠）をリセット
    $fileInput.removeClass('is-invalid');

    // 2. ファイルが選択されている、かつサイズが制限を超えている場合
    if (file?.size > maxSize) {
        // 3. サーバーへの送信を中止（Spring Boot側のエラーを防ぐ）
        event.preventDefault();
        
        // 4. Bootstrap 5 のエラー表示を適用
        $fileInput.addClass('is-invalid');
    }
});
