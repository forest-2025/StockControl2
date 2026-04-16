/**
 * popup.js
 *
 * パスワードで使用できる文字をポップアップで表示する.
 * ポップアップを閉じるボタンで閉じる.
 *
 * @example
 * <script th:src="@{/js/popup.js}" defer></script>
 */
"use strict";
$(function() {
	const popup = $(".popup");
	$("#popup-trigger").on("click", function() {
		popup.addClass("show");
			
	});

	$("#close").on("click", function() {
		popup.removeClass("show");
	});
});
