package com.example.domain.products.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.domain.customers.model.MCustomer;
import com.example.domain.products.model.MProduct;
import com.example.dto.products.ProductWithSupplier;
import com.example.dto.products.TTransactionHistory;

/**
 * 商品の数量の増減に関する業務処理を提供するサービス.
 * 
 */
public interface ProductCountService {

	/** 
	 * 商品IDから商品情報を取得する(削除済みは除く). 
	 * 
	 * @param productId 商品情報を取得する商品ID.
	 * @return 取得した商品情報.
	 */
	public MProduct getOneProduct(Integer productId);

	/** 
	 * 商品のIDから商品情報と入荷先情報を取得する(削除済みは除く).
	 * 入荷画面に遷移する際や、入力後のバリデーション時に使用.
	 * 
	 * @param productId 商品情報と入荷先情報を取得する商品ID.
	 * @return 取得した商品情報と入荷先情報.
	 */
	public ProductWithSupplier getOneProductWithSupplier(Integer productId);

	/** 
	 * ログイン中のユーザーのメールアドレスからユーザーIDを取得する.
	 * (履歴で誰が処理したかわかるようにするため).
	 * 
	 * @param userDetails ログイン中のユーザーの情報.
	 * @return ログイン中のユーザーのユーザーID.
	 */
	public Integer getUserId(UserDetails userDetails);

	/**
	 *  入荷した商品の在庫数を増やし,履歴を登録する.
	 *  
	 *  @param transactionHistory 登録する入荷履歴.
	 */
	public void processArrival(TTransactionHistory transactionHistory);
	
	/** 
	 * 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する.
	 *
	 * @return 出荷先一覧.
	 */ 
	public List<MCustomer> getCustomerList(); 
	
	/** 
	 * 出荷先が登録されていて,削除済みでないかを出荷先IDで検索して確認する.
	 * 
	 * @param customerId 検索する出荷先ID.
	 * @return 出荷先が存在して,かつ削除されていないなら true, 存在していないか削除済みなら false.
	 */
	public boolean existsByCustomerId(Integer customerId);
	
	/** 
	 * 商品IDから商品の在庫情報を取得し,在庫数のみ返す.
	 * (出荷数が在庫数を越えていないか確認するため.)
	 * 
	 * @param productId 取得する在庫情報の商品ID.
	 * @return 商品の在庫数.
	 */
	public Integer getOneStockQuantity(Integer productId);
	
	/** 
	 * 出荷した商品の在庫数を増やし,履歴を登録する.
	 * 
	 * @param transactionHistory 登録する出荷履歴.
	 */
	public void processShip(TTransactionHistory transactionHistory);
	
	/** 
	 * 在庫の修正で増減した在庫数を調整し,履歴を登録する.
	 * 
	 * @param transactionHistory 登録する修正履歴.
	 */
	public void processEdit(TTransactionHistory transactionHistory);
}