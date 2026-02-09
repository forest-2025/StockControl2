package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.ProductWithSupplier;

/**
 * 商品情報と入荷先情報を取得できる Mapper.
 * m_product と m_supplier テーブルを結合している.
 * 読み取り専用で DB への更新は行わない.
 * 
 */
@Mapper
public interface ProductWithSupplierMapper {
	
	/** 
	 * 商品IDから商品情報と入荷先情報を取得する(削除済みは除く). 
	 * 
	 * @param　productId 取得する商品のID.
	 * @return 商品情報と入荷先情報.*/
	public ProductWithSupplier findByProductId(Integer productId);

}
