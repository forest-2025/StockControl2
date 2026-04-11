package com.example.domain.products.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
import com.example.repository.ProductListMapper;
import com.example.repository.ProductMapper;
import com.example.repository.ProductWithSupplierMapper;
import com.example.repository.StockMapper;
import com.example.repository.SupplierMapper;
import com.example.repository.TransactionHistoryMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * ProductInfoService の実装クラス.
 * 
 */
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

	@Autowired
	private MessageSource messageSource;

	@Value("${file.upload-dir}")
	private String uploadDir;

	private static final String[] ALLOWED_EXTENSIONS = { "jpg", "jpeg" };

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
	public void updateIsDeleted(MProduct product) throws IOException {

		// 商品情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		product.setProductIsDeleted(1);

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
	public List<String> validateImage(MultipartFile file, List<String> errors) {

		// 選択したファイルのファイル名を取得する.
		String originalFileName = file.getOriginalFilename();

		// ファイル名がnullだったりなかったときはバリデーションエラーにする.
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
			// JPEGでなければバリデーションエラーにする.
			if (!allowed) {
				String errorMessage = messageSource.getMessage("FileFormatsDiffer", null, Locale.JAPAN);
				errors.add(errorMessage);
			}
		}
		return errors;

	}

	// 画像ファイルを登録する.
	public String uploadImage(MultipartFile file) throws IOException {

		String fileName = UUID.randomUUID().toString() + ".jpg";

		Path targetFile = Path.of(uploadDir, fileName);

		Files.createDirectories(targetFile.getParent());

		try (InputStream is = file.getInputStream()) {
			Files.copy(is, targetFile);

		}

		return fileName;

	}

	// 商品の画像情報を更新する.
	@Override
	public void updateProductImage(MProduct product, MProduct productImageEdit) throws IOException {

		// 既存画像を削除する.
		if (product.getProductImage() != null) {
			Path oldFile = Path.of(uploadDir, product.getProductImage());
			if (Files.exists(oldFile)) {
				Files.delete(oldFile);
			}
		}

		// 商品の画像情報を更新する.
		productMapper.updateProductImage(productImageEdit);

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