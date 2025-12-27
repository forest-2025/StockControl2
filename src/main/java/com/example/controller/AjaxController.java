package com.example.controller;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.products.model.ProductList;
import com.example.domain.products.service.ProductInfoService;

@RestController // REST API用のコントローラーで戻り値はHTMLではなくそのままHTTPレスポンスのBodyになる(@Controller + @ResponseBody).
public class AjaxController {

	@Autowired
	private ProductInfoService productInfoService;

	@Value("${file.upload-dir}")
	private String uploadDir;

	/* ResponseEntityはHTTPレスポンスの構成(ステータス・ヘッダー・ボディー)3つを全部まとめて返せるクラス.
	 * byte[]はbyte型の配列で画像ファイルはこの型で表すことができる(それぞれの保存形式（フォーマット）に従った「バイトの並び」になっている) */
	@GetMapping("/image/{productId}")
	public ResponseEntity<InputStreamResource> showImage(@PathVariable Integer productId) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);
		String filename = oneItem.getImage();

		/* 取得した商品情報が存在するか,DBのimageに画像ファイル名が存在するか(nullじゃないか)を確認し存在しなければ404エラーを設定する.
		 * (画像ファイルはnullでもいいがそもそもnullのときは画像表示のボタンが表示されないが念のため).
		 * HTTPステータスコード404 Not Found を設定*/
		/* ResponseEntityはBuilderパターン(複雑なオブジェクトを段階的に組み立てるための設計方法)でつくられていて,
		* .notFound()でオブジェクトの設定を行い.build()でオブジェクトを作成する.
		* (Builderパターンでは設定と生成を分離し,.build()を呼んで初めてResponseEntityという実体を作る). */

		if (oneItem == null || filename == null) {

			return ResponseEntity.status(404) // HTTPステータスコード404 Not Found (ページがない)を設定する.
					//.header("Error-Reason", "deleted")		// レスポンスヘッダにカスタムヘッダError-Reason(Xを先頭につけるのは非推奨)を追加し値をdeletedで設定する.
					.body(null); // レスポンスボディをnullに設定する.
		}

		// try文の外でも使うため先に宣言しておく.
		MediaType mediaType = null;

		/* try文の外でも使うため先に宣言しておく.
		 * Pathクラスはファイルシステム上のファイルやディレクトリのパス（経路）をオブジェクトとして表現し,
		 * そのパス情報（ファイル名、親ディレクトリなど）の取得,結合,比較といったパス自体の操作を行うためのクラス.
		 * ディレクトリと画像ファイル名を代入してファイルパスを表すオブジェクトを作成する */

		Path path = null;
		try {

			path = Path.of(uploadDir, filename);
			if(path == null) {
				
			}
			// HTTPレスポンスでクライアントにこのデータの種類を知らせるためにMIMEタイプをString型で取得する(タイプを特定できないとnullが返る).
			String contentType = null;

			/* pathの場所(/image/img.jpgもファイルシステム上の場所を指す)が存在するか確認する.
			 * (Files.exists(path)は引数(pathのこと)がnullのときnullpoint).*/
			if (Files.exists(path)) {
				contentType = Files.probeContentType(path);
				// String型で取得したMINEタイプをMediaType型へ変更する.
				mediaType = MediaType.parseMediaType(contentType);
			} else {
				// pathの場所がないときは代替画像を表示する.
				path = Path.of(uploadDir, "no_image.jpg");
				// MINEタイプをJPEGにしてMediaType型で設定する.
				mediaType = MediaType.parseMediaType("image/jpeg");
			}

			/* 2MB以上になるかもしれないのでFiles.readAllBytes()でファイル全体をメモリ上に読み込むのではなく,
			 * 少しずつ読み込み・出力するストリーム方式でメモリ消費を最小限に抑えつつ大きなファイルや同時アクセスに対応できるようにする.
			 * (大きなファイル分のメモリを確保するより,小さいバッファ分だけ読み込んですぐに出力して消して読み込んでを繰り返すほうが安定していて多数のアクセスにも強い).
			 * Files.newInputStream(path)でバイト単位でpathのデータを順番に読み込む入口(ストリーム)を準備する.
			 * InputStreamResourceはInputStreamクラスのラッパークラス.InputStreamクラスのオブジェクトを保持する(Springにとって扱いやすくしている). */
			InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

			/* ResponseEntityはBuilderパターン(複雑なオブジェクトを段階的に組み立てるための設計方法)でつくられていて,
			* .contentType(mediaType)などでオブジェクトの設定を行い.build()でオブジェクトを作成する.
			* (Builderパターンでは設定と生成を分離し,.build()を呼んで初めてResponseEntityという実体を作る). */
			return ResponseEntity.ok()
					.contentType(mediaType)
					.body(resource);

		} catch (Exception e) {
			
			e.printStackTrace();
			// if文でfalseで画像のpathの設定とMediaTypeへの変更をしているときにエラーになったときに画像取得できなかったときなどのcatch.
			try {
				path = Path.of(uploadDir, "no_image.jpg");
				mediaType = MediaType.parseMediaType("image/jpeg");
				InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
				return ResponseEntity.ok()
						.contentType(mediaType)
						.body(resource);
			} catch (Exception ex) {
				// 上のcatchでエラーになったときのcatch.
				ex.printStackTrace();
				return ResponseEntity
						.internalServerError() // 500エラー.
						.build();
			}
		}
	}
}
