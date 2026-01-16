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

	// 代替画像のファイル名.
	private final String altImage = "no_image.jpg";

	// データの種類を表すMIMEタイプをJPEGにする.
	private final String strMimeType = "image/jpeg";

	/* ResponseEntityはHTTPレスポンスの構成(ステータス・ヘッダー・ボディー)3つを全部まとめて返せるクラス.
	 * byte[]はbyte型の配列で画像ファイルはこの型で表すことができる(それぞれの保存形式（フォーマット）に従った「バイトの並び」になっている) */
	@GetMapping("/image/{productId}")
	public ResponseEntity<InputStreamResource> showImage(@PathVariable Integer productId) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);
		String fileName = oneItem.getProductImage();

		/* 取得した商品情報が存在するか,DBのimageに画像ファイル名が存在するか(nullじゃないか)を確認し存在しなければ404エラーを設定する.
		 * (画像ファイルはnullでもいいがそもそもnullのときは画像表示のボタンが表示されないが念のため). */
		if (oneItem == null || fileName == null) {

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

			/* pathの場所が存在しないか確認する(存在するならやることはないため). */
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
			/* この時点ではまだデータは流れない
			
			Files.newInputStream(path) はストリームを作っただけ
			
			InputStreamResource は「ストリームを持っているオブジェクト」
			
			body(resource) は「このリソースを返す準備をした」だけ
			
			データが実際に流れる瞬間
			
			Spring MVC が HTTPレスポンスをクライアントに送信する直前
			
			具体的には DispatcherServlet が HttpMessageConverter を呼んだとき
			
			ResourceHttpMessageConverter が resource.getInputStream() を開く
			
			そこから少しずつ読み出して HTTPレスポンスの OutputStream に書き込む
			
			この「読み出す」作業が 蛇口をひらく瞬間 です
			
			2. 「誰がひらくのか」
			
			Spring Framework がやる
			
			Controller が返した ResponseEntity<InputStreamResource> を受け取り
			
			ResourceHttpMessageConverter が実際にストリームを開く
			
			そのストリームから HTTP の OutputStream にデータをコピーする
			
			開発者は 自分で read したり close したりする必要はない
			
			3. まとめ（流れ）
			
			Files.newInputStream(path) → ストリーム生成（まだ流れない）
			
			InputStreamResource → Spring用ラップ（まだ流れない）
			
			return ResponseEntity.ok().body(resource) → 「流す準備完了」
			
			DispatcherServlet がレスポンス処理
			
			ResourceHttpMessageConverter が resource.getInputStream() を開く
			
			HTTPレスポンスの OutputStream に順次書き出す
			
			クライアントにデータが届く */
			return ResponseEntity.ok()
					.contentType(mediaType)
					.body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			// if文でfalseで画像のpathの設定とMediaTypeへの変更をしているときにエラーになったときに画像取得できなかったときなどのcatch.
			try {
				Path path = Path.of(uploadDir, altImage);
				mediaType = MediaType.parseMediaType(strMimeType);
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
