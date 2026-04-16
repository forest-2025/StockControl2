/**
 * cache.js
 *
 * ログアウト後にブラウザバックしたときに,キャッシュを無視してページを再読み込みすることで前の画面に戻らないようにする
 * (セッションが無効なのでログインページにリダイレクトされる)
 *
 * @example
 * <script th:src="@{/js/cache.js}" defer></script>
 * 
 */

"use strict";
$(function() {
	window.addEventListener("pageshow", function(event) {
		const nav = performance.getEntriesByType("navigation")[0];
  if (event.persisted || nav.type === "back_forward") {
    window.location.href = window.location.href.split('?')[0] + '?t=' + new Date().getTime();
  }
});

});
