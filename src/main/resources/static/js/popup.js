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
	console.log(popup);
	$("#popup-trigger").on("click", function() {
		console.log("クリックされた");
		popup.addClass("show")
			.fadeIn();
	});

	$("#close").on("click", function() {
		popup.fadeOut();
	});
});

/*  constで再代入しない値(参照先は変更しないが参照先の「中身の状態」は変更できる)を入れられるようになる. 

	id="popup-trigger" の要素(spanタグ)がクリックされたときに実行する処理を登録する.
	popup.addClass("show")でclass属性に"show"を追加することでCSSで定義された.showのスタイルを適用させている.
	.fadeIn();はHTML要素の不透明度（opacity）を時間をかけて上げ,徐々に表示させるアニメーションメソッド.
	(display: none;などで隠れている要素をふんわり表示する).
	
	fadeOut()メソッドは,HTML要素を徐々に透明にしながら（フェードアウト）,
	最終的に非表示（display: none;）にするためのアニメーションメソッド.

 */