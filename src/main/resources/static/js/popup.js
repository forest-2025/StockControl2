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
	const $popup = $(".popup");
	
	$("#popup-trigger").on("click", function(e) {
		$popup.addClass("show")
			.fadeIn();
	});

	$("#close").on("click", function(e) {
		$popup.fadeOut();
	});
});
/*
$("#popup-trigger").on("click", function() { $(".popup") .addClass("show") .fadeIn(); // return false; }); $("#close").on("click", function() { $(".popup").fadeOut(); // return false; });*/

/* id="popup-trigger" の要素()がクリックされたときに処理を実行す */