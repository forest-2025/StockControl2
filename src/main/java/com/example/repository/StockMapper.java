package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.product.model.TStock;

@Mapper
public interface StockMapper {

	/** 商品の在庫情報を登録する. */
	public void insertOne(TStock stock);

	/** 商品の在庫数を更新する.  */
	public void updateStockQuantity(Integer productId, Integer amountOfChange);

	/** 商品IDから在庫情報を取得する. */
	public TStock findByProductId(Integer productId);
}
