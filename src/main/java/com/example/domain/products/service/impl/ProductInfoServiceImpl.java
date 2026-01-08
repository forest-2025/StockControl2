package com.example.domain.products.service.impl;

import java.awt.Image;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	ProductWithSupplierMapper productWithSupplierMapper;

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
		Image image = imageRepository.findById(product.getProductId())
		        .orElseThrow(() -> new NotFoundException());

		    Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
		    Path target = base.resolve(image.getFileName()).normalize();

		    if (!target.startsWith(base)) {
		        throw new SecurityException("不正なファイルパス");
		    }

		    Files.deleteIfExists(target);
		productMapper.updateProductImage(product);
		
	}

	

}
