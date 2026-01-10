package com.example.domain.products.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		List<String> errors = new ArrayList<>();
		Path tempFile = null;
		String savedUuid = null;
		
		try {
			// ----------------------
			// 基本バリデーション
			// ----------------------
			if (file.isEmpty())
				errors.add("ファイルが選択されていません");
			if (file.getSize() > MAX_SIZE)
				errors.add("ファイルサイズが20MBを超えています");

			String filename = file.getOriginalFilename();
			if (filename == null || filename.isBlank()) {
				errors.add("ファイル名が不正です");
			} else {
				String ext = getExtension(filename).toLowerCase();
				boolean allowed = false;
				for (String a : ALLOWED_EXTENSIONS) {
					if (a.equals(ext)) {
						allowed = true;
						break;
					}
				}
				if (!allowed)
					errors.add("許可されていないファイル形式です（JPEGのみ）");
			}

			if (!errors.isEmpty()) {
				return new UploadResult(errors,null);
			}
			// ----------------------
			// 一時ファイル作成
			// ----------------------
			tempFile = Files.createTempFile("upload-", ".jpg");
			file.transferTo(tempFile.toFile());

			// ----------------------
			// MIMEタイプチェック
			// ----------------------
			String mimeType = tika.detect(tempFile.toFile());
			if (!mimeType.equalsIgnoreCase("image/jpeg")) {
				errors.add("画像ファイルではありません（JPEGのみ）");
			}

			// ----------------------
			// ImageIOで読み込めるか
			// ----------------------
			if (ImageIO.read(tempFile.toFile()) == null) {
				errors.add("画像ファイルとして読み込めません");
			}

			if (!errors.isEmpty())
				return new UploadResult(errors,null);

			// ----------------------
			// 保存ディレクトリ作成
			// ----------------------
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// ----------------------
			// UUIDで保存 (.jpg固定)
			// ----------------------
			savedUuid = UUID.randomUUID() + ".jpg";
			Path targetFile = Paths.get(UPLOAD_DIR, savedUuid);

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
					Files.deleteIfExists(tempFile);
					log.debug("一時ファイル削除成功: {}", tempFile);
				} catch (IOException e) {
					// 小規模・テストサーバーなので警告ログだけ
					log.warn("一時ファイル削除失敗: {}", tempFile, e);
				}
			}
		}

	   return new UploadResult(errors,savedUuid );
	}

	private String getExtension(String filename) {
		int dotIndex = filename.lastIndexOf('.');
		return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
	}

	public List<String> validateAndSaveImage(Long id, MultipartFile file) {
		List<String> errors = new ArrayList<>();
		Path tempFile = null;
		String newFilename = null;

		//		ImageEntity existing = null;
		//		if (id != null) {
		//			existing = imageMapper.findById(id);
		//		}	idが存在してるか確認している

		try {
			// ----------------------
			// file がある場合
			// ----------------------
			if (file != null && !file.isEmpty()) {
				if (file.getSize() > MAX_SIZE) {
					errors.add("ファイルサイズが20MBを超えています");
				}

				String originalName = file.getOriginalFilename();
				if (originalName != null && !originalName.toLowerCase().endsWith(".jpg") &&
						!originalName.toLowerCase().endsWith(".jpeg")) {
					errors.add("JPEG画像のみ対応しています");
				}

				if (!errors.isEmpty())
					return errors;

				// 一時ファイル作成
				tempFile = Files.createTempFile("upload-", ".jpg");
				file.transferTo(tempFile.toFile());

				String mimeType = tika.detect(tempFile.toFile());
				if (!mimeType.equalsIgnoreCase("image/jpeg")) {
					errors.add("JPEG画像ではありません");
				}

				if (ImageIO.read(tempFile.toFile()) == null) {
					errors.add("画像として読み込めません");
				}

				if (!errors.isEmpty())
					return errors;

				Path uploadPath = Paths.get(UPLOAD_DIR);
				if (!Files.exists(uploadPath))
					Files.createDirectories(uploadPath);

				newFilename = UUID.randomUUID().toString() + ".jpg";
				Path targetFile = uploadPath.resolve(newFilename);

				Thumbnails.of(tempFile.toFile())
						.size(MAX_WIDTH, MAX_HEIGHT)
						.toFile(targetFile.toFile());

				// 既存画像削除
				if (existing != null && existing.getFilename() != null) {
					Path oldFile = Paths.get(UPLOAD_DIR, existing.getFilename());
					if (Files.exists(oldFile))
						Files.delete(oldFile);
				}

			}

			// ----------------------
			// DB登録／更新
			// ----------------------
			if (existing == null) {
				ImageEntity image = new ImageEntity();
				image.setFilename(newFilename);

				imageMapper.insert(image);
			} else {
				if (file == null && existing.getFilename() != null) {
					existing.setFilename(null); // 削除のみ
				}
				if (file != null) {
					existing.setFilename(newFilename);
				}
				imageMapper.update(existing);
			}

		} catch (Exception e) {
			errors.add("画像処理中にエラーが発生しました");
			log.error("画像処理エラー", e);
		} finally {
			if (tempFile != null) {
				try {
					Files.deleteIfExists(tempFile);
				} catch (Exception e) {
					log.warn("一時ファイル削除失敗: {}", tempFile, e);
				}
			}
		}

		return errors;
	}
}
