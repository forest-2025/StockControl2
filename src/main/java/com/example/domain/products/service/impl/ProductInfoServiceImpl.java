package com.example.domain.products.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.product.model.HistoryDetails;
import com.example.domain.product.model.TStock;
import com.example.domain.products.dto.UploadResult;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.model.ProductList;
import com.example.domain.products.model.ProductWithSupplier;
import com.example.domain.products.service.ProductInfoService;
import com.example.domain.suppliers.model.MSupplier;
import com.example.repository.ProductListMapper;
import com.example.repository.ProductMapper;
import com.example.repository.ProductWithSupplierMapper;
import com.example.repository.StockMapper;
import com.example.repository.SupplierMapper;
import com.example.repository.TransactionHistoryMapper;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Service
@Transactional
@Slf4j
public class ProductInfoServiceImpl implements ProductInfoService {
	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private StockMapper stockMapper;

	@Autowired
	private SupplierMapper supplierMapper;

	@Autowired
	private ProductListMapper productListMapper;

	@Autowired
	private TransactionHistoryMapper transactionHistoryMapper;

	@Autowired
	private ProductWithSupplierMapper productWithSupplierMapper;

	@Value("${file.upload-dir}")
	private String uploadDir;

	private static final String UPLOAD_DIR = "uploads";
	private static final long MAX_SIZE = 20 * 1024 * 1024; // 20MB
	private static final String[] ALLOWED_EXTENSIONS = { "jpg", "jpeg" };
	private static final int MAX_WIDTH = 800;
	private static final int MAX_HEIGHT = 600;

	private final Tika tika = new Tika();

	// 商品一覧・商品検索.
	/** 削除済み以外の商品一覧を商品番号の昇順で取得する. */
	@Override
	public List<ProductList> getProductList() {

		List<ProductList> productList = productListMapper.findAll();

		return productList;
	}

	/** 削除済み以外の商品検索結果一覧を商品番号の昇順で取得する. */
	@Override
	public List<ProductList> getSearchProductList(String search) {

		List<ProductList> productItems = productListMapper.findSearchResults(search);
		return productItems;
	}

	// 商品情報の登録・修正・削除.

	// 共通処理.
	/** 商品IDから商品情報と入荷先情報を取得する(削除済みは除く). */ // 商品情報修正画面に遷移する際や、入力後のバリデーション時に使用.
	@Override
	public ProductWithSupplier getOneProductWithSupplier(Integer productId) {

		ProductWithSupplier productWithSupplier = productWithSupplierMapper.findByProductId(productId);
		return productWithSupplier;

	}

	/** 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する. */
	@Override
	public List<MSupplier> getAllSupplier() {

		List<MSupplier> supplierList = supplierMapper.findAllInAscById();
		return supplierList;
	}

	/** 入荷先が登録されてるか確認する(削除済みは除く). */
	@Override
	public boolean isRegister(Integer supplierId) {

		MSupplier supplier = supplierMapper.findBySupplierId(supplierId);

		// supplierにデータがあるかどうかで登録されているか確認する(入荷先削除済みは除いているため,削除済みの入荷先IDならsupplierはnullになる).
		if (supplier == null) {
			// データがないのでfalse.
			return false;
		} else {
			// データがあるので登録されている.
			return true;
		}
	}

	/** 商品番号の重複がないか確認する. */
	@Override
	public boolean isNotDuplicateProductNumber(String productNumber) {

		// 商品番号から商品情報を取得する.
		MProduct product = productMapper.findByProductNumber(productNumber);
		// productがnull(重複無し)か確認する.
		if (product == null) {
			// 重複がないのでtrueを返す.
			return true;
		} else {
			return false;
		}
	}

	//	/** 商品番号から商品IDを取得する（商品登録後に在庫数を0に設定するため） */
	//	@Override 下のregisterProduct()にいれたので、このメソッド単体で必要なければ削除して、必要ならregisterProduct()のこのメソッド部分をこのメソッドに変更する.
	//	public Integer getProductId(String productNumber) {
	//
	//		// 商品番号から商品情報を取得する.
	//		MProduct product = productMapper.findByProductNumber(productNumber);
	//
	//		// 商品情報のうち商品IDを取得する.
	//		Integer productId = product.getProductId();
	//
	//		return productId;
	//	}

