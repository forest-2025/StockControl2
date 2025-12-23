package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.products.model.ProductWithSupplier;

@Mapper
public interface ProductWithSupplierMapper {
	
	/** 商品IDから商品情報と入荷先情報を取得する(削除済みは除く). */
	public ProductWithSupplier findByProductId(Integer productId);

}
