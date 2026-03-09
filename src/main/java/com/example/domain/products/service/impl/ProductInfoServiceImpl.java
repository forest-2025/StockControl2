package com.example.domain.products.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.products.model.MProduct;
import com.example.domain.products.service.ProductInfoService;
import com.example.domain.suppliers.model.MSupplier;
import com.example.dto.products.HistoryDetails;
import com.example.dto.products.ProductList;
import com.example.dto.products.ProductWithSupplier;
import com.example.dto.products.TStock;
import com.example.dto.products.UploadResult;
import com.example.exception.types.ImageDeleteException;
import com.example.exception.types.ImageUpdateException;
import com.example.repository.ProductListMapper;
import com.example.repository.ProductMapper;
import com.example.repository.ProductWithSupplierMapper;
import com.example.repository.StockMapper;
import com.example.repository.SupplierMapper;
import com.example.repository.TransactionHistoryMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * ProductInfoService の実装クラス.
 * 
 */
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

	@Autowired
	private MessageSource messageSource;

	@Value("${file.upload-dir}")
	private String uploadDir;

	// staticなのでクラスで一つの値を持つ.
	private static final long MAX_SIZE = 20 * 1024 * 1024; // 20MB

	private static final String[] ALLOWED_EXTENSIONS = { "jpg", "jpeg" };

	private static final int MAX_WIDTH = 1200;

	private static final int MAX_HEIGHT = 1200;

	private final Tika tika = new Tika();

	// 1ページで表示する商品数・入出荷履歴数を10に設定する.
	private static final int SHOW_SIZE = 10;

	// 商品番号を指定された並べ替え順序（昇順または降順）に基づいて並び替えた商品一覧を取得する.
	@Override
	public PageInfo<ProductList> findAllSorted(String search, String sort, int page) {

		if (search == null) {
			return this.getProductList(page);

			// 検索ボタン,商品番号の昇順・降順ボタンを押したときのsearchには,検索フォームに何も入っていなければ空白が入るのでnullではないためこちらに分岐する.
		} else if (sort.equals("asc") || sort.equals("desc")) {
			return this.getSearchProductList(search, sort, page);

			/* sort(並び替え順序)がascまたはdescでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
			 * 削除済み以外の取得する. */
		} else {
			return this.getProductList(page);

		}

	}

	// 削除済み以外の商品一覧を商品番号の昇順でページングして取得する. 
	@Override
	public PageInfo<ProductList> getProductList(int page) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<ProductList> productList = productListMapper.findAll();

		return new PageInfo<>(productList);
	}

	// 削除済み以外の商品一覧から検索語句と一致する商品を,商品番号の昇順でページングして取得する.
	@Override
	public PageInfo<ProductList> getSearchProductList(String search, String sort, int page) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<ProductList> productItems = productListMapper.findSearchResults(search, sort);

		return new PageInfo<>(productItems);
	}

	// 商品のIDから商品情報と入荷先情報を取得する(削除済みは除く).
	@Override
	public ProductWithSupplier getOneProductWithSupplier(Integer productId) {

		ProductWithSupplier productWithSupplier = productWithSupplierMapper.findByProductId(productId);
		return productWithSupplier;

	}

	// 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する.
	@Override
	public List<MSupplier> getAllSupplier() {

		List<MSupplier> supplierList = supplierMapper.findAllInAscById();
		return supplierList;
	}

	// 入荷先IDから入荷先がDBに登録されてるか確認する(削除済みは除く).
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

	// 指定した商品番号と重複するデータの件数を取得する.
	@Override
	public boolean getCountDuplicates(Object productIdValue, Object productNumberValue) {

		/* 取得した商品の商品番号と商品IDから,商品番号が既存の商品番号と(修正時は自身の商品番号は除外して)重複しているか確認し,
		 * 重複している件数をcountに代入する.*/
		int count = productMapper.countDuplicates(productIdValue, productNumberValue);

		// 重複している件数が0件なら重複はないためtrueになる(修正時なら自身の商品番号との重複は重複とみなさないため0件になり,trueになる).
		if (count == 0) {

			return true;
		}

		return false;
	}

	// 商品情報を登録する.
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

	// 商品情報(商品番号・商品名・入荷先)を更新する.
	@Override
	public void updateProduct(MProduct product) {
		productMapper.updateOne(product);

	}

	// 商品IDから商品情報を取得する(削除済みは除く).
	@Override
	public MProduct getOneProduct(Integer productId) {
		MProduct product = productMapper.findByProductId(productId);
		return product;
	}

	// 削除フラグを更新する.
	@Override
	public void updateIsDeleted(MProduct product) {

		// 商品情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		product.setProductIsDeleted(1);

		try {
			// 画像は削除する(メモリを圧迫するため).
			if (product.getProductImage() != null) {
				Path oldFile = Path.of(uploadDir, product.getProductImage());
				if (Files.exists(oldFile)) {
					Files.delete(oldFile);
				}

			}

			// 画像を削除したため,DBのファイル名も削除するため,nullに設定する.
			product.setProductImage(null);

			// 削除フラグを更新する.
			productMapper.updateIsDeleted(product);

		} catch (IOException e) {
			throw new ImageDeleteException("画像の削除処理に失敗しました", e);
		}

	}

	// 商品IDから商品情報を商品番号の昇順で取得する(削除済みは除く).
	@Override
	public ProductList getOneItemInTheList(Integer productId) {

		ProductList productList = productListMapper.findByProductId(productId);

		return productList;
	}

	// 商品IDからその商品の履歴を降順でページングして取得する.  
	@Override
	public PageInfo<HistoryDetails> getHistoryForOneProduct(int page, Integer productId) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<HistoryDetails> historyList = transactionHistoryMapper.findByProductId(productId);

		return new PageInfo<>(historyList);
	}

	@Override
	public UploadResult validateAndUpload(MultipartFile file, UploadResult result) throws IOException {
		List<String> errors = new ArrayList<>();
		// ファイルの拡張子を取得
		String originalFileName = file.getOriginalFilename();

		if (originalFileName == null || originalFileName.isBlank()) {
			String errorMessage = messageSource.getMessage("InvalidFileName", null, Locale.JAPAN);
			errors.add(errorMessage);
		} else {
			// 下にあるprivateメソッドgetExtension()を呼び出して拡張子を取得し,それを小文字に変換している.
			String extension = this.getExtension(originalFileName).toLowerCase();

			// 拡張子がJPEG(.jpg, .jpeg)か確認する.
			boolean allowed = false;
			for (String allowedExtension : ALLOWED_EXTENSIONS) {
				if (allowedExtension.equals(extension)) {
					allowed = true;
					break;
				}
			}
			if (!allowed) {
				String errorMessage = messageSource.getMessage("FileFormatsDiffer", null, Locale.JAPAN);
				errors.add(errorMessage);
			}
		}

		//この時点でバリデーションエラーがあるときは一旦フォーム画面でエラーを表示する.
		if (!errors.isEmpty()) {
			result.setErrors(errors);
			result.setFileName(null);
			return result;
		}

		// アップロードファイルはUUIDを使って重複しない名前に変更する
		String fileName = UUID.randomUUID().toString() + ".jpg";
		// 画像保存先
		Path targetFile = Path.of(uploadDir, fileName);

		// 保存先ディレクトリがなければ作成する
		Files.createDirectories(targetFile.getParent());
		// アップロードしたファイルを保存
		Files.write(targetFile, file.getBytes());
		
		result.setErrors(errors);
		result.setFileName(fileName);

		return result;

	}
	
	
	//	// 商品画像をバリデーションチェックし,ローカルファイルストレージ(プロジェクト直下)に保存する. 
	//	@Override
	//	public UploadResult validateAndUpload(MultipartFile file, UploadResult result) {
	//		// バリデーションエラーがあった時のメッセージを格納するListを宣言する(tryの外でも使用するためここで宣言).
	//		List<String> errors = new ArrayList<>();
	//
	//		/* Pathクラスはファイルやディレクトリの場所（パス）を表すためのクラス(tryの外でも使用するためここで宣言). 
	//		 * ファイルやディレクトリの実際の操作はFilesクラス,場所や名前などの表現はPathクラスを使用する. */
	//		Path tempFile = null;
	//
	//		// ファイル名を取得するための変数を宣言する(tryの外でも使用するためここで宣言).
	//		String fileName = null;
	//
