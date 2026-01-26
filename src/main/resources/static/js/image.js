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
		var productId = $(this).data("product-id");
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
/* 	$(function() はjQueryの「DOMが完全に読み込まれたら実行」する書き方.ページ内のHTMLが読み込まれてから中の処理を実行することで,
	まだDOMがない状態で$("#showImageBtn")を操作してエラーになるのを防げる.
	$はjQueryの省略したもの(jQuery(...)といっしょ).DOMを探して,jQueryオブジェクトとして返す関数のこと.
	#はidセレクタ(id属性を持つ要素を指定するためのCSSセレクタ)を指定している.$("#showImageBtn")でid = showImageBtnの要素,
	画像表示ボタンのbuttonタグを指定している.
	.onメソッドはHTML要素に対してクリックやホバーなどのイベントハンドラ（動作）を登録するメソッド(基本形は $(セレクタ).on("イベント名", 関数);).
	イベント名はブラウザがあらかじめ用意しているものが決まって存在する(自作もできる).イベント名clickはクリックしたときの動作の登録となる.
	let oldUrl = null;ブラウザのメモリ上に一時的に存在するバイナリデータ（Blob）を参照するための特殊なURL
    */