	/** 商品情報を登録する. */
	@Override
	public void registerProduct(MProduct product) {

		// 商品を登録する.
		productMapper.insertOne(product);

		// 商品番号から商品情報を取得する.
		String productNumber = product.getProductNumber();
		MProduct registerdProduct = productMapper.findByProductNumber(productNumber);

		// 商品情報のうち商品IDを取得する.
		Integer productId = registerdProduct.getProductId();

		// 登録した商品の在庫数を0に設定して、登録した商品の在庫数を持つデータを在庫数テーブルに初期登録する（これにより商品一覧画面に在庫数を渡せる）.
		TStock stock = new TStock(productId, 0);
		stockMapper.insertOne(stock);
	}

	/** 商品情報(商品番号・商品名・入荷先)を更新する. */
	@Override
	public void updateProduct(MProduct product) {
		productMapper.updateOne(product);

	}

	/** 商品IDから商品情報を取得する(削除済みは除く). */
	@Override
	public MProduct getOneProduct(Integer productId) {
		MProduct product = productMapper.findByProductId(productId);
		return product;
	}

	/** 商品情報(削除フラグ)を更新する. */
	@Override
	public void updateIsDeleted(MProduct product) {

		// 商品情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		product.setProductIsDeleted(1);
		// 削除フラグを更新する.
		productMapper.updateIsDeleted(product);
	}

	// 商品の詳細.
	/** 削除済み以外の商品IDから商品情報を商品番号の昇順で取得する. */
	@Override
	public ProductList getOneItemInTheList(Integer productId) {

		ProductList productList = productListMapper.findByProductId(productId);

		return productList;
	}

	/** 商品IDからその商品の履歴を降順で取得する. */
	@Override
	public List<HistoryDetails> getHistoryForOneProduct(Integer productId) {

		List<HistoryDetails> historyList = transactionHistoryMapper.findByProductId(productId);

		return historyList;
	}

