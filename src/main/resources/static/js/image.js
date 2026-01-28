/**
 * image.js
 *
 * 商品の詳細画面の画像表示を押して画像を表示する.
 * 表示した画像をバツ印を押して非表示にする.
 *
 * @example
 * <script th:src="@{/js/image.js}" defer></script>
 * 
 */

$(function() {
	// 前回のBlob URLを保持（メモリ解放用）
	let oldUrl = null;

	$("#showImageBtn").on("click", function() {
		const productId = $(this).data("product-id");
		$.ajax({
			url: "/image/" + productId,
			method: "GET",
			xhrFields: {
				responseType: "blob"
			},
			success: function(blob) {
				// 以前のURLを解放
				if (oldUrl) {
					URL.revokeObjectURL(oldUrl);
				}

				// 新しいBlob URLを作成して表示
				const url = URL.createObjectURL(blob);
				$("#resultImage")
					.attr("src", url)
					.removeClass("d-none");
				$("#closeImageBtn")
					.removeClass("d-none");

				// 今回のURLを保持
				oldUrl = url;

			},
			error: function() {
				alert("画像の取得に失敗しました");
			}
		});

	});

	// ×ボタンで非表示にする.
	$("#closeImageBtn").click(function() {
		if (oldUrl) {
			URL.revokeObjectURL(oldUrl);
			oldUrl = null;
		}
		$("#resultImage").addClass("d-none");
		$("#closeImageBtn").addClass("d-none");
	});
});
//$(function () {
//    $.ajax({
//        url: "/images/sample.png",
//        method: "GET",
//        xhrFields: { responseType: "blob" }
//    })
//    .done(function (blob) {
//        const url = URL.createObjectURL(blob);
//        $("#image").attr("src", url);
//    })
//    .fail(function (jqXHR) {
//        $("#image").attr("src", "/images/default.png");
//    });
//});

