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

import com.example.domain.products.service.ProductInfoService;
import com.example.dto.products.ProductList;

import lombok.extern.slf4j.Slf4j;

/**
 * Ajax通信で画像を表示するためのコントローラクラス.
 * 
 */
@RestController // REST API用のコントローラーで戻り値はHTMLではなくそのままHTTPレスポンスのBodyになる(@Controller + @ResponseBody).
@Slf4j
public class AjaxController {

	@Autowired
	private ProductInfoService productInfoService;

	@Value("${file.upload-dir}")
	private String uploadDir;

	// 代替画像のファイル名.
	private final String altImage = "no_image.jpg";

	// データの種類を表すMIMEタイプをJPEGにする.
	private final String strMimeType = "image/jpeg";

	/**
	 * 商品詳細画面の画像表示ボタンを押してくるところ.
	 *
	 * @param productId 画像表示したい商品のID.
	 * @return HTTPレスポンスの構成(ステータス・ヘッダー・ボディー)を返す.
	 */
	// ResponseEntityはHTTPレスポンスの構成(ステータス・ヘッダー・ボディー)3つを全部まとめて返せるクラス.
	@GetMapping("/image/{productId}")
	public ResponseEntity<InputStreamResource> showImage(@PathVariable Integer productId) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);
		String fileName = oneItem.getProductImage();

		/* 取得した商品情報が存在するか,DBのimageに画像ファイル名が存在するか(nullじゃないか),空じゃないかを確認し存在しなければ404エラーを設定する.
		 * (画像ファイルはnullでもいいがそもそもnullのときは画像表示のボタンが表示されないし,空のときもないと思うが念のため). */
		if (oneItem == null || fileName == null || fileName.isEmpty()) {

			/* ResponseEntityはBuilderパターン(複雑なオブジェクトを段階的に組み立てるための設計方法)でつくられていて,
			 * .status(404)はレスポンスのHTTPステータスコードを「404」に設定するメソッドで,URLは正しいがその場所に見つかるべきファイルやコンテンツがないことを示す.
			 * (Builderパターンでは設定と生成を分離し,.build()を呼んで初めてResponseEntityという実体を作る). */
			return ResponseEntity.status(404) // HTTPステータスコード404 Not Found (ページがない)を設定する.
					.build();
		}

		// try文の外でも使うため先に宣言しておく.
		MediaType mediaType = MediaType.parseMediaType(strMimeType);

		try {
			/* ディレクトリパスとDBに保存されているファイル名を結合した結合したパスを表すPathインスタンスを作成する.
			 * Pathクラスはファイルシステム上のファイルやディレクトリのパス（経路）をオブジェクトとして表現し,
			 * そのパス情報（ファイル名、親ディレクトリなど）の取得・結合・比較といったパス自体の操作を行うためのクラス.
			 * ディレクトリと画像ファイル名を代入してファイルパスを表すオブジェクトを作成する 
			 * ((uploadDirや)fileNameがnullだとNullPointerExceptionになるので上で確認している). */
			Path path = Path.of(uploadDir, fileName);

			/* pathの場所が存在しないか確認する(存在するならやることはないため).
			 * (DBのimageとおなじファイル名がuploadになければtrueになる). */
			if (!Files.exists(path)) {
				// pathの場所がないときは代替画像を表示する.
				path = Path.of(uploadDir, altImage);
			}

			/* 2MB以上になるかもしれないのでFiles.readAllBytes()でファイル全体をメモリ上に読み込むのではなく,
			 * 少しずつ読み込み・出力するストリーム方式でメモリ消費を最小限に抑えつつ大きなファイルや同時アクセスに対応できるようにする.
			 * (大きなファイル分のメモリを確保するより,小さいバッファ分だけ読み込んですぐに出力して消して読み込んでを繰り返すほうが安定していて多数のアクセスにも強い).
			 * Files.newInputStream(path)でバイト単位でpathのデータを順番に読み込む入口(ストリーム)を準備する.
			 * InputStreamResourceはInputStreamクラスのラッパークラス.InputStreamクラスのオブジェクトを保持する(Springにとって扱いやすくしている). */
			InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

			/* .ok()でHTTPステータスコードを200 OKに指定する.
			 * .contentType(mediaType)で HTTPヘッダーのフィールドのContent-Typeを指定する.
			 * .body(resourceでHTTPのボディ部分に送信するデータ本体を設定する.
			 * これで,画像データを流す(読みだす)準備が完了する.
			 * DispatcherServlet(クライアントからのリクエストを受信し,適切なハンドラ（Controller）に処理を委譲した後,
			 * 最終的なレスポンスを生成するサーブレット)がレスポンスを処理する.
			 * ResourceHttpMessageConverterがストリームを開いて情報を取得する(内部的にresource.getInputStream()).
			 * 取得したInputStreamからデータを読み取り,HttpOutputMessageのOutputStreamへコピー（転送）する.
			 * (InputStreamResourceの場合,getInputStream()を呼ぶと「元のストリームそのもの」を返すため,
			 * コンバーターが読み取った時点でストリームは消費され,その後は二度と読み取ることができなくなる).
			 * コピーが完了した後,ResourceHttpMessageConverterは取得したInputStreamをクローズする.
			 * コンバーターが処理を終えると,Springのフレームワーク側でHTTPレスポンスのOutputStreamもflush()
			 * （フラッシュ.バッファリングされていたすべての出力バイトを強制的に書き込む(小さいバッファ分だけ読み込んですぐに出力するが,
			 * その小さいバッファ分に満たずに終了したとき送り残しがでるので,それをふせぐために行う)).
			 * クライアントへの送信が完了する. */
			return ResponseEntity.ok()
					.contentType(mediaType)
					.body(resource);

		} catch (Exception e) {
			
			// DBのimageが空(nullじゃなくて空)のときなどもここに来る.
			log.info("画像取得エラー", e);
			try {
				Path path = Path.of(uploadDir, altImage);
				mediaType = MediaType.parseMediaType(strMimeType);
				InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
				return ResponseEntity.ok()
						.contentType(mediaType)
						.body(resource);

			} catch (Exception ex) {
				// 上のcatchでエラーになったときのcatch.
				log.info("画像取得失敗", ex);
				return ResponseEntity
						.internalServerError() // 500エラー.
						.build();
			}
		}
	}
}
