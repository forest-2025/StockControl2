package com.example.domain.products.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.domain.product.model.HistoryDetails;
import com.example.domain.products.dto.UploadResult;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.model.ProductList;
import com.example.domain.products.model.ProductWithSupplier;
import com.example.domain.suppliers.model.MSupplier;

public interface ProductInfoService {

	// 商品一覧・商品検索.
	/** 削除済み以外の商品一覧を商品番号の昇順で取得する. */
	public List<ProductList> getProductList();

	/** 削除済み以外の商品検索結果一覧を商品番号の昇順で取得する. */
	public List<ProductList> getSearchProductList(String search);

	// 商品情報の登録・修正・削除.
	/** 入荷先一覧取得する. */
	public List<MSupplier> getAllSupplier();

	/** 入荷先が登録されてるか確認する(削除済みは除く).  */
	public boolean isRegister(Integer supplierId);

	/** 商品番号の重複がないか確認する. */ // 入力後のバリデーション時に使用する.
	public boolean isNotDuplicateProductNumber(String productNumber);

	/** 商品番号から商品IDを取得する.*/ // 商品登録後に在庫数を0に設定するため.
	//public Integer getProductId(String productNumber);

	/** 商品情報を登録する. */
	public void registerProduct(MProduct product);

	/** 商品のIDから商品情報と入荷先情報を取得する(削除済みは除く). */ // 商品情報修正画面に遷移する際や、入力後のバリデーション時に使用.
	public ProductWithSupplier getOneProductWithSupplier(Integer productId);

	/** 商品情報(商品番号・商品名・入荷先)を更新する. */
	public void updateProduct(MProduct product);

	/**　商品IDから商品情報を取得する(削除済みは除く)　*/
	public MProduct getOneProduct(Integer productId);

	/** 商品情報(削除フラグ)を更新する. */
	public void updateIsDeleted(MProduct product);

	// 商品の詳細情報.
	/** 削除済み以外の商品IDから商品情報を商品番号の昇順で取得する. */
	public ProductList getOneItemInTheList(Integer productId);

	/** 商品IDからその商品の履歴を降順で取得する. */
	public List<HistoryDetails> getHistoryForOneProduct(Integer productId);
	
	/** 商品画像のバリデーションチェックとローカルファイルストレージ(プロジェクト直下)に保存する. */
	public UploadResult validateAndUpload(MultipartFile file);

	/** 商品の画像情報を更新する. */
	public void updateProductImage(MProduct product);
	
}
