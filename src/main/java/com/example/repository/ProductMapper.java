package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.products.model.MProduct;

@Mapper
public interface ProductMapper {
	
	/**  商品番号から商品情報を取得する. */
	public MProduct findByProductNumber(String productNumber);
	
	/** 商品情報を登録する. */
	public void insertOne(MProduct product);
	
	/** 商品IDから商品情報を取得する(削除済みは除く). */
	public MProduct findByProductId(Integer productId);
	
	/** 商品情報(商品番号・商品名・入荷先・削除フラグ)を更新する. */
	public void updateOne(MProduct product);
	
	/** 商品情報(削除フラグ)を更新する. */
	public void updateIsDeleted(MProduct product);
	
	/** 商品の画像情報を更新する. */
	public void updateProductImage(MProduct product);
	
}
