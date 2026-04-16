package com.example.domain.products.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.domain.products.model.MProduct;
import com.example.domain.suppliers.model.MSupplier;
import com.example.dto.products.HistoryDetails;
import com.example.dto.products.ProductList;
import com.example.dto.products.ProductWithSupplier;
import com.github.pagehelper.PageInfo;

/**
 * 商品情報に関する業務処理を提供するサービス.
 * 
 */
public interface ProductInfoService {

	/** 
	 * 商品番号を指定された並べ替え順序（昇順または降順）に基づいて並び替えた商品一覧を取得する.
	 *
	 * @param search 検索語句.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 商品一覧のページ情報.
	 */
	public PageInfo<ProductList> findAllSorted(String search, String sort, int page);

	/** 
	 * 削除済み以外の商品一覧を商品番号の昇順でページングして取得する.
	 *
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 商品一覧のページ情報.
	 */
	public PageInfo<ProductList> getProductList(int page);

	/** 
	 * 削除済み以外の商品一覧から検索語句が商品番号・商品名・入荷先名・入荷先ふりがなと一致する商品を,
	 * 商品番号の昇順でページングして取得する.
	 * 
	 * @param search 検索語句.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 商品一覧のページ情報.
	 */
	public PageInfo<ProductList> getSearchProductList(String search, String sort, int page);

	/** 
	 * 商品のIDから商品情報と入荷先情報を取得する(削除済みは除く).
	 * 商品情報修正画面に遷移する際や、入力後のバリデーション時に使用する.
	 * 
	 * @param productId 情報を取得する商品ID.
	 * @return 取得した商品情報と入荷先情報.
	 */
	public ProductWithSupplier getOneProductWithSupplier(Integer productId);

	/** 
	 * 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する.
	 * 
	 * @return 入荷先一覧.
	 */
	public List<MSupplier> getAllSupplier();

	/** 
	 * 入荷先IDから入荷先が DB に登録されてるか確認する(削除済みは除く).
	 * 
	 * @param supplierId 登録されているか確認する入荷先ID.
	 * @return 	登録されていれば true.
	 * 			登録されていなければ false.
	 */
	public boolean isRegister(Integer supplierId);

	/** 
	 * 指定した商品番号と重複するデータが存在するか判別する.
	 * 登録時はすべてのレコードを対象とし,更新時は商品IDで商品自身を除外して確認する.
	 * 
	 * @param productIdValue 商品ID.
	 * @param productNumberValue 商品番号.
	 * @return 重複がなければ true,あれば false.
	 */
	public boolean isNotDuplicates(Object productIdValue, Object productNumberValue);

	/** 
	 * 商品情報を登録する. 
	 * 
	 * @param product 登録する商品情報.
	 */
	public void registerProduct(MProduct product);

	/** 
	 * 商品情報(商品番号・商品名・入荷先)を更新する. 
	 * 
	 * @param product 更新する商品情報.
	 */
	public void updateProduct(MProduct product);

	/**
	 * 商品IDから商品情報を取得する(削除済みは除く).
	 * 
	 * @param productId 商品情報を取得する商品ID.
	 * @return 取得した商品情報.
	 */
	public MProduct getOneProduct(Integer productId);

	/** 
	 * 削除フラグを更新する.
	 * 商品情報の削除は物理削除ではなく論理削除のため削除フラグを1に設定する.
	 * 商品画像は物理削除するため,nullに設定する.
	 * 
	 * @param product 更新する商品情報.
	 */
	public void updateIsDeleted(MProduct product) throws IOException;

	/** 
	 * 商品IDから商品情報を商品番号の昇順で取得する(削除済みは除く). 
	 * 
	 * @param productId 商品情報を取得する商品ID.
	 * @return 取得した商品情報一覧.
	 */
	public ProductList getOneItemInTheList(Integer productId);

	/** 
	 * 商品IDからその商品の履歴を降順でページングして取得する. 
	 *
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @param productId 取得する商品履歴の商品ID.
	 * @return 履歴一覧のページ情報.
	 */
	public PageInfo<HistoryDetails> getHistoryForOneProduct(int page, Integer productId);

	/**
	 * 商品画像のサイズが20MBを越えていないかを確認する.
	 * 
	 * @param file アップロードされた商品画像.
	 * @return	有効なファイルサイズなら true,そうでなければ false.
	 */
	public boolean isValidFileSize(MultipartFile file);

	/**
	 * 商品画像が JPEG かを確認する.
	 * 
	 * @param file アップロードされた商品画像.
	 * @return	JPEGなら true,そうでなければ false.
	 */
	public boolean isAllowedExtension (MultipartFile file);
	
	/** 
	 * 商品画像をバリデーションチェックする. 
	 * 
	 * @param file アップロードされた商品画像.
	 * @param errors 商品画像のバリデーションエラーメッセージのリストを保持するオブジェクト.
	 * @return 	バリデーションエラーメッセージを保持するオブジェクト.
	 *			バリデーションエラーがない場合,errors は nullが格納される.
	 */
	//public List<String> validateImage(MultipartFile file, List<String> errors);

	/** 
	 * 商品画像をローカルファイルストレージ(プロジェクト直下)に保存する. 
	 * 
	 * @param file アップロードされた商品画像.
	 * @return 	画像ファイルの一意にしたファイル名.
	 * @throws IOException 画像ファイルの保存処理で例外が発生した場合. 
	 */
	public String uploadImage(MultipartFile file) throws IOException;

	/** 
	 * 商品の画像情報を更新する.
	 * 既存の画像は削除する(物理削除).
	 * 
	 * @param product 既存の商品情報(画像情報を削除するため必要).
	 * @param productImageEdit 更新する商品情報.
	 * @throws IOException 画像ファイルの削除処理で例外が発生した場合. 
	 */
	public void updateProductImage(MProduct product, MProduct productImageEdit) throws IOException;

}