	/**
	 * 画像バリデーション＆保存
	 * 複数のエラーをまとめて返す
	 *
	 * @param file MultipartFile
	 * @return エラーメッセージリスト（エラーなしなら空リスト）
	 */
	public UploadResult validateAndUpload(MultipartFile file) {
		// バリデーションエラーがあった時のメッセージを格納するListを宣言する(tryの外でも使用するためここで宣言).
		List<String> errors = new ArrayList<>();

		/* Pathクラスはファイルやディレクトリの場所（パス）を表すためのクラス(tryの外でも使用するためここで宣言). 
		 * ファイルやディレクトリの実際の操作はFilesクラス,場所や名前などの表現はPathクラスを使用する. */
		Path tempFile = null;

		// ファイル名を取得するための変数を宣言する(tryの外でも使用するためここで宣言).
		String fileName = null;

		try {
			// ----------------------
			// 基本バリデーション
			// ----------------------
			//			if (file.isEmpty())
			//				errors.add("ファイルが選択されていません");ファイルが0バイトか確認している
			if (file.getSize() > MAX_SIZE) {
				errors.add("画像ファイルは20MB以下にしてください");
			}

			// ファイルの元の名前(ユーザーが選択したときのファイル名)を取得する.
			String originalFileName = file.getOriginalFilename();
			// ファイル名がnullまたは空白("")や空文字(" ")でないかを拡張子も含んで確認する.
			if (originalFileName == null || originalFileName.isBlank()) {
				errors.add("ファイル名が不正です");
			} else {
				// 下にあるgetExtension()を呼び出して拡張子を取得し,それを小文字に変換している(this.は省略).
				String extension = getExtension(originalFileName).toLowerCase();

				// 拡張子がJPEG(.jpg, .jpeg)か確認する.
				boolean allowed = false;
				for (String allowedExtension : ALLOWED_EXTENSIONS) {
					if (allowedExtension.equals(extension)) {
						allowed = true;
						break;
					}
				}
				if (!allowed)
					errors.add("画像ファイルはJPEG(.jpgまたは.jpeg)のみ登録できます");
			}

			// この時点でバリデーションエラーがあるときは一旦フォーム画面でエラーを表示する(このあと画像を一時保存するため).
			if (!errors.isEmpty()) {
				return new UploadResult(errors, null);
			}
			// ----------------------
			// 一時ファイル作成
			// ----------------------
			/* 画像ファイルの確認や加工をするためデータを一時ファイルに移すため一時ファイルを作成する.
			 * (複数の処理を安全に行うため).
			 * Filesクラスは静的メソッド(staticメソッド)のみを持つユーティリティクラス
			 * (特定のオブジェクトに属さず,様々な場所で繰り返し使う汎用的な機能（共通処理や便利なメソッド・定数など）をまとめて置いたクラス).
			 * ファイルやディレクトリ操作を安全・簡潔に行える.
			 * createTempFile()でOSが管理する一時ディレクトリに一時ファイルを作成している(中身は空).
			 * 引数の"upload-", ".jpg"によってupload-xxxxxx.jpg
			 * (xxxxxxは自動作成で名前が衝突しないようになっている)
			 * のような名前のファイルが作成される. */
			tempFile = Files.createTempFile("upload-", ".jpg");
			/* .transferTo(Path)のほうのメソッド(Fileよりこちらのほうが推奨されている).
			 * 画像ファイルはアップロードされるとサーブレットコンテナが "自動的" に小さいサイズはメモリ（バイト配列）に,
			 * 大きいサイズは一時ファイルに保存する.
			 * この画像ファイルに直接アクセスすることはできず、MultipartFile オブジェクトを通してのみ扱える.
			 * 小さい・大きいの判定はspring.servlet.multipart.file-size-thresholdで決まり,
			 * デフォルトは0Bのため0Bより大きいファイルはすべて一時ファイルに保存される.
			 * transferTo()で先ほど作成した一時ファイルにファイルを移すが,このときサーブレットコンテナが保存した保存形態によって挙動が変わる.
			 * 一時ファイルの場合は,可能であればrename（move）によって指定先へ移動され,元の一時ファイルは消える.
			 * そのため2回目のアクセスはできない.メモリ上の場合は、ヒープ領域上(newキーワードで生成されるオブジェクトの実体（インスタンスデータや配列）が格納される)
			 * のバイト配列を指定先ファイルへ書き出す.このメモリはMultipartFileが不要になればガベージコレクションにより自動的に解放される.
			 * なお,一時ファイルと保存先が異なるファイルシステムの場合はrenameができずcopy + deleteになることがあるが,
			 * 一時ファイルは通常リクエスト終了時に自動削除される(サーブレットコンテナの一時ファイルも作成した一時ファイルもOSのディレクトリに保存されるが,
			 * デフォルトでは場所が違う可能性が高い.). */
			file.transferTo(tempFile);

			// ----------------------
			// MIMEタイプチェック
			// ----------------------
			/* MIMEタイプ(メディアタイプ)を確認する.
			 * MIMEタイプはインターネット上でやり取りされるファイルやデータが「どのような形式（種類）か」を示すラベルで,
			 * ヘッダーのContent-Typeで指定される(JPEGならimage/jpegのような形式).
			 * ファイルの拡張子は偽装できるためファイル内のMIMEタイプを確認することで不適切なファイルのアップロードを防ぐことができる.
			 * (ファイルには拡張子だけでなく,ファイルの先頭に特定のバイト列が決められているものが多く,その先頭のバイト列
			 * (マジックナンバーまたはマジックバイト)でそのファイルの形式が判定できる.Apache Tikaを使用するとマジックナンバー,
			 * 拡張子,ファイル内部の解析を行いファイルの形式を判定してくれる).
			 * detect(Path)でPathのMIMEタイプを判定してくれる(ドキュメントにdetect(String)~と書いてあるのは,
			 * detect(String)はファイルの内容を読み込まずにファイル名でMIMEタイプを判断する(もはや中身があってもなくてもいい)ので,
			 * 内容見なくていいのならこちらを使うようにという補足情報を教えてくれているだけ).
			 * detect(Path)の戻り値はimage/jpegのような形式の文字列. */
			String mimeType = tika.detect(tempFile);
			// equalsIgnoreCase("image/jpeg")で大文字小文字の区別なく文字列の比較ができる.
			if (!mimeType.equalsIgnoreCase("image/jpeg")) {
				errors.add("画像ファイルはJPEG(.jpgまたは.jpeg)のみ登録できます");
			}

			// ----------------------
			// ImageIOで読み込めるか
			// ----------------------
			/* ImageIOで読み込めるか確認する.
			 * 圧縮されてバイナリデータの形でおくられてきたJPEGファイルをImageIO.read()で展開してピクセルに変換して読み込んでいる.
			 * ImageIOクラスで対応しているクラスならBufferedImage(メモリ上で展開されてピクセル変換された画像の幅・高さ・色形式などのデータを持つJavaで扱えるオブジェクト)が返り,
			 * ちがうければnullが返る.JPEGはImageIOクラスで対応しているためこれを利用して展開してピクセル変換できるかどうかで画像かどうか確認している.
			 * ここでの確認はあくまで画像かどうかの確認でJPEGかどうかまでは確認しているわけではない(ImageIOクラスで対応している画像かどうかわかるだけ).
			 * この「メモリ上で展開されてピクセル変換」はJVMのヒープ領域上で行われるため,上でサーブレットコンテナが小さいファイルをメモリにいれたままで一時ファイルを作成せずにこれを行うと,
			 * 圧縮したJPEGファイル + 展開したファイル(圧縮したファイルの数倍の大きさ)となりメモリをかなり使用し,大きすぎるとOutOfMemoryErrorになる.
			 * 一時ファイルに作成して移したことにより,メモリ上には展開したファイルだけになるのでメモリの圧迫を防ぐことができる.
			 * プロジェクト直下に保存できる容量はプロジェクトがあるドライブ（C:やD:など）のディスクの空き容量が上限となり,一時ファイルの容量の上限はOSの一時ディレクトリがあるドライブの空き容量となる.
			 * メモリ（JVMヒープ）は
			 * System.out.println("最大メモリ: " + Runtime.getRuntime().maxMemory()/1024/1024 + "MB");
			 * System.out.println("割り当て済みメモリ: " + Runtime.getRuntime().totalMemory()/1024/1024 + "MB");
			 * System.out.println("使用中メモリ: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024 + "MB");
			 * で最大メモリ(上限)・現在確保しているメモリ(起動時に表示すると初期確保ヒープに近い)・現在使用中のメモリがみれる.
			 * JVMは必要に応じてメモリを拡張するので常に最大を使っているわけではない. */
			if (ImageIO.read(tempFile.toFile()) == null) {
				errors.add("画像ファイルとして読み込めません");
			}
			
			// errorsがあればバリデーションエラーとしてフォーム画面に戻す.
			if (!errors.isEmpty())
				return new UploadResult(errors, null);

			// ----------------------
			// 保存ディレクトリ作成
			// ----------------------
			/* Pathはファイルシステム上のファイルやディレクトリのパス（経路）を表すためのインタフェースで、インタフェースのためnewでのオブジェクト作成はできない.
			 * PathsクラスはPathを生成するユーティリティクラスでメソッドもObjectクラスから継承したもの以外は,Pathを生成するget()メソッド(2種)しかない.
			 * Path型の変数uploadPathに代入されているのはPathを実装した「実際のクラスのインスタンス」で,Paths.get(uploadDir)は
			 * Paths.get(String first, String... more)で,どのOSかを判定して適切なPath実装を作っていたが,Path.ofで同じように作れるようになった.
			 * https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/nio/file/Path.javaで,
			 * public static Path of(String first, String... more) {
			 * 	return FileSystems.getDefault().getPath(first, more);}
			 * とある.FileSystems.getDefault()で実行環境のOSに対応したFileSystemのオブジェクト(FileSystem自体は抽象クラスなので実態は実装されたクラス)を作成する.
			 * (Windowsならsun.nio.fs.WindowsFileSystemのインスタンス,LinuxやmacOSならsun.nio.fs.UnixFileSystemのインスタンス).
			 * WindowsFileSystemならWindowsFileSystem.getPath(first, more)となりfirstとmoreを\(バックスラッシュ)で結合していく.
			 * (sb.append('\\');とあるが \ はエスケープ文字なので,'\\' と書くことで1文字のバックスラッシュを表す).
			 * return WindowsPath.parse(this, path);となる(thisはこのWindowsPathインスタンス自体で,pathはfirstとmoreを結合した文字列).
			 * WindowsPath.parse(this, path)で WindowsPathParser.Result result = WindowsPathParser.parse(path);となり,
			 * WindowsPathParser.parse(path)で入力文字列の形式を判定(C:\Users→ドライブ付き絶対パス,foo\bar→相対パスなど)し,ルート部分（root）の取り出し
			 * (ルートとはファイルシステム上で「最上位の基準となる場所」のことで,パスの先頭にありそのパスが絶対パスかどうかを決める(C:\Usersならルート(root)はC:\でUsersは残り(path)の扱い)),
			 * ルート部分以外の残りのpathの部分を分割・検証（無効文字チェックなど）を行い,解析によって得られたパスの種類（絶対・相対）・ルート文字列・残りのパス部分を
			 * WindowsPathParser.ResultというWindowsPathParserの小さなデータクラス(ネスト静的クラス（Static Nested Class）)に詰めて返す.
			 * それをもとにreturn new WindowsPath(fs, result.type(), result.root(), result.path());が作成される.
			 * これはsun.nio.fs.WindowsPath(LinuxやmacOSではsun.nio.fs.UnixPath)のオブジェクトが作成されていて,
			 * WindowsPathはPathの実装クラスのためPath uploadPathで受け取っている(つまりuploadPathの中身はWindowsPathのインスタンス). */
			Path uploadPath = Path.of(uploadDir);
			
			// ファイル／ディレクトリが「実際に存在しないか」を確認する.
			if (!Files.exists(uploadPath)) {
				// 存在しなければ作成する(親ディレクトリがなければそれも作成してくれる).
				Files.createDirectories(uploadPath);
			}

			// ----------------------
			// UUIDで保存 (.jpg固定)
			// ----------------------
			/* savedUuidに一意のファイル名を作成し代入する.
			 * UUID.randomUUID()の戻り値はUUIDのオブジェクトだが文字列を連結すると
			 * UUID.randomUUID().toString();が内部でよばれてString型に自動で変換してくれる. */
			fileName = UUID.randomUUID() + ".jpg";
			// ディレクトリのパスにファイル名をつなげて完全なファイルのパスを作成する(パスができているだけでファイルはできていない).
			Path targetFile = Path.of(uploadDir, fileName);

			/* .ofで画像ファイルを入力画像として読み込む(Path型は受け取れないのでFile型に変換してる).
			 * .sizeでリサイズし,.outputFormat("jpg")で強制的にJPEGに変換している(念のため).
			 * .toFile(targetFile.toFile())でファイルを作成している.
			 * (引数ををFile型に変換したtargetFileにすることで一意の名前に変換されてファイル名でファイルが作成される). */
			Thumbnails.of(tempFile.toFile())
					.size(MAX_WIDTH, MAX_HEIGHT)
					.outputFormat("jpg")
					.toFile(targetFile.toFile());

			//log.info("画像保存成功: {}", targetFile);

		} catch (IOException e) {
			errors.add("画像処理中にエラーが発生しました");
			log.error("画像処理エラー", e);
		} finally {
			// ----------------------
			// 一時ファイル削除
			// ----------------------
			if (tempFile != null) {
				try {
					// deleteIfExists()は,もしファイルがあれば削除し、なければ何もしないメソッド.
					Files.deleteIfExists(tempFile);
					log.debug("一時ファイル削除成功: {}", tempFile);
				} catch (IOException e) {
					// 小規模・テストサーバーなので警告ログだけ
					log.warn("一時ファイル削除失敗: {}", tempFile, e);
				}
			}
		}

		return new UploadResult(errors, fileName);
	}

