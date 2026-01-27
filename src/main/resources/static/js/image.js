/**
 * image.js
 *
 * 商品の詳細画面の画像表示を押して画像を表示する
 * 表示した画像をバツ印を押して非表示にする
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
	jQueryには2種類の機能があり,1つ目がDOMを操作するための機能.
	特定のDOM要素が存在するため,jQueryインスタンス.メソッド(インスタンスに対する操作)という形式になる(インスタンスメソッド).
	2つ目がDOMがなくても使えて,影響を与える特定のDOM要素が存在しないためjQueryオブジェクトを作る意味がない機能(DOMに依存しない汎用機能).
	この2つ目が静的メソッド($ 本体から直接呼ぶ,インスタンスを必要としていない)というが,設計・説明のための概念的な呼び方であってjsの文法用語ではない.
	ajaxは2つ目だがajaxはプロパティで,そのプロパティの値が関数なのでそのメソッドが呼び出されている.
	
	jQuery には大きく 2 種類の機能がある。

① DOM を操作するための機能
特定の DOM 要素が前提となるため、
$(selector) によって jQuery インスタンスを生成し、
そのインスタンスに対してメソッドを呼び出す
（＝インスタンスメソッド）。

② DOM がなくても使える汎用機能
特定の DOM 要素に紐づかず、
jQuery オブジェクト（インスタンス）を作る意味がないため、
$ 本体から直接呼び出す形になっている。

この ② を 静的メソッドと呼ぶことがあるが、
これは設計・説明のための概念的な呼び方であり、
JavaScript の文法用語ではない。

ajax は ② に該当し、
ajax 自体は $ のプロパティであり、
そのプロパティの値が関数なので、
結果としてその関数（＝メソッド）が呼び出されている。
	
	let oldUrl = null;ブラウザのメモリ上に一時的に存在するバイナリデータ（Blob）を参照するための特殊なURL
    */

