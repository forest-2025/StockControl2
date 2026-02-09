package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.HistoryDetails;
import com.example.dto.products.TTransactionHistory;

/**
 * 入出荷・修正履歴を扱う Mapper.
 * t_transaction_history テーブルの情報取得・登録を行う.
 * (履歴のため更新・削除の処理はない).
 * 
 */
@Mapper
public interface TransactionHistoryMapper {
	
	/** 
	 * 入荷・出荷・修正履歴を登録する.
	 * 
	 * @param  transactionHistory 登録する履歴.
	 */
	public void insertOne(TTransactionHistory transactionHistory);
	
	/** 
	 * 商品IDから履歴を降順で取得する.
	 * 
	 * @param productId 取得する商品のID.
	 * @return 入荷・出荷・修正履歴一覧.
	 */
	public List<HistoryDetails> findByProductId(Integer productId);

}
