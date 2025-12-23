/**
 * 
 */
// image.js
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

	// ×ボタンで非表示
	$("#closeImageBtn").click(function() {
		$("#resultImage").addClass("d-none");
		$("#closeImageBtn").addClass("d-none");
	});
});
