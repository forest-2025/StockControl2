package com.example.domain.products.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.domain.customers.model.MCustomer;
import com.example.domain.product.model.TTransactionHistory;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.model.ProductWithSupplier;

public interface ProductCountService {

	// 共通処理.
	
	/**　商品IDから商品情報を取得する(削除済みは除く).　*/
	public MProduct getOneProduct(Integer productId);

	/** 商品のIDから商品情報と入荷先情報を取得する(削除済みは除く). */ // 入荷画面に遷移する際や、入力後のバリデーション時に使用.
	public ProductWithSupplier getOneProductWithSupplier(Integer productId);

	// 入荷処理.
	/** ログイン中のユーザーのメールアドレスからユーザーIDを取得する. */ // 入荷履歴を更新するため.
	public Integer getUserId(UserDetails userDetails);

	/** 入荷した商品の在庫数を増やし、履歴を登録する. */
	public void processArrival(TTransactionHistory transactionHistory);
	
	// 出荷処理.
	/** 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する */ // 出荷フォームに出荷先名を渡すため.
	public List<MCustomer> getCustomerList(); 
	
	/** 出荷先が登録されているか(また,削除済みでないかを)出荷先IDで検索する. */
	public MCustomer getCustomer(Integer customerId);
	
	/** 商品IDから商品の在庫情報を取得し、在庫数のみ返す. */ // 出荷数が在庫数を越えていないか確認するため.
	public Integer getOneStockQuantity(Integer productId);
	
	/** 出荷した商品の在庫数を増やし、履歴を登録する. */
	public void processShip(TTransactionHistory transactionHistory);
	
	// 修正処理.
	/** 在庫の修正で増減した在庫数を調整し、履歴を登録する. */
	public void processEdit(TTransactionHistory transactionHistory);
}