/* 	jQueryを使用している.
	jQueryはブラウザがHTMLを読み込みjsでも操作できるよう生のDOM（DOMオブジェクト）に変換する.
	それをjQueryでラップすることで,jQueryの便利なメソッドが使えるようになる.
	ユーザーがURLにアクセスする. → サーバがHTMLをレスポンスする. → ブラウザがHTMLを読み込み,jsを発見する(<script src="jquery.js"> みたいなの). 
	→  ブラウザがjquery.jsを自動リクエストする. → jquery.jsレスポンスを受け取ったらブラウザが読み込んで解析・実行を行いjQueryが使用できるように準備する.
	→ 準備が整いjQueryが使用できる.
	
	jsの関数はすべて関数オブジェクト(関数でありながらオブジェクトでもあるもの.jsでは関数もオブジェクトの一種として扱われるため,値として変数に代入できたり,
	プロパティを持たせることができたり,メソッドを持たせることができたりする).
	
	$(function() はjQueryの「DOMが完全に読み込まれたら実行」する書き方.ページ内のHTMLが読み込まれてから中の処理を実行することで,
	まだDOMがない状態で$("#showImageBtn")を操作してエラーになるのを防げる.
	$はjQueryの省略したもの(jQuery(...)といっしょでエイリアス(別名・ニックネームのようなもの))でこれ自体はグローバル関数.
	(プログラム全体からどこからでも呼び出し（アクセス）可能な関数のこと).
	
	#はidセレクタ(id属性を持つ要素を指定するためのCSSセレクタ)を指定している.$("#showImageBtn")でid = showImageBtnの要素,
	画像表示ボタンのbuttonタグを指定していて,これでbuttonタグををラップしたjQueryオブジェクトを返すことができる.
	
	.onメソッドはjQueryオブジェクトのメソッドで,HTML要素に対してクリックやホバーなどのイベントハンドラ（動作）を登録するメソッド.
	(基本形は $(セレクタ).on("イベント名", 関数);).
	イベント名はブラウザがあらかじめ用意しているものが決まって存在する(自作もできる).イベント名clickはクリックしたときの動作を表す.
	$(function{ ～ alert("画像の取得に失敗しました"); } });までが.onメソッドの第2引数になる.
	画像表示ボタンをクリックしたら、function()の｛｝の中の処理を実行するように登録している.
	
	const productIdで変数productIdに再代入しない値(参照先を変更しない)を入れれるようになる.
	$(this)はイベントが発生した要素そのもの(ここでは#showImageBtnのこと)を指す.
	.data("product-id")はjQueryオブジェクトの.data()メソッドのことで,
	引数に生のDOMにあるdata-* 属性(ここでは(th:)data-product-idのことを指す)を入れることで,
	その属性の値を取得したり,操作したりできる.今回は値を取得して変数productIdに代入している.
	
	$(セレクタ).メソッドと$.メソッドは違う.
	$()は引数にCSSセレクタを渡すと,そのセレクタに一致するDOM要素をラップしたjQueryオブジェクトが返り,jQueryオブジェクト共通のインスタンスメソッドが使える.
	$.メソッドは$が関数オブジェクトなのでオブジェクト.静的メソッド(インスタンス（オブジェクト）を作成せずに直接呼び出せるメソッドのこと)という形式.
	オブジェクト.メソッドなのでただのメソッド呼び出しのようにみえるが少し違う.
	jQueryには大きく2種類の機能がある.
	①DOMを操作するための機能
		特定のDOM要素が前提となるため,$(セレクタ)によってjQueryインスタンスを生成し,そのインスタンスに対してメソッドを呼び出す（インスタンスメソッド）。
	②DOMがなくても使える汎用機能
		特定のDOM要素に紐づかず,jQueryオブジェクト（インスタンス）を作る意味がないため,$ 本体から直接呼び出す形になっている.
		この②を静的メソッドと呼ぶことがあるが,これは設計・説明のための概念的な呼び方であり,jsの文法用語ではない.
	ajaxは②に該当し,ajax自体は$のプロパティであり,そのプロパティの値が関数のため結果としてその関数（＝メソッド）が呼び出されている.
	
	Ajaxとは画面表示中にウェブページ全体を再読み込みせずに,jsが裏でサーバーと非同期で通信し,必要なデータだけ受け取り画面の一部だけ更新する技術のこと.
	画面（HTML）のボタンを押す→jsが「通信しろ」と命令する(このコード)→
	XHR(XMLHttpRequest はブラウザ上のjsからサーバーへHTTPリクエストを送るための仕組み（API 作る・送る・受け取る))が,
	HTTPリクエストを作成して送る→サーバー(AjaxControllerクラスの@GetMapping("/image/{productId}"))にいくという流れ.
	url: でどこのURLに, method: でどのHTTPメソッドで,xhrFields: {} でどんなものを取りに行くのかを設定している.
	(これらはjQueryのajax()メソッドのオプションで,ajax()メソッドに渡す設定値のこと).
	XHRを操作するためにXMLHttpRequestクラスあり,そのオブジェクトがXMLHttpReques（XHR）オブジェクト.これによりXHRを操作する.
	xhrFields（エックスエイチアール・フィールズ）は,ajax()メソッドのオプションの一つで,内部で生成されるXHRオブジェクトのプロパティを設定できる.
	これにより,HTTPリクエストの通信状態による処理の変更や,レスポンスの受け取り方,HTTPリクエストに関する詳細な設定などを指定できる.
	xhrFields:{}の{}はjsのオブジェクトリテラル({}を使って,コード内で直接オブジェクトを定義・生成する方法のこと).
	{}内がXHRオブジェクトの各プロパティにどんな値を設定するかを対応付けられる箱となり,中に「プロパティ名 : 値」とすることで,
	jQueryはAjaxの内部でXHRオブジェクトの同名プロパティを設定する.
	responseTypeはXHRオブジェクトのプロパティの1つで,サーバーから返ってきたレスポンス（データ）をどの形式で受け取るかを指定するもの.
	"blob"はバイナリデータ（画像やPDFなど）として受け取ることを指定している.
	(Binary Large OBject（バイナリ(コンピュータが扱う「0と1の並び」で構成されたデータ)の大きなオブジェクト） の略).
	success:はHTTPリクエストが成功したときに実行される関数を設定するjQueryのajax()のオプション.
	AjaxでresponseType: "blob" を使うと,サーバーから返ってきたバイナリデータをブラウザがBlobオブジェクトにラップ(変換)する.
	このオブジェクトを慣習的にblobといい(別に何でもいい,便宜上の名前)関数の引数に渡されている.
	URLはブラウザが提供する組み込みのグローバルオブジェクト（クラスのようなもの）.jsの標準オブジェクトで,URLを扱うための便利なメソッドがいくつか用意されている.
	$とは使い方が似ているが,$はjQueryの便利関数,URLはブラウザ組み込みオブジェクトなので注意.
	createObjectURL()メソッドはURLが持つメソッドで,ブラウザのメモリ上だけで使える一時的でblob: で始まる特殊なURL文字列を返すメソッド.
	(サーバーから受け取ったBlob（バイナリデータ）やFileオブジェクトは直接<img>や<a>に設定できないため,ブラウザが内部でそのデータを一時的に保存し,
	そのデータを参照するための特殊なURLを作る必要がある.文字列自体にデータが入っているのではなく,ブラウザが内部でこの文字列とBlobオブジェクトを紐付けているので,
	文字列からメモリ上のBlobオブジェクトへ参照できるようになっている).
	$("#resultImage").attr("src", url)でid属性がresultImageの要素(imgタグ)にsrc属性を追加または上書き）して,
	その値に変数urlの文字列を代入している.
	.removeClass("d-none");でclass属性のd-noneを削除している(もしclass属性に"d-none"が含まれていなければ何も起きない).
	error:はjQueryのajax()メソッドのオプションの一つ.HTTPリクエストが失敗したとき(サーバーが404や500を返したとやネットワークエラーが発生したなど),
	実行される関数（コールバック関数）を指定する.
	alert()はjsの組み込み関数で画面にポップアップのダイアログを表示するために使う.
	
	let oldUrl = null;ブラウザのメモリ上に一時的に存在するバイナリデータ（Blob）を参照するための特殊なURL
    */

