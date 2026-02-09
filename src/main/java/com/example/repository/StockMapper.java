package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.TStock;

/**
 * 在庫情報を扱う Mapper.
 * t_stock テーブルの情報取得・登録・更新を行う.
 * 
 */
@Mapper
public interface StockMapper {

	/** 
	 * 商品の在庫情報を登録する. 
	 * 
	 * @param stock 登録する在庫情報.
	 */
	public void insertOne(TStock stock);

	/** 
	 * 商品の在庫数を更新する.
	 * 
	 * @param productId 更新する商品のID.
	 * @param amountOfChange 商品の増減数.
	 */
	public void updateStockQuantity(Integer productId, Integer amountOfChange);

	/** 
	 * 商品IDから在庫情報を取得する.
	 * 
	 * @param productId 取得する商品のID.
	 * @return 在庫情報.
	 */
	public TStock findByProductId(Integer productId);
}