	/** ファイル名の拡張子を取得する("."は除く). */
	private String getExtension(String filename) {
		// 0始まりで.のインデックスを取得する("."がないときは-1になる).
		int dotIndex = filename.lastIndexOf('.');
		// ファイル名の"."以降を取得する(substringの開始位置をdotIndexに１足すことで"."を除く拡張子を取得できる).
		return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
	}

	@Override
	public void updateProductImage(MProduct product) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

//	public List<String> validateAndSaveImage(Long id, MultipartFile file) {
//		List<String> errors = new ArrayList<>();
//		Path tempFile = null;
//		String newFilename = null;
//
//		//		ImageEntity existing = null;
//		//		if (id != null) {
//		//			existing = imageMapper.findById(id);
//		//		}	idが存在してるか確認している
//
//		try {
//			// ----------------------
//			// file がある場合
//			// ----------------------
//			if (file != null && !file.isEmpty()) {
//				if (file.getSize() > MAX_SIZE) {
//					errors.add("ファイルサイズが20MBを超えています");
//				}
//
//				String originalName = file.getOriginalFilename();
//				if (originalName != null && !originalName.toLowerCase().endsWith(".jpg") &&
//						!originalName.toLowerCase().endsWith(".jpeg")) {
//					errors.add("JPEG画像のみ対応しています");
//				}
//
//				if (!errors.isEmpty())
//					return errors;
//
//				// 一時ファイル作成
//				tempFile = Files.createTempFile("upload-", ".jpg");
//				file.transferTo(tempFile.toFile());
//
//				String mimeType = tika.detect(tempFile.toFile());
//				if (!mimeType.equalsIgnoreCase("image/jpeg")) {
//					errors.add("JPEG画像ではありません");
//				}
//
//				if (ImageIO.read(tempFile.toFile()) == null) {
//					errors.add("画像として読み込めません");
//				}
//
//				if (!errors.isEmpty())
//					return errors;
//
//				Path uploadPath = Paths.get(uploadDir);
//				if (!Files.exists(uploadPath))
//					Files.createDirectories(uploadPath);
//
//				newFilename = UUID.randomUUID().toString() + ".jpg";
//				Path targetFile = uploadPath.resolve(newFilename);
//
//				Thumbnails.of(tempFile.toFile())
//						.size(MAX_WIDTH, MAX_HEIGHT)
//						.toFile(targetFile.toFile());
//
//				// 既存画像削除
//				if (existing != null && existing.getFilename() != null) {
//					Path oldFile = Paths.get(UPLOAD_DIR, existing.getFilename());
//					if (Files.exists(oldFile))
//						Files.delete(oldFile);
//				}
//
//			}
//
//			// ----------------------
//			// DB登録／更新
//			// ----------------------
//			if (existing == null) {
//				ImageEntity image = new ImageEntity();
//				image.setFilename(newFilename);
//
//				imageMapper.insert(image);
//			} else {
//				if (file == null && existing.getFilename() != null) {
//					existing.setFilename(null); // 削除のみ
//				}
//				if (file != null) {
//					existing.setFilename(newFilename);
//				}
//				imageMapper.update(existing);
//			}
//
//		} catch (Exception e) {
//			errors.add("画像処理中にエラーが発生しました");
//			log.error("画像処理エラー", e);
//		} finally {
//			if (tempFile != null) {
//				try {
//					Files.deleteIfExists(tempFile);
//				} catch (Exception e) {
//					log.warn("一時ファイル削除失敗: {}", tempFile, e);
//				}
//			}
//		}
//
//		return errors;
//	}
}
