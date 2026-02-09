package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.ProductList;

/**
 * 商品一覧の情報を取得できる Mapper.
 * m_product ・ m_supplier ・ t_stock テーブルのうち,商品一覧画面に必要なカラムを結合している.
 * 読み取り専用で DB への更新は行わない.
 * 
 */
@Mapper
public interface ProductListMapper {
	
	/** 
	 * 削除済み以外の商品一覧を商品番号の昇順で取得する.
	 * 
	 * @return 商品一覧.
	 */
	public List<ProductList> findAll();
	
	/** 
	 * 削除済み以外の商品検索結果一覧商品番号の昇順で取得する.
	 * 
	 * @param search 検索語句.
	 * @return 商品一覧.
	 */
	public List<ProductList> findSearchResults(String search);
	
	/** 
	 * 削除済み以外の商品IDから商品情報を商品番号の昇順で取得する.
	 * 
	 * @param productId 取得する商品のID.
	 * @return 商品一覧.
	 */
	public ProductList findByProductId(Integer productId);


}
