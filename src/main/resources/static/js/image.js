
/**
 * image.js
 *
 * application.properties
 * (セッションが無効なのでログインページにリダイレクトされる)
 *
 * @example
 * <script th:src="@{/js/image.js}" defer></script>
 * 
 */

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

