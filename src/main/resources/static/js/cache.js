/**
 * cache.js
 *
 * ログアウト後にブラウザバックしたときに
 * ページを再読み込みして表示させない
 *
 * @example
 * <script th:src="@{/js/cache.js}" defer></script>
 */
//  ログアウト後もブラウザバックすると前の画面に戻れるのはブラウザのBack-ForwardCache,
//	（BFCache(バックフォワードキャッシュ) ブラウザがページ全体のスナップショットを丸ごと保存して瞬時に戻る仕組みのこと)が有効になっているからで,
//	<meta http-equiv="Cache-Control" content="no-store">では制御できない.
//	そのためjavascriptでページが表示されるイベント時にevent.persistedがtrue(BFCacheからの復元のときで通常の読み込などはfalseになる)なら,
//	そのページのURLにもう一度アクセス(つまり再読み込み)することにより,最新版のブラウザでもキャッシュを無視した再読み込みができる.
//	location.reload(true);は非推奨.
//	このjsはログアウトしたときに戻れないようにしたい画面に使用するのでlogin-layoutではそもそもログアウトができないため使用しない(意味がない).
//	レイアウトcommon-layoutはlogoutがログアウト後の画面なのでログアウトできないことと,404エラーページではログアウトできないことと,
//	errorページではBFCacheが無効になるブラウザが多いためspringsecurityのデフォルト設定で対応できることからこのjsを使用しなくてもよいが,
//	BFCacheが無効になるブラウザでないことも考慮して使用している.
window.addEventListener("pageshow", function(event) {
	if (event.persisted) {
		window.location.href = window.location.href;
	}
});