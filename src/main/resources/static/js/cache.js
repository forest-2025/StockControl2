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
/*  ログアウト後もブラウザバックすると前の画面に戻れるのはブラウザのBack-ForwardCache,
	（BFCache(バックフォワードキャッシュ) ブラウザがページ全体のスナップショットを丸ごと保存して瞬時に戻る仕組みのこと)が有効になっているからで,
	<meta http-equiv="Cache-Control" content="no-store">では制御できない.
	そのためJavaScriptでブラウザのキャッシュを無視してページを強制的に再読み込みする.
	このjsはログアウトしたときに戻れないようにしたい画面に使用するのでlogin-layoutではそもそもログアウトができないため使用しない(意味がない).
	レイアウトcommon-layoutはlogoutがログアウト後の画面なのでログアウトできないことと,404エラーページではログアウトできないことと,
	errorページではBFCacheが無効になるブラウザが多いためspringsecurityのデフォルト設定で対応できることからこのjsを使用しなくてもよいが,
	BFCacheが無効にならないブラウザが存在することを考慮して使用している. 
*/

window.addEventListener("pageshow", function (event) {
  const nav = performance.getEntriesByType("navigation")[0];
  if (event.persisted || nav.type === "back_forward") {
    window.location.href = window.location.href.split('?')[0] + '?t=' + new Date().getTime();
  }
});


/* 　windowはブラウザの「窓」そのもの を表すオブジェクト.
	addEventListenerは指定した要素でクリックやキー入力などの「イベント」が発生した際にあらかじめ設定しておいた関数を実行させ,
	Webページに動き（インタラクティブ(人やシステムが互いに作用し合う状態.ユーザーの操作に対しシステムが表示を変えるなど.)な機能）を追加するためのメソッド.
	対象.addEventListener("イベント名", 処理の関数,useCapture);
	useCaptureはイベントの伝播方法を設定する.(falseはバブリング（親に伝わる）,trueはキャプチャ（親から順に伝わる).
		(例)
	
	  		お	|<div id="parent" style="padding: 20px; background: lightblue;">
    			|　　子|<button id="child">クリック</button>
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
	
		このようなとき子のボタンを押すとまず,
		parent.addEventListener("click", function() {alert("親の処理が先に実行される");}, true);
		が実行される.これはメソッドの第三引数がtrue(キャプチャ（親から順に伝わる))のため,親の処理から行われている.
		(その後,child.addEventListener("click", function() {alert("子の処理");});が実行される).
		こちらの方法はあまり使わないため,デフォルトはfalse.
		falseの場合は子→親の順番に実行され,そもそも親の処理が設定されていないなら子の処理のみで完結する.
		
	pageshowはイベント名でページの表示のこと(はじめてページを開いた・リロード・ブラウザバックなど,ユーザーの画面にページが出た瞬間に起きるイベント.
	DOMContentLoadedやload とは異なり,BFCache復帰時も確実に呼ばれる).
	
	function()は処理のまとまり(本来はfunction hello() {console.log("こんにちは"); }).のような形だが,今回はその場でしか使用せず,
	ほかで使うことのない処理のため名前がない(無名関数)).
	
	performanceはjsからPerformance APIを操作するためのオブジェクト. 
	Performance APIはページの読み込みやリソース取得・遷移・描画などのパフォーマンス情報を計測・保持するためのAPIでこのオブジェクトからアクセスすることができる.

	getEntriesByType(type)メソッドは引数で指定した種類のパフォーマンス計測データをすべて配列で取得するメソッド.
	今回は引数が"navigation"なのでPerformanceNavigationTimingオブジェクトの配列が返る.
	(このオブジェクトはブラウザのPerformance APIが提供するページナビゲーション(ブラウザがユーザーを別のページに移動させる操作やページ遷移のこと)
	に関する詳細な計測情報をまとめたオブジェクト).
	そのオブジェクトの配列の[0]が変数navにはいる.
	つまり,1回のページ遷移に関する詳細な計測情報オブジェクトの配列の[0]が返っている.
	[0]なのは現在表示中のページのナビゲーション情報しか保持していないため.
	なぜ配列なのかというと,APIが将来的に複数のナビゲーション情報を保持する可能性を考慮した設計になっているから.
	
	eventは発生したイベントそのものの情報が入ったオブジェクト.
	ながれとしては,
	1.ページを離れる時(ブラウザバックしたとき)にそれぞれのブラウザ基準でできそうならスナップショットをつくる.
	2.遷移したらスナップショットを復元する(できないこともある).
	3.pageshowが発火して(このコードがあるから),イベントオブジェクトが引数に渡される.
	というかんじになる.イベントの詳細（どの要素,どんな種類,BFCacheか等）はブラウザしか知らないためブラウザが自動的に渡してくれるためこちらでは何もしなくてよい.
	またeventとしているがべつにeとかでもいい.なにが渡されているかわかりやすいからeventが使用されることが多い.
	そのイベントオブジェクトのプロパティの一つがpersistedでBFCacheならtrue,そうじゃないならfalseをもつ.
	ただし,このプロパティをtrueにするかfalseにするかはそれぞれのブラウザやバージョンの基準によるためこれがfalseだからといってBFCacheではないということではない.
	そのため,nav.type === "back_forward"で1回のページ遷移に関する詳細な計測情報オブジェクトのtypeプロパティがback_forwardか確認している.
	(typeプロパティはこのページ遷移がどの種類かを表す値を保持するプロパティ.BFCacheを含むブラウザの戻る・進むボタンによる遷移の場合,値がback_forwardになる).
	
	BFCacheなら,
	windowオブジェクトのプロパティlocation（location型のオブジェクトでURL情報を保持する）のhrefプロパティ(現在のページの完全なURL（文字列）をもつ),
	つまりブラウザで現在表示しているページのURLに,ブラウザで現在表示しているページのURLをsplitメソッドでURLをメイン部分とクエリパラメータ部分とに分割し,
	[0]でURLのメイン部分のみ取得することでクエリパラメータ部分を削除する。そのメイン部分だけのURLに文字列"？t="をつけることでtクエリパラメータを新しく作り,
	その値を現在のタイムスタンプ（1970年1月1日からのミリ秒）にすることで,ブラウザに新しいページとして認識させキャッシュを回避する.
	window.location.hrefが今見ているページのURLなので,それに代入するということは新しいURLに遷移する操作とブラウザは認識する.
	これにより,ブラウザはサーバーに対して新しいリクエストを送るか,キャッシュを確認してページを表示する.
	このとき代入するURLが以前と同じURLであれば,ブラウザはキャッシュを使う可能性がある(キャッシュはURL単位で保存される).
	そのため'?t=' + new Date().getTime();で現在のタイムスタンプをURLの末尾につけることで別のURLと認識させられる.
	別のURLならブラウザは必ずサーバーにリクエストを送ることになるが,springsecurityの.invalidateHttpSession(true)と,
	.deleteCookies("JSESSIONID")で現在のHTTPセッションの破棄・ブラウザ側のクッキーの削除が行われているため,ユーザーは未認証状態と判断され,
	(直リンク状態?).loginPage("/login")で設定したURLにリダイレクトする.
	別のURLにしたいだけなら'?t=' + new Date().getTime()ではなく適当に"a"とかじゃダメなのかと思うが,タイムスタンプをしようすることで,
	一意の(安全な)URLになるためこちらがベスト.
	ほかにも方法はあるが,
	location.reload(true);	非推奨.
	location.reload();		ページをリロードするがキャッシュ利用の可能性あり.
	のためこの方法とする.
 */