//			try {
//				if (file.getSize() > MAX_SIZE) {
//					String errorMessage = messageSource.getMessage("OverSize", null, Locale.JAPAN);
//					errors.add(errorMessage);
//				}
	//
	//			// ファイルの元の名前(ユーザーが選択したときのファイル名)を取得する.
	//			String originalFileName = file.getOriginalFilename();
	//
	//			/* ファイル名がnullまたは空白("")や空文字(" ")でないかを拡張子も含めて確認する.
	//			 * (画像ファイルを選択しなかった場合,空文字になるためnullにならなかったがブラウザによってはnullになる).
	//			 * (ファイル名を拡張子含めて空白または空文字にすることは基本的に(拡張子の.がつくため)難しい(Unix系なら理論上可能らしい).
	//			 * キーボードのAltキーを押したまま,右側のテンキーで255を入力し,Altキーを離すと拡張子なしのファイル名ができるが,
	//			 * その場合isBlank()では判別できない(Alt + 255はU+00A0という一般的なノーブレーキングスペース
	//			 * (NBSP コンピュータ上で扱う特殊な空白文字のことで,その前後で強制的な改行（自動折り返し）をさせないスペース)で,
	//			 * isBlank()は内部でCharacter.isWhitespace()を使用するが,Character.isWhitespace()で
	//			 * U+00A0は対応してないから).あとの拡張子チェックでみつけられるためU+00A0などのチェックまではしていない. */
	//			if (originalFileName == null || originalFileName.isBlank()) {
	//				String errorMessage = messageSource.getMessage("InvalidFileName", null, Locale.JAPAN);
	//				errors.add(errorMessage);
	//			} else {
	//				// 下にあるprivateメソッドgetExtension()を呼び出して拡張子を取得し,それを小文字に変換している.
	//				String extension = this.getExtension(originalFileName).toLowerCase();
	//
	//				// 拡張子がJPEG(.jpg, .jpeg)か確認する.
	//				boolean allowed = false;
	//				for (String allowedExtension : ALLOWED_EXTENSIONS) {
	//					if (allowedExtension.equals(extension)) {
	//						allowed = true;
	//						break;
	//					}
	//				}
	//
	//				if (!allowed) {
	//					String errorMessage = messageSource.getMessage("FileFormatsDiffer", null, Locale.JAPAN);
	//					errors.add(errorMessage);
	//				}
	//			}
	//
	//			// この時点でバリデーションエラーがあるときは一旦フォーム画面でエラーを表示する(このあと画像を一時保存するため).
	//			if (!errors.isEmpty()) {
	//				result.setErrors(errors);
	//				result.setFileName(null);
	//				return result;
	//			}
	//
	//			/* 画像ファイルの確認や加工をするためデータを一時ファイルに移すため一時ファイルを作成する.
	//			 * (複数の処理を安全に行うため).
	//			 * Filesクラスは静的メソッド(staticメソッド)のみを持つユーティリティクラス
	//			 * (特定のオブジェクトに属さず,様々な場所で繰り返し使う汎用的な機能（共通処理や便利なメソッド・定数など）をまとめて置いたクラス).
	//			 * ファイルやディレクトリ操作を安全・簡潔に行える.
	//			 * createTempFile()でOSが管理する一時ディレクトリに一時ファイルを作成している(中身は空).
	//			 * 引数の"upload-", ".jpg"によってupload-xxxxxx.jpg
	//			 * (xxxxxxは自動作成で名前が衝突しないようになっている)
	//			 * のような名前のファイルが作成される. */
	//			tempFile = Files.createTempFile("upload-", ".jpg");
	//
	//			/* .transferTo(Path)のほうのメソッド(Fileよりこちらのほうが推奨されている).
	//			 * 画像ファイルはアップロードされるとサーブレットコンテナが "自動的" に小さいサイズはメモリ（バイト配列）に,
	//			 * 大きいサイズは一時ファイルに保存する.
	//			 * この画像ファイルに直接アクセスすることはできず、MultipartFile オブジェクトを通してのみ扱える.
	//			 * 小さい・大きいの判定はspring.servlet.multipart.file-size-thresholdで決まり,
	//			 * デフォルトは0Bのため0Bより大きいファイルはすべて一時ファイルに保存される(今回は1MBで設定している).
	//			 * transferTo()で先ほど作成した一時ファイルにファイルを移すが,このときサーブレットコンテナが保存した保存形態によって挙動が変わる.
	//			 * 一時ファイルの場合は,可能であればrename（move）によって指定先へ移動され,元の一時ファイルは消える.
	//			 * そのため2回目のアクセスはできない.メモリ上の場合は、ヒープ領域上(newキーワードで生成されるオブジェクトの実体（インスタンスデータや配列）が格納される)
	//			 * のバイト配列を指定先ファイルへ書き出す.このメモリはMultipartFileが不要になればガベージコレクションにより自動的に解放される.
	//			 * なお,一時ファイルと保存先が異なるファイルシステムの場合はrenameができずcopy + deleteになることがあるが,
	//			 * 一時ファイルは通常リクエスト終了時に自動削除される(サーブレットコンテナの一時ファイルも作成した一時ファイルもOSのディレクトリに保存されるが,
	//			 * デフォルトでは場所が違う可能性が高い.). */
	//			file.transferTo(tempFile);
	//
	//			/* MIMEタイプ(メディアタイプ)を確認する.
	//			 * MIMEタイプはインターネット上でやり取りされるファイルやデータが「どのような形式（種類）か」を示すラベルで,
	//			 * ヘッダーのContent-Typeで指定される(JPEGならimage/jpegのような形式).
	//			 * ファイルの拡張子は偽装できるためファイル内のMIMEタイプを確認することで不適切なファイルのアップロードを防ぐことができる.
	//			 * (ファイルには拡張子だけでなく,ファイルの先頭に特定のバイト列が決められているものが多く,その先頭のバイト列
	//			 * (マジックナンバーまたはマジックバイト)でそのファイルの形式が判定できる.Apache Tikaを使用するとマジックナンバー,
	//			 * 拡張子,ファイル内部の解析を行いファイルの形式を判定してくれる).
	//			 * detect(Path)でPathのMIMEタイプを判定してくれる(ドキュメントにdetect(String)~と書いてあるのは,
	//			 * detect(String)はファイルの内容を読み込まずにファイル名でMIMEタイプを判断する(もはや中身があってもなくてもいい)ので,
	//			 * 内容見なくていいのならこちらを使うようにという補足情報を教えてくれているだけ).
	//			 * detect(Path)の戻り値はimage/jpegのような形式の文字列. */
	//			String mimeType = tika.detect(tempFile);
	//			// equalsIgnoreCase("image/jpeg")で大文字小文字の区別なく文字列の比較ができる.
	//			if (!mimeType.equalsIgnoreCase("image/jpeg")) {
	//				String errorMessage = messageSource.getMessage("FileFormatsDiffer", null, Locale.JAPAN);
	//				errors.add(errorMessage);
	//			}
	//
	//			/* ImageIOで読み込めるか確認する.
	//			 * 圧縮されてバイナリデータの形でおくられてきたJPEGファイルをImageIO.read()で展開してピクセルに変換して読み込んでいる.
	//			 * ImageIOクラスで対応しているクラスならBufferedImage(メモリ上で展開されてピクセル変換された画像の幅・高さ・色形式などのデータを持つJavaで扱えるオブジェクト)が返り,
	//			 * ちがうければnullが返る.JPEGはImageIOクラスで対応しているためこれを利用して展開してピクセル変換できるかどうかで画像かどうか確認している.
	//			 * ここでの確認はあくまで画像かどうかの確認でJPEGかどうかまでは確認しているわけではない(ImageIOクラスで対応している画像かどうかわかるだけ).
	//			 * この「メモリ上で展開されてピクセル変換」はJVMのヒープ領域上で行われるため,上でサーブレットコンテナが小さいファイルをメモリにいれたままで一時ファイルを作成せずにこれを行うと,
	//			 * 圧縮したJPEGファイル + 展開したファイル(圧縮したファイルの数倍の大きさ)となりメモリをかなり使用し,大きすぎるとOutOfMemoryErrorになる.
	//			 * 一時ファイルに作成して移したことにより,メモリ上には展開したファイルだけになるのでメモリの圧迫を防ぐことができる.
	//			 * プロジェクト直下に保存できる容量はプロジェクトがあるドライブ（C:やD:など）のディスクの空き容量が上限となり,一時ファイルの容量の上限はOSの一時ディレクトリがあるドライブの空き容量となる.
	//			 * メモリ（JVMヒープ）は
	//			 * System.out.println("最大メモリ: " + Runtime.getRuntime().maxMemory()/1024/1024 + "MB");
	//			 * System.out.println("割り当て済みメモリ: " + Runtime.getRuntime().totalMemory()/1024/1024 + "MB");
	//			 * System.out.println("使用中メモリ: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024 + "MB");
	//			 * で最大メモリ(上限)・現在確保しているメモリ(起動時に表示すると初期確保ヒープに近い)・現在使用中のメモリがみれる.
	//			 * JVMは必要に応じてメモリを拡張するので常に最大を使っているわけではない. */
	//			if (ImageIO.read(tempFile.toFile()) == null) {
	//				String errorMessage = messageSource.getMessage("NotReadImageFile", null, Locale.JAPAN);
	//				errors.add(errorMessage);
	//			}
	//
	//			// errorsがあればバリデーションエラーとしてフォーム画面に戻す.
	//			if (!errors.isEmpty()) {
	//				result.setErrors(errors);
	//				result.setFileName(null);
	//				return result;
	//			}
	//
	//			/* 保存ディレクトリを作成する.
	//			 * Pathはファイルシステム上のファイルやディレクトリのパス（経路）を表すためのインタフェースで、インタフェースのためnewでのオブジェクト作成はできない.
	//			 * PathsクラスはPathを生成するユーティリティクラスでメソッドもObjectクラスから継承したもの以外は,Pathを生成するget()メソッド(2種)しかない.
	//			 * Path型の変数uploadPathに代入されているのはPathを実装した「実際のクラスのインスタンス」で,Paths.get(uploadDir)は
	//			 * Paths.get(String first, String... more)で,どのOSかを判定して適切なPath実装を作っていたが,Path.ofで同じように作れるようになった.
	//			 * https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/nio/file/Path.javaで,
	//			 * public static Path of(String first, String... more) {
	//			 * 	return FileSystems.getDefault().getPath(first, more);}
	//			 * とある.FileSystems.getDefault()で実行環境のOSに対応したFileSystemのオブジェクト(FileSystem自体は抽象クラスなので実態は実装されたクラス)を作成する.
	//			 * (Windowsならsun.nio.fs.WindowsFileSystemのインスタンス,LinuxやmacOSならsun.nio.fs.UnixFileSystemのインスタンス).
	//			 * WindowsFileSystemならWindowsFileSystem.getPath(first, more)となりfirstとmoreを\(バックスラッシュ)で結合していく.
	//			 * (sb.append('\\');とあるが \ はエスケープ文字なので,'\\' と書くことで1文字のバックスラッシュを表す).
	//			 * return WindowsPath.parse(this, path);となる(thisはこのWindowsPathインスタンス自体で,pathはfirstとmoreを結合した文字列).
	//			 * WindowsPath.parse(this, path)で WindowsPathParser.Result result = WindowsPathParser.parse(path);となり,
	//			 * WindowsPathParser.parse(path)で入力文字列の形式を判定(C:\Users→ドライブ付き絶対パス,foo\bar→相対パスなど)し,ルート部分（root）の取り出し
	//			 * (ルートとはファイルシステム上で「最上位の基準となる場所」のことで,パスの先頭にありそのパスが絶対パスかどうかを決める(C:\Usersならルート(root)はC:\でUsersは残り(path)の扱い)),
	//			 * ルート部分以外の残りのpathの部分を分割・検証（無効文字チェックなど）を行い,解析によって得られたパスの種類（絶対・相対）・ルート文字列・残りのパス部分を
	//			 * WindowsPathParser.ResultというWindowsPathParserの小さなデータクラス(ネスト静的クラス（Static Nested Class）)に詰めて返す.
	//			 * それをもとにreturn new WindowsPath(fs, result.type(), result.root(), result.path());が作成される.
	//			 * これはsun.nio.fs.WindowsPath(LinuxやmacOSではsun.nio.fs.UnixPath)のオブジェクトが作成されていて,
	//			 * WindowsPathはPathの実装クラスのためPath uploadPathで受け取っている(つまりuploadPathの中身はWindowsPathのインスタンス). */
	//			Path uploadPath = Path.of(uploadDir);
	//
	//			// ファイル／ディレクトリが「実際に存在しないか」を確認する.
	//			if (!Files.exists(uploadPath)) {
	//				// 存在しなければ作成する(親ディレクトリがなければそれも作成してくれる).
	//				Files.createDirectories(uploadPath);
	//			}
	//
	//			/* 一意のファイル名を作成し代入する.
	//			 * UUID.randomUUID()の戻り値はUUIDのオブジェクトだが文字列を連結すると,
	//			 * UUID.randomUUID().toString();が内部でよばれてString型に自動で変換してくれる. */
	//			fileName = UUID.randomUUID() + ".jpg";
	//
	//			// ディレクトリのパスにファイル名をつなげて完全なファイルのパスを作成する(パスができているだけでファイルはできていない).
	//			Path targetFile = Path.of(uploadDir, fileName);
	//
	//			/* Thumbnails.ofでThumbnails.Builder<File>オブジェクト(Builderオブジェクト)を生成する.
	//			 * (Path型は受け取れないのでtoFile()でFile型に変換してる).
	//			 * .sizeはBuilderオブジェクトのメソッドで,これでリサイズできる.
	//			 * (Builder内部にサイズ設定を追加,戻り値が.ofで作成されたオブジェクトにサイズ設定を追加されたBuilderオブジェクト).
	//			 * .outputFormat("jpg")で強制的にJPEGに変換している(念のため).
	//			 * .toFile(targetFile.toFile())でファイルを作成,保存している.
	//			 * (引数をFile型に変換したtargetFileにすることで一意の名前に変換されたファイル名でファイルが作成される). */
	//			Thumbnails.of(tempFile.toFile())
	//					.size(MAX_WIDTH, MAX_HEIGHT)
	//					.outputFormat("jpg")
	//					.toFile(targetFile.toFile());
	//
	//			result.setErrors(errors);
	//			result.setFileName(fileName);
	//
	//			return result;
	//
	//		} catch (IOException e) {
	//			throw new ImageRegisterException("画像の登録処理に失敗しました", e);
	//
	//		} finally {
	//			// 一時ファイル削除する.
	//			if (tempFile != null) {
	//				try {
	//					// deleteIfExists()で一時ファイルを削除する(もしファイルがあれば削除し,なければ何もしないメソッド).
	//					Files.deleteIfExists(tempFile);
	//					log.info("一時ファイル削除成功: {}", tempFile);
	//				} catch (IOException e) {
	//					throw new TempFileDeleteException("一時ファイルの削除に失敗しました", e);
	//				}
	//			}
	//		}
	//	}

	// 商品の画像情報を更新する.
	@Override
	public void updateProductImage(MProduct product, MProduct productImageEdit) {

		try {
			// 既存画像を削除する.
			if (product.getProductImage() != null) {
				Path oldFile = Path.of(uploadDir, product.getProductImage());
				if (Files.exists(oldFile)) {
					Files.delete(oldFile);
				}
			}

			// 商品の画像情報を更新する.
			productMapper.updateProductImage(productImageEdit);

		} catch (IOException e) {
			throw new ImageUpdateException("画像の更新処理に失敗しました", e);
		}
	}

	// ==========================================
	//    privateメソッド.
	// ==========================================

	// ファイル名の拡張子を取得する("."は除く). 
	private String getExtension(String filename) {
		// 0始まりで.のインデックスを取得する("."がないときは-1になる).
		int dotIndex = filename.lastIndexOf('.');

		// ファイル名の"."以降を取得する(substringの開始位置をdotIndexに１足すことで"."を除く拡張子を取得できる).
		return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
	}
}