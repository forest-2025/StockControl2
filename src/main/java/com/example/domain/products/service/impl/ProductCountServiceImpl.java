package com.example.domain.products.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.customers.model.MCustomer;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.service.ProductCountService;
import com.example.domain.users.model.MUser;
import com.example.dto.products.ProductWithSupplier;
import com.example.dto.products.TStock;
import com.example.dto.products.TTransactionHistory;
import com.example.repository.CustomerMapper;
import com.example.repository.ProductMapper;
import com.example.repository.ProductWithSupplierMapper;
import com.example.repository.StockMapper;
import com.example.repository.TransactionHistoryMapper;
import com.example.repository.UserMapper;

/**
 * ProductCountService の実装クラス.
 * 
 */
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

	// 商品IDから商品情報を取得する(削除済みは除く). 
	@Override
	public MProduct getOneProduct(Integer productId) {
		MProduct product = productMapper.findByProductId(productId);
		return product;
	}
	
	// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く).
	@Override
	public ProductWithSupplier getOneProductWithSupplier(Integer productId) {
		
		ProductWithSupplier productWithSupplier = productWithSupplierMapper.findByProductId(productId);
		return productWithSupplier;
		
	}

	// ログイン中のユーザーのメールアドレスからユーザーIDを取得する.
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

	// 入荷した商品の在庫数を増やし,履歴を登録する.
	@Override
	public void processArrival(TTransactionHistory transactionHistory) {

		//  商品IDとamountOfChange(入荷数)を取得する(フォーム入力されたformクラスの値はエンティティクラスTTransactionHistoryに変換してある).
		Integer productId = transactionHistory.getProductId();
		Integer amountOfChange = transactionHistory.getAmountOfChange();

		// 商品IDとamountOfChange(入荷数)から商品IDの商品の在庫をamountOfChangeの数量分増加させる.
		stockMapper.updateStockQuantity(productId, amountOfChange);

		// 履歴を登録する.
		transactionHistoryMapper.insertOne(transactionHistory);

	}

	// 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する.
	@Override
	public List<MCustomer> getCustomerList() {
		
		List<MCustomer> customerList = customerMapper.findAllInAscById();

		return customerList;
	}

	// 出荷先が登録されていて,削除済みでないかを出荷先IDで検索して確認する.
	public boolean existsByCustomerId(Integer customerId) {
		MCustomer customer = customerMapper.findByCustomerId(customerId);
		if(customer == null) {
			return false;
		}
			return true;
	}
	
	// 商品IDから商品の在庫情報を取得し,在庫数のみ返す.
	@Override
	public Integer getOneStockQuantity(Integer productId) {
		TStock stock = stockMapper.findByProductId(productId);
		Integer stockQuantity = stock.getStockQuantity();
		return stockQuantity;
	}

	// 出荷した商品の在庫数を減らし,履歴を登録する.
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

	// 在庫の修正で増減した在庫数を調整し,履歴を登録する.
	@Override
	public void processEdit (TTransactionHistory transactionHistory) {

		// 商品IDとamountOfChange(在庫の修正数)を取得する(フォーム入力されたformクラスの値はTTransactionHistoryに変換してある).
		Integer productId = transactionHistory.getProductId();
		Integer amountOfChange = transactionHistory.getAmountOfChange();

		// 商品IDとamountOfChange(在庫の修正数)から商品IDの商品の在庫をamountOfChangeの数量分調整する.
		stockMapper.updateStockQuantity(productId, amountOfChange);
			
		// 履歴を登録する.
		transactionHistoryMapper.insertOne(transactionHistory);

	}

	
}
