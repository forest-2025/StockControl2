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

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@RestController // REST API用のコントローラーで戻り値はHTMLではなくそのままHTTPレスポンスのBodyになる(@Controller + @ResponseBody).
public class AjaxController {

	@Autowired
	private ProductInfoService productInfoService;

	@Value("${file.upload-dir}")
	private String uploadDir;

	/* ResponseEntityはHTTPレスポンスの構成(ステータス・ヘッダー・ボディー)3つを全部まとめて返せるクラス.
	 * byte[]はbyte型の配列で画像ファイルはこの型で表すことができる(それぞれの保存形式（フォーマット）に従った「バイトの並び」になっている) */
	@GetMapping("/image/{productId}")
	public ResponseEntity<InputStreamResource> showImage(@PathVariable Integer productId,
			HttpServletResponse response) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);
		String filename = oneItem.getImage();
		/* Pathクラスはファイルシステム上のファイルやディレクトリのパス（経路）をオブジェクトとして表現し,
		 * そのパス情報（ファイル名、親ディレクトリなど）の取得,結合,比較といったパス自体の操作を行うためのクラス.
		 * ディレクトリと画像ファイル名を代入してファイルパスを表すオブジェクトを作成する */
		try {
			Path path = Path.of(uploadDir, filename);
			/* 取得した商品情報が存在するか,DBのimageに画像ファイル名が存在するか(nullじゃないか),pathの場所(/image/img.jpgもファイルシステム上の場所を指す)が存在するか確認し存在しなければエラー画面へ.
			 * (画像ファイルはnullでもいいがそもそもnullのときは画像表示のボタンが表示されないのでここにこれないため不正なアクセスとしてエラー画面に遷移させる).
			 * .badRequest()で400エラー(クライアントがサーバーに送ったリクエストが不正な形式であるためサーバーがそれを理解・処理できなかったことを示すHTTPステータスコード).
			 * ResponseEntityはBuilderパターン(複雑なオブジェクトを段階的に組み立てるための設計方法)でつくられていて,
			 * .badRequest()でオブジェクトの設定を行い.build()でオブジェクトを作成する.
			 * (Builderパターンでは設定と生成を分離し,.build()を呼んで初めてResponseEntityという実体を作る). */
			if (oneItem == null || filename == null || !Files.exists(path)) {

				return ResponseEntity
						.badRequest()
						.build();
			}

			System.out.println(filename);

			System.out.println(path);

			// HTTPレスポンスでクライアントにこのデータの種類を知らせるためにMIMEタイプをString型で取得する(タイプを特定できないとnullが返る).
			String contentType = Files.probeContentType(path);
			
			if (contentType == null) {
				return ResponseEntity
						.badRequest()
						.build();
			}
			MediaType mediaType = MediaType.parseMediaType(contentType);
			System.out.println(mediaType);
			//MediaType mediaType = MediaType.parseMediaType(contentType);
			/* 2MB以上になるかもしれないのでFiles.readAllBytes()でファイル全体をメモリ上に読み込むのではなく,
			 * 少しずつ読み込み・出力するストリーム方式でメモリ消費を最小限に抑えつつ大きなファイルや同時アクセスに対応できるようにする.
			 * (大きなファイル分のメモリを確保するより,小さいバッファ分だけ読み込んですぐに出力して消して読み込んでを繰り返すほうが安定していて多数のアクセス二も強い).
			 * Files.newInputStream(path)でバイト単位でpathのデータを順番に読み込む入口(ストリーム)を準備する.
			 * InputStreamResourceはInputStreamクラスのラッパークラス.InputStreamクラスのオブジェクトを保持する.
			 * Springが必要なタイミングでこのInputStreamから少しずつデータをバッファに読み込み,OutputStreamに書き出しいっぱいになったらHTTPレスポンスとしてクライアントに送信を繰り返す. 
			 * OutputStreamはSpringがResponseEntityやResourceを返すとき,内部でOutputStreamに書き込む処理を行い,.
			 * OutputStreamは内部的にバッファを持っていることが多いためSpringが裏で用意してくれているので何もしない.
			 * (org.springframework.web.context.request.async.StandardServletAsyncWebRequest$LifecycleServletOutputStreamが使用されている.
			 * buffer size = 8192(8KB)) */
			InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

			ServletOutputStream out = response.getOutputStream();
			System.out.println(out.getClass().getName());
			int bufSize = response.getBufferSize();
			System.out.println("buffer size = " + bufSize);
			return ResponseEntity.ok()
					.contentType(mediaType)
					.body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity
					.internalServerError()
					.build();
		}
	}
}
