package com.example.domain.products.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.product.model.HistoryDetails;
import com.example.domain.product.model.TStock;
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

import net.coobird.thumbnailator.Thumbnails;

@Service
@Transactional
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

	/** 商品の画像情報を更新する. */
	@Override
	public void updateProductImage(MProduct product) {
		// ローカルファイルストレージ保存(プロジェクト直下に保存)している画像ファイルを削除する.


	}

	/** 商品画像のバリデーションチェックをする.　
	  * @throws Exception */
	public String checkProductImage(MultipartFile file, String uniqueName, BindingResult bindingResult) throws Exception{

		/* InputStreamクラスは,バイト単位でデータを読み込むための抽象クラス.
		 * MultipartFileはインターフェースで,HTTPでアップロードされたファイルのことで,
		 * ファイル内容をバイトデータとして持っている.
		 * そのバイトデータをInputStreamを通して順次読み込む.
		 * つまりInputStreamはバイトデータを読み込む窓口のようなもので,
		 * InputStream inputStream = new BufferedInputStream(file.getInputStream())で,
		 * ファイルのデータを読み込む窓口（InputStreamオブジェクト）を生成しているだけでMultipartFileのバイトデータをBufferedInputStreamで扱う準備が整えている.
		 * BufferedInputStreamでラップすることで,元の InputStream をそのまま読み込むより便利になる.
		 * (内部にバッファがあるので少しずつではなくまとめて読み込めることで速くなる.
		 * またmark/resetに対応しているため,ファイルを少し読み込むとファイルの最初に戻れずにまた読み込みたいときにオブジェクトを破棄して再取得するということをしなくていい).
		 * tryの()はリソース宣言でリソース(InputStream)を安全に自動で閉じるために変数宣言＋初期化をおこなっている.
		 * (tryブロック終了後inputStream.close();を自動的に呼ぶように設定している).
		 * ここで宣言された変数のこと(inputStream)をリソースオブジェクトという.
		 * Javaのプログラムの中で外部との接続を行うとき,必ずその対象と接続して様々な処理をするためのオブジェクトを利用する.
		 * 処理が終了したらそのオブジェクト（接続）を閉じ（解放）なければならない.
		 * (ファイルの場合には接続したままでは他のプログラムが同じファイルを開くことが出来ないということが起こったり,接続できる上限数に達してしまって,
		 * 接続ができなくなるといったエラーになることがある). */
		try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {

			/* 選択された画像ファイルの大きさが20MB以内か確認する(20MBも大丈夫).
			 * 計算の単位はバイトなので注意する(1KB = 1024 バイト, 1MB = 1024KB = 1024 * 1024 バイト, 20MB = 20 * 1024 * 1024 バイト).
			 * 20MBより大きければエラーとエラーメッセージを追加する. */
			long maxSize = 20 * 1024 * 1024;
			if (file.getSize() > maxSize) {
				bindingResult.rejectValue("multipartFile", "OverSize");
			}

			// 画像の形式がJPEGかApache Tikaを使用して確認する（MIMEタイプをString型取得してから確認する）.
			Tika tika = new Tika();
			String mimeType = tika.detect(inputStream);

			// JPEGのMINEタイプ"image/jpeg"とおなじか確認し,違うときはエラーとエラーメッセージを追加する.
			if (!mimeType.equals("image/jpeg")) {
				bindingResult.rejectValue("productFile", "FileFormatsDiffer");
			}
			System.out.println(bindingResult);

			// エラーがあれば登録フォームに戻るため,保存先ディレクトリの確認や画像のリサイズの前に確認する.
			if (!bindingResult.hasErrors()) {

				/* Fileクラスは「ファイルやディレクトリのパス情報」を表すオブジェクト.
				 * 保存先のディレクトリ(ファイルを入れるフォルダのようなもの)のパス情報をもつオブジェクトを作成し保存先のディレクトリが存在するかどうかを確認し,
				 * 存在しなければディレクトリを作成する. */
				File dir = new File(uploadDir);
				if (!dir.exists()) { // java.lang.SecurityException(非チェック例外).
					dir.mkdirs(); // java.lang.SecurityException(非チェック例外).
				}

				/* UUIDでユニーク名を生成する(ユニバーサル・ユニーク・アイデンティファイアとは、全世界で重複しないように設計された128ビット長の一意な識別子(ID)のこと). 
				 * UUID.randomUUID()でランダムな一意のIDを取得し,toString()で文字列化したものにJPEGの拡張子をつけることで,
				 * 一意のファイル名を作成できる. */
				uniqueName = UUID.randomUUID().toString() + ".jpg";

				// 保存したいディレクトリパスと一意の名前にした保存したい画像ファイル名を組み合わせたFileクラスのオブジェクトを作成する.
				File dest = new File(uploadDir, uniqueName); // java.lang.NullPointerException(非チェック例外).

				/* 画像をリサイズする（最大幅800px, 最大高さ600px）.
				 * Thumbnails.of(in)でinputStreamを通してバイトデータを読み込む. */
				Thumbnails.of(inputStream) // NullPointerException/IllegalArgumentException/IOException(チェック例外).
						.size(800, 600) // このメソッドを何回も呼んだり,このメソッドの後にscale(double)メソッド(拡大縮小率の設定ができるメソッド)を呼ばなければ例外なし). 
						.toFile(dest); // 画像をリサイズしたものを作成し,Fileクラスのオブジェクトのディレクトリとファイル名でサーバ上に画像を保存する(パソコンの元画像を消しても表示できる).
			}

		} catch (Exception e) {
			throw new Exception ("商品画像の確認と保存に失敗しました", e);
		}
		return uniqueName;
	}

}
