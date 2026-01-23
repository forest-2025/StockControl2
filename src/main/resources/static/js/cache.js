/**
 * cache.js
 *
 * ログアウト後にブラウザバックしたときに
 * ページを再読み込みして表示させない
 *
 * @example
 * <script th:src="@{/js/cache.js}" defer></script>
 */
/*  ログアウト後もブラウザバックすると前の画面に戻れるのはブラウザのBack-ForwardCache,
	（BFCache(バックフォワードキャッシュ) ブラウザがページ全体のスナップショットを丸ごと保存して瞬時に戻る仕組みのこと)が有効になっているからで,
	<meta http-equiv="Cache-Control" content="no-store">では制御できない.
	そのためJavaScriptでページが表示されるイベント時にevent.persistedがtrue(BFCacheからの復元のとき.通常の読み込などはfalseになる)なら,
	そのページのURLにもう一度アクセス(つまり再読み込み)することにより,最新版のブラウザでもキャッシュを無視した再読み込みができる.
	location.reload(true);は非推奨.
	このjsはログアウトしたときに戻れないようにしたい画面に使用するのでlogin-layoutではそもそもログアウトができないため使用しない(意味がない).
	レイアウトcommon-layoutはlogoutがログアウト後の画面なのでログアウトできないことと,404エラーページではログアウトできないことと,
	errorページではBFCacheが無効になるブラウザが多いためspringsecurityのデフォルト設定で対応できることからこのjsを使用しなくてもよいが,
	BFCacheが無効にならないブラウザが存在することを考慮して使用している. 
*/
window.addEventListener("pageshow", function(event) {
	if (event.persisted) {
		window.location.href = window.location.href;
	}

});

/* 　windowはブラウザの「窓」そのもの を表すオブジェクト.
	addEventListenerは指定した要素でクリックやキー入力などの「イベント」が発生した際に,あらかじめ設定しておいた関数を実行させ,
	Webページに動き（インタラクティブな機能）を追加するためのメソッド.
	対象.addEventListener("イベント名", 処理の関数,useCapture);
	useCaptureはイベントの伝播方法を設定する.(falseはバブリング（親に伝わる）,trueはキャプチャ（親から順に伝わる).
		(例)
	
	  		お	|<div id="parent" style="padding: 20px; background: lightblue;">
    			|　　子|<button id="child">クリックしてね</button>
	　		や	|</div>
	
			const parent = document.getElementById("parent");	
			const child = document.getElementById("child");		
			parent.addEventListener("click", function() {
				alert("親の処理が先に実行される");
			}, true); 

			child.addEventListener("click", function() {
			alert("子の処理");
			});
	
		ここの親と子はHTMLの入れ子関係のこと.
		document.getElementById("parent")は,
			id がparentのHTML要素を探し,それをparentという変数に入れている.
			documentは今表示されているHTML全体を表すオブジェクト.
			getElementById("parent")はid="parent"が付いている要素を1つ探すメソッド.
			constは再代入不可の変数(参照先を変更できない変数.ただし参照先の要素の状態は変更できる
			(この例ならparentのstyle属性のbackgroundをredとかはできる).定数とはちょっと違う).
	
		このようなとき,子のボタンを押すとまず
		parent.addEventListener("click", function() {alert("親の処理が先に実行される");}, true);
		が実行される.これはメソッドの第三引数がtrue(キャプチャ（親から順に伝わる))のため,親の処理から行われている.
		(その後,child.addEventListener("click", function() {alert("子の処理");});が実行される).
		こちらの方法はあまり使わないため,デフォルトはfalse.
		falseの場合は子→親の順番に実行され,そもそも親の処理が設定されていないなら子の処理のみで完結する.
		
	pageshowはイベント名でページの表示のこと(はじめてページを開いた・リロード・ブラウザバックなど,ユーザーの画面にページが出た瞬間に起きるイベント).
	function()は処理のまとまり(本来はfunction hello() {console.log("こんにちは"); }).のような形だが,今回はその場でしか使用せず,
	ほかで使うことのない処理のため名前がない(無名関数)).
	eventは発生したイベントそのものの情報が入ったオブジェクト.
	ながれとしてはpageshowが起きたらこの関数を使うようにブラウザに登録しておく(このコード).
	ページが表示される（pageshowが発生）.
	ブラウザが登録されていた関数を呼び出す.
	そのときブラウザがイベントの情報を引数として渡す.
	というかんじになる.イベントの詳細（どの要素,どんな種類,bfcacheか等）はブラウザしか知らないためブラウザが自動的に渡してくれるため,こちらでは何もしなくてよい.
	また,eventとしているがべつにeとかでもいい.なにが渡されているかわかりやすいからeventが使用されることが多い.
	
	
	

 */
