package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.HistoryDetails;
import com.example.dto.products.TTransactionHistory;

@Mapper
public interface TransactionHistoryMapper {
	
	/** 入荷・出荷・修正履歴を登録する. */
	public void insertOne(TTransactionHistory transactionHistory);
	
	/** 商品IDから履歴を降順で取得する. */
	public List<HistoryDetails> findByProductId(Integer productId);

}
