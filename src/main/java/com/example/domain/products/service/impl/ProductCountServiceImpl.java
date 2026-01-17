package com.example.domain.products.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.customers.model.MCustomer;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.model.ProductWithSupplier;
import com.example.domain.products.service.ProductCountService;
import com.example.domain.users.model.MUser;
import com.example.dto.products.TStock;
import com.example.dto.products.TTransactionHistory;
import com.example.repository.CustomerMapper;
import com.example.repository.ProductMapper;
import com.example.repository.ProductWithSupplierMapper;
import com.example.repository.StockMapper;
import com.example.repository.TransactionHistoryMapper;
import com.example.repository.UserMapper;

@Service
@Transactional
public class ProductCountServiceImpl implements ProductCountService {

	@Autowired
	private StockMapper stockMapper;

	@Autowired
	private TransactionHistoryMapper transactionHistoryMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private CustomerMapper customerMapper;
	
	@Autowired
	private ProductWithSupplierMapper productWithSupplierMapper;
	
	@Autowired
	private ProductMapper productMapper;

	// 共通処理.
	/** 商品IDから商品情報を取得する(削除済みは除く). */
	@Override
	public MProduct getOneProduct(Integer productId) {
		MProduct product = productMapper.findByProductId(productId);
		return product;
	}
	
	/** 商品IDから商品情報と入荷先情報を取得する(削除済みは除く). */ // 入荷画面に遷移する際や、入力後のバリデーション時に使用.
	@Override
	public ProductWithSupplier getOneProductWithSupplier(Integer productId) {
		
		ProductWithSupplier productWithSupplier = productWithSupplierMapper.findByProductId(productId);
		return productWithSupplier;
		
	}

	// 入荷処理.
	/** ログイン中のユーザーのメールアドレスからユーザーIDを取得する. */	// 入荷履歴を更新するため、.
	@Override
	public Integer getUserId(UserDetails userDetails) {

		// ログイン中のユーザーのメールアドレス取得する.
		String emailAddress = userDetails.getUsername();
		
		// メールアドレスからユーザー情報を取得する(削除済みは除く).
		MUser user = userMapper.findByEmailAddress(emailAddress);
		
		// ユーザー情報からユーザーIDを取得する.
		Integer userId = user.getUserId();

		return userId;
	}

	/** 入荷した商品の在庫数を増やし、履歴を登録する. */
	@Override
	public void processArrival(TTransactionHistory transactionHistory) {

		//  商品IDとamountOfChange(入荷数)を取得する(フォーム入力されたformクラスの値はエンティティクラスTTransactionHistoryに変換してある).
		Integer productId = transactionHistory.getProductId();
		Integer amountOfChange = transactionHistory.getAmountOfChange();

		// 商品IDとamountOfChange(入荷数)から商品IDの商品の在庫をamountOfChangeの数量分増加させる.
		stockMapper.updateStockQuantity(productId, amountOfChange);

		// 入荷履歴に登録する.
		transactionHistoryMapper.insertOne(transactionHistory);

	}

	// 出荷処理.
	/** 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する. */ // 出荷フォームに出荷先名を渡すため.
	@Override
	public List<MCustomer> getCustomerList() {
		
		List<MCustomer> customerList = customerMapper.findAllInAscById();

		return customerList;
	}

	/** 出荷先が登録されているか(また,削除済みでないかを)出荷先IDで検索する. */ // 出荷確定後のバリデーションで使用.
	@Override
	public MCustomer getCustomer(Integer customerId) {

		MCustomer customer = customerMapper.findByCustomerId(customerId);

		return customer;
	}
	
	/** 商品IDから商品の在庫情報を取得し、在庫数のみ返す. */ // 出荷数が在庫数を越えていないか確認するため.
	@Override
	public Integer getOneStockQuantity(Integer productId) {
		TStock stock = stockMapper.findByProductId(productId);
		Integer stockQuantity = stock.getStockQuantity();
		return stockQuantity;
	}

	/** 出荷した商品の在庫数を減らし、履歴を登録する. */
	@Override
	public void processShip(TTransactionHistory transactionHistory) {

		// 商品IDとamountOfChange(出荷数)を取得する(フォーム入力されたformクラスの値はエンティティクラスTTransactionHistoryに変換してある).
		Integer productId = transactionHistory.getProductId();
		Integer amountOfChange = transactionHistory.getAmountOfChange();

		/* 出荷は減少するのでamountOfChangeを負の数にする.
		 * (updateStockQuantit()では stock_quantity + #{amountOfChange}としているため在庫の減少はamountOfChangeを負の数にしないと減らせない). */
		amountOfChange = -amountOfChange;

		// 商品IDとamountOfChange(出荷数)から商品IDの商品の在庫をamountOfChangeの数量分減少させる.
		stockMapper.updateStockQuantity(productId, amountOfChange);
			
		// amountOfChangeを設定して履歴を登録する(履歴で負の数で管理する).
		transactionHistory.setAmountOfChange(amountOfChange);
		transactionHistoryMapper.insertOne(transactionHistory);

	}

	// 修正処理.
	/** 修正した商品の在庫数を調整し、履歴を登録する. */
	@Override
	public void processEdit (TTransactionHistory transactionHistory) {

		// 商品IDとamountOfChange(在庫の修正数)を取得する(フォーム入力されたformクラスの値はエンティティクラスTTransactionHistoryに変換してある).
		Integer productId = transactionHistory.getProductId();
		Integer amountOfChange = transactionHistory.getAmountOfChange();

		// 商品IDとamountOfChange(在庫の修正数)から商品IDの商品の在庫をamountOfChangeの数量分調整する.
		stockMapper.updateStockQuantity(productId, amountOfChange);
			
		// 履歴を登録する.
		transactionHistoryMapper.insertOne(transactionHistory);

	}

	
}
