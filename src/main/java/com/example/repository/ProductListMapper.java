package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.ProductList;

@Mapper
public interface ProductListMapper {
	
	/** 削除済み以外の商品一覧を商品番号の昇順で取得する. */
	public List<ProductList> findAll();
	
	/** 削除済み以外の商品検索結果一覧商品番号の昇順で取得する. */
	public List<ProductList> findSearchResults(String search);
	
	/** 削除済み以外の商品IDから商品情報を商品番号の昇順で取得する. */
	public ProductList findByProductId(Integer productId);


}
